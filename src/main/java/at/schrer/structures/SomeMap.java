package at.schrer.structures;

import java.util.*;

/**
 * A map implementation based on simple arrays.
 * It is initialized with a size of 30, unless a specific initial size is given in the constructor. For initial sizes lower than 1 the default size is used.
 * Because of the array nature of the map it is grown when necessary.
 *
 * @param <K> type of the key
 * @param <V> type of the value
 */
public class SomeMap<K extends Comparable<K>, V> implements Map<K, V> {

    private static final int DEFAULT_INIT_SIZE = 30;
    private static final int GROW_FACTOR = 2;
    private static final double SHRINK_FACTOR = 0.75;

    private final int initSize;
    private int maxSize;

    private int size;
    private Entry<K, V>[] entries;

    public SomeMap() {
        this(DEFAULT_INIT_SIZE);
    }

    public SomeMap(int initialSize) {
        if (initialSize<1) {
            initialSize = DEFAULT_INIT_SIZE;
        }

        this.initSize = initialSize;
        this.maxSize = initialSize;
        this.size = 0;
        this.entries = new Entry[initSize];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return findEntryIndexByKey(key) >= 0;
    }

    @Override
    public boolean containsValue(Object value) {
        return findEntryByValue(value) != null;
    }

    @Override
    public V get(Object key) {
        Entry<K,V> entry = findEntryByKey(key);
        return entry == null ? null : entry.getValue();
    }

    @Override
    public V put(K key, V value) {
        Entry<K, V> entry = findEntryByKey(key);
        V oldValue;
        if (entry == null) {
            int freePosition = findFreePosition();
            if (freePosition < 0) {
                grow();
                freePosition = findFreePosition();
            }

            entry = new Entry<>(key, value);
            this.entries[freePosition] = entry;
            size++;
            oldValue = null;
        } else {
            oldValue = entry.getValue();
            entry.setValue(value);
        }

        return oldValue;
    }

    @Override
    public V remove(Object key) {
        final int index = findEntryIndexByKey(key);
        if (index < 0) {
            return null;
        }

        V value = entries[index].getValue();
        entries[index] = null;
        size--;

        shrinkIfNeeded();
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry: m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.size = 0;
        this.entries = new Entry[initSize];
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (Entry<K,V> entry : entries) {
            if (entry == null) {
                continue;
            }
            keys.add(entry.getKey());
        }
        return keys;
    }

    @Override
    public Collection<V> values() {
        Set<V> values = HashSet.newHashSet(this.size);
        for (Entry<K,V> entry : entries) {
            if (entry == null) {
                continue;
            }
            values.add(entry.getValue());
        }
        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entrySet = HashSet.newHashSet(this.size);
        for (Entry<K,V> entry : entries) {
            if (entry == null) {
                continue;
            }
            entrySet.add(entry);
        }
        return entrySet;
    }

    private void grow(){
        final int newMaxSize = this.maxSize*GROW_FACTOR;
        final Entry[] newEntryArray = new Entry[newMaxSize];
        System.arraycopy(this.entries, 0, newEntryArray, 0, size);
        this.entries = newEntryArray;
        this.maxSize = newMaxSize;
    }

    private void shrinkIfNeeded(){
        int newMaxSize = (int) Math.round(this.maxSize*SHRINK_FACTOR);
        if (newMaxSize < initSize) {
            // Never go below initSize
            newMaxSize = initSize;
        }
        if (newMaxSize < size) {
            // Better not do that then
            return;
        }

        final Entry[] newEntryArray = new Entry[newMaxSize];
        int nextEntryIndex = 0;
        for (Entry<K, V> entry : entries) {
            if (entry == null) {
                continue;
            }
            newEntryArray[nextEntryIndex] = entry;
            nextEntryIndex++;
        }
        this.entries = newEntryArray;
        this.maxSize = newMaxSize;
    }

    private Entry<K, V> findEntryByKey(Object key){
        final int entryIndex = findEntryIndexByKey(key);
        if (entryIndex < 0) {
            return null;
        }
        return entries[entryIndex];
    }

    private Entry<K, V> findEntryByValue(Object value){
        for (Entry<K,V> entry : entries) {
            if (entry == null) {
                continue;
            }
            if(entry.getValue().equals(value)){
                return entry;
            }
        }
        return null;
    }

    /**
     * Returns the index of the entry with this key in the entries array.
     *
     * @param key the key to look for
     * @return the index of the entry or -1 if there is no entry with this key
     */
    private int findEntryIndexByKey(Object key) {
        for (int i = 0; i < entries.length; i++) {
            Entry<K,V> entry = entries[i];
            if (entry == null) {
                continue;
            }
            if (entry.getKey().equals(key)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds a free position in the entries array.
     *
     * @return the index of a free position or -1 if there is no free position.
     */
    private int findFreePosition(){
        if (size == maxSize) {
            return -1;
        }
        if (entries[size] == null) {
            return size;
        }

        for (int i = 0; i < entries.length; i++) {
            if (entries[i] == null) {
                return i;
            }
        }

        // It should never get here but whatever
        return -1;
    }

    protected int getMaxSize(){
        return this.maxSize;
    }

    public static class Entry<K extends Comparable<K>, V> implements Map.Entry<K,V>, Comparable<K> {
        private final K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public int compareTo(K o) {
            return this.key.compareTo(o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }
}
