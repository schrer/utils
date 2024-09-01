package at.schrer.inject;

import at.schrer.inject.annotations.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextBuilder {
    private List<Class<?>> components;
    private Map<Class<?>, Object> componentInstances;

    public ContextBuilder(String packagePath) throws ContextException {
        this.components = new ArrayList<>();
        this.componentInstances = new HashMap<>();
        final ClassScanner classScanner = new ClassScanner(packagePath);
        try {
            components.addAll(classScanner.findByAnnotation(Component.class));
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new ContextException("Unable to load context.", e);
        }
    }
}
