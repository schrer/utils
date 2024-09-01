package at.schrer.inject;

import at.schrer.inject.dummyclasses.Component1;
import at.schrer.inject.dummyclasses.NonComponent1;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContextBuilderTest {

    private static final String DUMMY_PACKAGE = "at.schrer.inject.dummyclasses";

    @Test
    void instantiateContext() throws ContextException {
        ContextBuilder contextBuilder = new ContextBuilder(DUMMY_PACKAGE);
    }

    @Test
    void loadComponent() throws ContextException {
        // Given
        ContextBuilder contextBuilder = new ContextBuilder(DUMMY_PACKAGE);
        // When
        Component1 instance1 = contextBuilder.getInstance(Component1.class);
        // Then
        assertNotNull(instance1);
    }

    @Test
    void checkSingletonBehavior() throws ContextException {
        // Given
        ContextBuilder contextBuilder = new ContextBuilder(DUMMY_PACKAGE);
        // When
        Component1 instance1 = contextBuilder.getInstance(Component1.class);
        Component1 instance2 = contextBuilder.getInstance(Component1.class);
        // Then
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertEquals(instance1, instance2);
    }

    @Test
    void loadNonComponent() throws ContextException {
        // Given
        ContextBuilder contextBuilder = new ContextBuilder(DUMMY_PACKAGE);
        // When / Then
        assertThrows(ContextException.class, () -> contextBuilder.getInstance(NonComponent1.class));
    }
}
