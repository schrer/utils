package at.schrer.utils.inject;

import at.schrer.utils.inject.dummyclasses.name.NamedDummy;
import at.schrer.utils.inject.dummyclasses.name.NoNameDummy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComponentBluePrintTest {

    @Test
    void loadName(){
        // Given
        String namedDummyName = "thisDummyIsNamed";
        ComponentBluePrint<NoNameDummy> noNameBluePrint = new ComponentBluePrint<>(NoNameDummy.class);
        ComponentBluePrint<NamedDummy> namedBluePrint = new ComponentBluePrint<>(NamedDummy.class);

        // Then
        assertTrue(noNameBluePrint.getComponentAlias().isEmpty());
        assertTrue(namedBluePrint.getComponentAlias().isPresent());
        assertEquals(namedDummyName, namedBluePrint.getComponentAlias().get());
    }
}
