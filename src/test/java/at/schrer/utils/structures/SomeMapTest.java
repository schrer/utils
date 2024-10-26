package at.schrer.utils.structures;

import at.schrer.utils.structures.SomeMap;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SomeMapTest {
    @Test
    void testBasicPutSizeGet(){
        // Given
        SomeMap<String, Integer> map = new SomeMap<>();
        // When
        map.put("bla", 1);
        // Then
        assertEquals(1, map.size());
        assertEquals(1, map.get("bla"));
    }

    @Test
    void testContainsKey(){
        // Given
        SomeMap<String, Integer> map = threeNumbersMap();
        // Then
        assertTrue(map.containsKey("two"));
        assertTrue(map.containsKey("one"));
        assertTrue(map.containsKey("three"));
        assertFalse(map.containsKey("four"));
        assertFalse(map.containsKey("five"));
        assertFalse(map.containsKey("six"));
    }

    @Test
    void testContainsValue(){
        // Given
        SomeMap<String, Integer> map = threeNumbersMap();
        // Then
        assertTrue(map.containsValue(1));
        assertTrue(map.containsValue(3));
        assertTrue(map.containsValue(2));
        assertFalse(map.containsValue(4));
        assertFalse(map.containsValue(5));
        assertFalse(map.containsValue(6));
    }

    @Test
    void testNotOkIntialSizes(){
        // Given
        final int defaultSize = 30;
        SomeMap<String, Integer> zero = new SomeMap<>(0);
        SomeMap<String, Integer> negative = new SomeMap<>(-50);
        SomeMap<String, Integer> twenty = new SomeMap<>(20);
        SomeMap<String, Integer> fiveK = new SomeMap<>(5000);
        SomeMap<String, Integer> def = new SomeMap<>();

        // Then
        assertEquals(defaultSize, zero.getMaxSize());
        assertEquals(defaultSize, negative.getMaxSize());
        assertEquals(defaultSize, def.getMaxSize());
        assertEquals(20, twenty.getMaxSize());
        assertEquals(5000, fiveK.getMaxSize());
    }

    @Test
    void testGrowingAndSize(){
        // Given
        SomeMap<String, Integer> map = new SomeMap<>(1);
        // Then
        assertEquals(1, map.getMaxSize());
        assertEquals(0, map.size());

        // When
        map.put("one",1);
        // Then
        assertEquals(1, map.getMaxSize());
        assertEquals(1, map.size());

        // When
        map.put("two", 2);
        // Then
        assertEquals(2, map.getMaxSize());
        assertEquals(2, map.size());

        // When
        map.put("three", 3);
        // Then
        assertEquals(4, map.getMaxSize());
        assertEquals(3, map.size());

        // When
        map.put("four", 4);
        // Then
        assertEquals(4, map.getMaxSize());
        assertEquals(4, map.size());

        // When
        map.put("five", 5);
        // Then
        assertEquals(8, map.getMaxSize());
        assertEquals(5, map.size());
    }


    @Test
    void testOverwrite(){
        // Given
        SomeMap<String, Integer> map = threeNumbersMap();

        // When
        Integer old = map.put("one", 2);

        // Then
        assertEquals(1, old);
        assertEquals(2, map.get("one"));
    }

    @Test
    void testNoGrowNeeded(){
        // Given
        SomeMap<String, Integer> map = new SomeMap<>(1);

        // When
        map.put("one",1);
        // Then
        assertEquals(1, map.getMaxSize());
        assertEquals(1, map.size());

        // When
        map.put("one", 2);
        // Then
        assertEquals(1, map.getMaxSize());
        assertEquals(1, map.size());

        // When
        map.put("two", 2);
        // Then
        assertEquals(2, map.getMaxSize());
        assertEquals(2, map.size());

        // When
        map.put("two", 1);
        // Then
        assertEquals(2, map.getMaxSize());
        assertEquals(2, map.size());

        // When
        map.put("five", 5);
        // Then
        assertEquals(4, map.getMaxSize());
        assertEquals(3, map.size());
    }

    @Test
    void testEntrySet(){
        // Given
        SomeMap<String, Integer> map = threeNumbersMap();
        // When
        Set<Map.Entry<String, Integer>> entrySet = map.entrySet();

        // Then
        assertEquals(3, entrySet.size());
        assertTrue(entrySet.stream().map(Map.Entry::getKey).collect(Collectors.toSet()).containsAll(List.of("one", "two", "three")));
        assertTrue(entrySet.stream().map(Map.Entry::getValue).collect(Collectors.toSet()).containsAll(List.of(1, 2, 3)));
    }

    @Test
    void testGetValues(){
        // Given
        SomeMap<String, Integer> map = threeNumbersMap();
        // When
        Collection<Integer> values = map.values();
        // Then
        assertEquals(3, values.size());
        assertTrue(values.containsAll(List.of(1, 2, 3)));
    }

    @Test
    void testKeySet(){
        // Given
        SomeMap<String, Integer> map = threeNumbersMap();
        // When
        Set<String> keys = map.keySet();

        // Then
        assertEquals(3, keys.size());
        assertTrue(keys.containsAll(List.of("one", "two", "three")));

    }

    @Test
    void testRemove(){
        // Given
        SomeMap<String, Integer> map = new SomeMap<>();
        // When
        // TODO
        // Then

    }

    @Test
    void testShrink(){
        // Given
        SomeMap<String, Integer> map = new SomeMap<>();
        // When
        // TODO
        // Then

    }

    private SomeMap<String, Integer> threeNumbersMap(){
        return createFilledMap(
                List.of("one", "two", "three"),
                List.of(1,2,3)
        );
    }

    private <K extends Comparable<K>,V> SomeMap<K,V> createFilledMap(List<K> keys, List<V> values){
        SomeMap<K, V> map = new SomeMap<>(keys.size());
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), values.get(i));
        }
        return map;
    }
}
