package at.schrer.inject;

import at.schrer.inject.annotations.Component;
import at.schrer.structures.SomeAcyclicGraph;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * The ContextBuilder class is used to scan a package for available components (annotated with @Component) and create and provide instances of them.
 * A contextbuilder instance can be made through the getContextInstance function. If a contextbuilder for a specific package was already created,
 * the same instance will be returned again when a getContextInstance is called with that same package name again.
 * So only one contextbuilder will be created per package and is then reused.
 *
 * If a package contains components with unsatisfiable dependencies (circular dependencies, no components without dependencies), getContextInstance method throws a ContextException.
 *
 */
public class ContextBuilder {

    private static final Map<String, ContextBuilder> loadedBuilders = new HashMap<>();

    private final List<Class<?>> components;
    private final Map<Class<?>, Object> componentInstances;

    private final SomeAcyclicGraph<ComponentBluePrint<Class<?>>> componentGraph;

    private ContextBuilder(String packagePath) throws ContextException {
        this.components = new ArrayList<>();
        this.componentInstances = new HashMap<>();
        this.componentGraph = new SomeAcyclicGraph<>();

        final ClassScanner classScanner = new ClassScanner(packagePath);
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
            List<ComponentBluePrint<Class<?>>> deps = new ArrayList<>();
            List<Class<?>> dependencies = constructor.getDependencies();
            for (Class<?> dep : dependencies) {
                Optional<ComponentBluePrint<Class<?>>> depNode =
                        componentGraph.find(it -> it.isSameClass(dep));
                if(depNode.isEmpty()) {
                    break;
                }
                deps.add(depNode.get());
            }
            if (deps.size() == dependencies.size()) {
                return Optional.of(deps);
            }
        }
        return Optional.empty();
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

    public <T> T getComponent(Class<T> componentClass) throws ContextException {
        if (componentInstances.containsKey(componentClass)) {
            return ((T) componentInstances.get(componentClass));
        }

        if (!components.contains(componentClass)) {
            throw new ContextException("Class not in context: " + componentClass.getName());
        }

        try {
            T instance = componentClass.getDeclaredConstructor().newInstance();
            componentInstances.put(componentClass, instance);
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ContextException("Failed to create instance of class " + componentClass.getName(), e);
        }
    }
}
