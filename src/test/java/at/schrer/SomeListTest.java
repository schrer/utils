package at.schrer;

import at.schrer.structures.SomeList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SomeListTest {

    @Test
    void testBasicAddSizeGet(){
        // Given
        SomeList<String> list = new SomeList<>();
        // When
        list.add("bla");
        // Then
        assertEquals(1, list.size());
        assertEquals("bla", list.get(0));
    }

    @Test
    void testGetWithAFew(){
        // Given
        SomeList<String> list = get10FilledList();
        // Then
        assertEquals(10, list.size());
        assertEquals("first", list.get(0));
        assertEquals("third", list.get(2));
        assertEquals("tenth", list.get(9));
    }

    @Test
    void testGetFirst(){
        // Given
        SomeList<String> list = get10FilledList();
        // Then
        assertEquals("first", list.getFirst());
    }

    @Test
    void testGetLast(){
        // Given
        SomeList<String> list = get10FilledList();
        // Then
        assertEquals("tenth", list.getLast());
    }

    @Test
    void testContainsEmptyList(){
        // Given
        SomeList<String> list = new SomeList<>();
        // Then
        assertFalse(list.contains("some"));
    }

    @Test
    void testContains(){
        // Given
        SomeList<String> list = get10FilledList();
        // Then
        assertFalse(list.contains("some"));
        assertFalse(list.contains(null));
        assertTrue(list.contains("fifth"));
    }

    @Test
    void testContainsNull(){
        // Given
        SomeList<String> list = new SomeList<>();
        list.add(null);
        // Then
        assertTrue(list.contains(null));
    }

    @Test
    void testToArrayEmpty(){
        // Given
        SomeList<String> list = new SomeList<>();
        // When
        Object[] arr = list.toArray();
        // Then
        assertNotNull(arr);
        assertEquals(0, arr.length);
    }

    @Test
    void testToArray(){
        // Given
        SomeList<String> list = get10FilledList();
        // When
        Object[] arr = list.toArray();
        // Then
        assertEquals(10, arr.length);
        assertEquals(arr[0], list.getFirst());
        assertEquals(arr[9], list.getLast());
    }


    private SomeList<String> get10FilledList(){
        SomeList<String> list = new SomeList<>();
        list.add("first");
        list.add("second");
        list.add("third");
        list.add("fourth");
        list.add("fifth");
        list.add("sixth");
        list.add("seventh");
        list.add("eighth");
        list.add("ninth");
        list.add("tenth");
        return list;
    }
}
