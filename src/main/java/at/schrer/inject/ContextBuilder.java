package at.schrer.inject;

import at.schrer.inject.annotations.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextBuilder {

    private static final Map<String, ContextBuilder> loadedBuilders = new HashMap<>();

    private final List<Class<?>> components;
    private final Map<Class<?>, Object> componentInstances;

    private ContextBuilder(String packagePath) throws ContextException {
        this.components = new ArrayList<>();
        this.componentInstances = new HashMap<>();

        final ClassScanner classScanner = new ClassScanner(packagePath);
        try {
            components.addAll(classScanner.findByAnnotation(Component.class));
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new ContextException("Unable to load context.", e);
        }
    }

    public static ContextBuilder getInstance(String packagePath) throws ContextException {
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
