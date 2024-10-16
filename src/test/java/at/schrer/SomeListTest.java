package at.schrer;

import at.schrer.structures.SomeList;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    void testAddAllIndex_emptyInput(){
        // Given
        SomeList<String> list = new SomeList<>();
        list.add("test");
        // When
        boolean modified = list.addAll(0, List.of());
        // Then
        assertFalse(modified);
        assertEquals(1, list.size());
        assertTrue(list.contains("test"));
    }

    @Test
    void testAddAllIndex_outOfBounds(){
        // Given
        SomeList<String> list = new SomeList<>();
        list.add("test");
        // When
        assertThrows(IndexOutOfBoundsException.class, () -> list.addAll(5, List.of("bla")));
    }

    @Test
    void testAddAllIndex_emptyList(){
        // Given
        SomeList<String> list1 = new SomeList<>();
        SomeList<String> list2 = new SomeList<>();
        SomeList<String> list3 = new SomeList<>();
        SomeList<String> list4 = new SomeList<>();

        // When
        boolean modified1 = list1.addAll(0, List.of());
        boolean modified2 = list2.addAll(0, List.of("some"));
        boolean modified3 = list3.addAll(0, List.of("some", "more"));
        boolean modified4 = list4.addAll(0, List.of("some", "more", "elements"));

        // Then
        assertFalse(modified1);
        assertTrue(list1.isEmpty());

        assertTrue(modified2);
        assertEquals(1, list2.size());
        assertTrue(list2.contains("some"));

        assertTrue(modified3);
        assertEquals(2, list3.size());
        assertTrue(list3.containsAll(List.of("some", "more")));

        assertTrue(modified4);
        assertEquals(3, list4.size());
        assertTrue(list4.containsAll(List.of("some", "more", "elements")));
    }

    @Test
    void testAddAllIndex_prefilledList(){
        // Given
        SomeList<String> list = new SomeList<>();
        list.add("some");
        list.add("value");
        list.add("more");
        list.add("values");

        // When
        boolean modified = list.addAll(0, List.of());
        // Then
        assertFalse(modified);
        assertEquals(4, list.size());
    }

    @Test
    void testAddAllIndex_addInBegin(){
        // Given
        SomeList<String> list = new SomeList<>();
        list.add("last");
        list.add("two");
        list.add("elements");

        // When
        boolean modified = list.addAll(0, List.of("first", "element"));

        // Then
        assertTrue(modified);
        assertEquals(5, list.size());
        assertEquals(0, list.indexOf("first"));
        assertEquals(1, list.indexOf("element"));
        assertEquals(2, list.indexOf("last"));
        assertEquals(3, list.indexOf("two"));
        assertEquals(4, list.indexOf("elements"));
    }

    @Test
    void testAddAllIndex_addAtEnd(){
        // Given
        SomeList<String> list = new SomeList<>();
        list.add("first");
        list.add("two");
        list.add("elements");

        // When
        boolean modified = list.addAll(3, List.of("last", "element"));

        // Then
        assertTrue(modified);
        assertEquals(5, list.size());
        assertEquals(0, list.indexOf("first"));
        assertEquals(1, list.indexOf("two"));
        assertEquals(2, list.indexOf("elements"));
        assertEquals(3, list.indexOf("last"));
        assertEquals(4, list.indexOf("element"));
    }

    @Test
    void testAddAllIndex_addSingleInMiddle(){
        // Given
        SomeList<String> list = new SomeList<>();
        list.add("first");
        list.add("this");
        list.add("then");
        list.add("that");

        // When
        boolean modified = list.addAll(2, List.of("single"));

        // Then
        assertTrue(modified);
        assertEquals(5, list.size());
        assertEquals(0, list.indexOf("first"));
        assertEquals(1, list.indexOf("this"));
        assertEquals(2, list.indexOf("single"));
        assertEquals(3, list.indexOf("then"));
        assertEquals(4, list.indexOf("that"));
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
