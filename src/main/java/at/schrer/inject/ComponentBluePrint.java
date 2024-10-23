package at.schrer.inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class ComponentBluePrint<T> {
    private final List<ComponentConstructor<T>> constructors;
    private final ComponentConstructor<T> noArgConstructor;
    private final Class<T> componentClass;

    public ComponentBluePrint(Class<T> componentClass) {
        this.componentClass = componentClass;
        List<ComponentConstructor<T>> allConstructors = new ArrayList<>();
        for (Constructor<?> constructor : componentClass.getConstructors()) {
            allConstructors.add((ComponentConstructor<T>) new ComponentConstructor<>(constructor));
        }

        this.constructors = Collections.unmodifiableList(allConstructors);
        this.noArgConstructor = constructors.stream()
                .filter(ComponentConstructor::isDependencyLess)
                .findAny().orElse(null);
    }

    /**
     * Checks if the component can be instantiated without any arguments.
     *
     * @return true if a no-args constructor is available, false otherwise.
     */
    public boolean canBeDependencyLess(){
        return noArgConstructor != null;
    }

    public boolean isSameClass(Class<?> clazz){
        return this.componentClass == clazz;
    }

    public Optional<T> getNoArgsInstance()
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (noArgConstructor == null) {
            return Optional.empty();
        }

        return Optional.of(noArgConstructor.getInstance());
    }

    public T getInstance(Object... parameters)
            throws ContextException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Optional<ComponentConstructor<T>> constructor = constructors.stream()
                .filter(it -> it.matchesParameters(parameters))
                .findFirst();
        if (constructor.isEmpty()) {
            throw new ContextException("No matching constructor found for parameters");
        }
        return constructor.get().getInstance(parameters);
    }

    public List<ComponentConstructor<T>> getConstructors(){
        return constructors;
    }

    public static class ComponentConstructor<V> {
        private final Constructor<V> constructor;
        private final List<Class<?>> dependencies;
        private final boolean dependencyLess;

        public ComponentConstructor(Constructor<V> constructor) {
            this.constructor = constructor;
            this.dependencies = List.of(constructor.getParameterTypes());
            this.dependencyLess = constructor.getParameterCount() == 0;
        }

        public List<Class<?>> getDependencies() {
            return dependencies;
        }

        public boolean isDependencyLess() {
            return dependencyLess;
        }

        public boolean matchesParameters(Object... parameters){
            Set<Class<?>> providedParamClasses = Arrays.stream(parameters)
                    .map(Object::getClass)
                    .collect(Collectors.toSet());
            return providedParamClasses.size() == dependencies.size()
                    && dependencies.containsAll(providedParamClasses)
                    && providedParamClasses.containsAll(dependencies);
        }

        public V getInstance(Object... parameters)
                throws InvocationTargetException, InstantiationException, IllegalAccessException {
            return constructor.newInstance(parameters);
        }
    }
}
