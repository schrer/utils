package at.schrer.utils.inject;

import at.schrer.utils.inject.annotations.Component;
import at.schrer.utils.structures.SomeAcyclicGraph;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The ContextBuilder class is used to scan a package for available components (annotated with @Component) and create and provide instances of them.
 * A contextbuilder instance can be made through the getContextInstance function. If a contextbuilder for a specific package was already created,
 * the same instance will be returned again when a getContextInstance is called with that same package name again.
 * So only one contextbuilder will be created per package and is then reused.
 *
 * Instances are created as singletons. This means an instance will be created only once. If the same component class is requested again, the previously created instance is returned again.
 * The ContextBuilder create instances of components lazily, only once they are requested (or needed as dependency for other components).
 *
 * If a package contains components with unsatisfiable dependencies (circular dependencies, no components without dependencies), getContextInstance method throws a ContextException.
 *
 */
public class ContextBuilder {

    private static final Map<String, ContextBuilder> loadedBuilders = new ConcurrentHashMap<>();

    private final Map<Class<?>, Object> componentInstances;

    private final SomeAcyclicGraph<ComponentBluePrint<Class<?>>> componentGraph;

    private ContextBuilder(String packagePath) throws ContextException {
        this.componentInstances = new HashMap<>();
        this.componentGraph = new SomeAcyclicGraph<>();

        final ClassScanner classScanner = new ClassScanner(packagePath);
        List<Class<?>> components = new ArrayList<>();
        try {
            components.addAll(classScanner.findByAnnotation(Component.class));
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new ContextException("Unable to load context.", e);
        }

        final Set<ComponentBluePrint<Class<?>>> bluePrints = new HashSet<>();
        for (Class<?> componentClass : components) {
            bluePrints.add((ComponentBluePrint<Class<?>>) new ComponentBluePrint<>(componentClass));
        }

        final List<ComponentBluePrint<Class<?>>> noArgBluePrints = bluePrints.stream()
                .filter(ComponentBluePrint::canBeDependencyLess)
                .toList();

        for (ComponentBluePrint<Class<?>> bluePrint : noArgBluePrints) {
            this.componentGraph.addNode(bluePrint);
            bluePrints.remove(bluePrint);
        }

        int lastRunResolved = this.componentGraph.size();
        while (!bluePrints.isEmpty() && lastRunResolved > 0) {
            Set<ComponentBluePrint<Class<?>>> addedToGraph = new HashSet<>();
            for (ComponentBluePrint<Class<?>> bluePrint : bluePrints) {
                Optional<List<ComponentBluePrint<Class<?>>>> dependencyNodes =
                        getSatisfiableDependencies(bluePrint);
                if (dependencyNodes.isPresent()) {
                    componentGraph.addNode(bluePrint, dependencyNodes.get());
                    addedToGraph.add(bluePrint);
                }
            }

            lastRunResolved = addedToGraph.size();

            addedToGraph.forEach(bluePrints::remove);
            addedToGraph.clear();
        }

        if (!bluePrints.isEmpty()) {
            throw new ContextException("Unable to resolve dependencies for " + bluePrints.size() + " components.");
        }

    }

    /**
     * Get an instance of a component class.
     * Either a new instance is created, or if an instance of the same component was requested previously, the same instance is returned again.
     *
     * @param componentClass the class of the component
     * @return an instance of the component
     * @param <T> the type of the component
     * @throws ContextException if the class is not known component within the context or cannot be created for other reasons.
     */
    public <T> T getComponent(Class<T> componentClass) throws ContextException {
        if (componentInstances.containsKey(componentClass)) {
            return ((T) componentInstances.get(componentClass));
        }

        Optional<ComponentBluePrint<Class<?>>> bluePrintOptional = componentGraph.find(it -> it.isSameClass(componentClass));
        if (bluePrintOptional.isEmpty()) {
            throw new ContextException("Class not in context: " + componentClass.getName());
        }

        var bluePrint = bluePrintOptional.get();
        try {
            T instance;
            if (bluePrint.canBeDependencyLess()) {
                instance = (T) bluePrint.getNoArgsInstance();
            } else {
                Set<ComponentBluePrint<Class<?>>> outbounds = componentGraph.getOutbounds(bluePrint);

                // TODO make this non-recursive
                Object[] dependencyInstances = outbounds.stream()
                        .map(ComponentBluePrint::getComponentClass)
                        .map(this::getComponent)
                        .toArray();
                instance = (T) bluePrint.getInstance(dependencyInstances);
            }
            componentInstances.put(componentClass, instance);
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ContextException("Failed to create instance of class " + componentClass.getName(), e);
        }
    }

    /**
     * Get a ContextBuilder instance for the given package.
     *
     * @param packagePath the package to scan for components.
     * @return a ContextBuilder instance, initialized on the given package.
     * @throws ContextException if an error happens while trying to load the components of the given package.
     */
    public static ContextBuilder getContextInstance(String packagePath) throws ContextException {
        if (loadedBuilders.containsKey(packagePath)) {
            return loadedBuilders.get(packagePath);
        }

        synchronized (loadedBuilders) {
            if (loadedBuilders.containsKey(packagePath)) {
                return loadedBuilders.get(packagePath);
            }
            ContextBuilder newBuilder = new ContextBuilder(packagePath);
            loadedBuilders.put(packagePath, newBuilder);
            return newBuilder;
        }
    }

    /**
     * Clears all instances of ContextBuilders, allowing to create new instances for already loaded packages.
     * This is meant for testing only, to allow a clean slate after each test.
     * It does not destroy ContextBuilder instances that are still referenced in other objects/scopes. They continue to function and are not closed.
     */
    public static void clearContextInstances(){
        loadedBuilders.clear();
    }

    /**
     * Searches for a list of classes present in the graph of components that already have all dependencies fulfilled.
     * If the blueprint has a constructor without arguments, an empty list is returned, as there are no dependencies needed.
     * If no constructor can be fully satisfied with the available blueprints in the component graph, an empty Optional is returned.
     *
     * @param bluePrint the component blueprint for which satisfiable dependencies should be found
     * @return an Optional containing a list of dependencies that can satisfy one of the constructors, or an empty Optional if there is no such list
     */
    private Optional<List<ComponentBluePrint<Class<?>>>> getSatisfiableDependencies(ComponentBluePrint<Class<?>> bluePrint){
        if (bluePrint.canBeDependencyLess()) {
            return Optional.of(List.of());
        }

        List<ComponentBluePrint.ComponentConstructor<Class<?>>> constructors = bluePrint.getConstructors();
        for (ComponentBluePrint.ComponentConstructor<Class<?>> constructor : constructors) {
            var result = resolveConstructorDependencies(constructor);
            if(result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    private Optional<List<ComponentBluePrint<Class<?>>>> resolveConstructorDependencies(
            ComponentBluePrint.ComponentConstructor<Class<?>> constructor
    ){
        List<Class<?>> dependencies = constructor.getDependencies();
        List<ComponentBluePrint<Class<?>>> deps = dependencies.stream()
                .map(dep -> componentGraph.find(
                        it -> it.isSameClass(dep)
                ).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        if (deps.size() == dependencies.size()) {
            return Optional.of(deps);
        }
        return Optional.empty();
    }
}
