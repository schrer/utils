package at.schrer.inject;

import at.schrer.inject.dummyclasses.Component1;
import at.schrer.inject.dummyclasses.Component2;
import at.schrer.inject.dummyclasses.NonComponent1;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContextBuilderTest {

    private static final String DUMMY_PACKAGE = "at.schrer.inject.dummyclasses";
    private static final String DUMMY_PACKAGE_SUB = "at.schrer.inject.dummyclasses.sub";

    @Test
    void instantiateContext() throws ContextException {
        ContextBuilder contextBuilder = ContextBuilder.getInstance(DUMMY_PACKAGE);
    }

    @Test
    void getTwoBuildersOnSamePackage() throws ContextException {
        // When
        ContextBuilder contextBuilder1 = ContextBuilder.getInstance(DUMMY_PACKAGE);
        ContextBuilder contextBuilder2 = ContextBuilder.getInstance(DUMMY_PACKAGE);
        // Then
        assertEquals(contextBuilder1, contextBuilder2);
    }

    @Test
    void getTwoBuildersOnDiffPackage() throws ContextException {
        // When
        ContextBuilder contextBuilder1 = ContextBuilder.getInstance(DUMMY_PACKAGE);
        ContextBuilder contextBuilder2 = ContextBuilder.getInstance(DUMMY_PACKAGE_SUB);
        // Then
        assertNotEquals(contextBuilder1, contextBuilder2);
    }

    @Test
    void loadComponent() throws ContextException {
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getInstance(DUMMY_PACKAGE);
        // When
        Component1 instance1 = contextBuilder.getComponent(Component1.class);
        // Then
        assertNotNull(instance1);
    }

    @Test
    void checkSingletonBehavior() throws ContextException {
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getInstance(DUMMY_PACKAGE);
        // When
        Component1 instance1 = contextBuilder.getComponent(Component1.class);
        Component2 instance2 = contextBuilder.getComponent(Component2.class);
        Component1 instance3 = contextBuilder.getComponent(Component1.class);
        Component2 instance4 = contextBuilder.getComponent(Component2.class);
        // Then
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertEquals(instance1, instance3);
        assertEquals(instance2, instance4);
    }

    @Test
    void loadNonComponent() throws ContextException {
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getInstance(DUMMY_PACKAGE);
        // When / Then
        assertThrows(ContextException.class, () -> contextBuilder.getComponent(NonComponent1.class));
    }
}
