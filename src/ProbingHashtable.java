/*
 * ProbingHashtable.java
 * 
 * Version 1.0
 * 
 * 2013-3-19
 * 
 * Copyright (c) 2013 Jackson Scholl. 
 */

import java.util.*;

/**
 * A linear-probing hash table implementation.
 * 
 * @version 2013-03-19
 * @author Jackson Scholl
 * 
 * @param <K> The key type
 * @param <V> The value type
 */
public class ProbingHashtable<K extends Comparable<K>, V> implements Dictionary<K, V> {
    final static double DEF_MAX = 0.75;
    final static double DEF_MIN = 0.25;
    final static double DEF_SET = 0.5;
    
    private static final int MIN_CAPACITY = 11; // The minimum size of the array; when smaller than this, no down-sizing
                                                // will occur.
    
    private Entry<K, V>[] array; // The array holding all the key/value pairs
    private int size; // The current number of elements.
    private int capacity; // Current capacity of the array.
    
    private double maxFullness; // determines how full the array can get before resizing occurs; default 1/2
    private double minFullness; // determines how empty the array can get before resizing occurs; default 3/4
    private double setFullness; // determines how full the array should be made when resizing; default 1/4
    
    /**
     * Constructs an empty {@code HashtableB} with the specified {@code maximum}, {@code minimum}, and {@code set}
     * fullness ratios
     * 
     * @param maximum the maximum fullness
     * @param minimum the minimum fullness
     * @param set the fullness when the array is resized.
     * @throws IllegalArgumentException if {@code minimum} is less than or equal to zero or {@code set} is less or equal
     *             to than {@code minimum} or {@code maximum} is less than or equal to {@code set} or {@code maximum} is
     *             greater than one.
     */
    @SuppressWarnings("unchecked")
    public ProbingHashtable(double maximum, double minimum, double set) throws IllegalArgumentException {
        if (0 >= minimum)
            throw new IllegalArgumentException("Illegal minimum fullness: " + minimum);
        if (minimum >= set)
            throw new IllegalArgumentException("Minimum fullness is greater than or equal to set.");
        if (set >= maximum)
            throw new IllegalArgumentException("Set fullness is greater than or equal to maximum.");
        if (maximum >= 1)
            throw new IllegalArgumentException("Illegal maximum fullness: " + maximum);
        
        size = 0;
        capacity = MIN_CAPACITY;
        maxFullness = maximum;
        minFullness = minimum;
        this.setFullness = set;
        
        array = (Entry<K, V>[]) new Entry[capacity];
    }
    
    public ProbingHashtable(double maximum, double minimum) throws IllegalArgumentException {
        this(maximum, minimum, DEF_SET);
    }
    
    public ProbingHashtable() {
        this(DEF_MAX, DEF_MIN);
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * A hash of the key. I used the absolute value of the key's hashcode so that I didn't get weird negative indices.
     * 
     * @param key
     * @return the hash
     * @throws NullPointerException
     */
    private int hash(K key) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        return Math.abs(key.hashCode());
    }
    
    private int getIndex(K key) {
        int i = hash(key) % capacity;
        while (array[i] != null && !key.equals(array[i].k)) {
            i = (i + 1) % capacity;
        }
        return i;
    }
    
    public V get(K key) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        
        int i = getIndex(key);
        return array[i] == null ? null : array[i].v;
    }
    
    public boolean containsKey(K key) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        
        int i = getIndex(key);
        return array[i] != null;
    }
    
    public boolean containsValue(V value) throws NullPointerException {
        if (value == null)
            throw new NullPointerException("Value is not allowed to be null");
        
        for (Entry<K, V> p : array) {
            if (p != null && value.equals(p.v))
                return true;
        }
        
        return false;
    }
    
    public Set<K> getAllKeys() {
        Set<K> set = new HashSet<K>(size);
        for (Entry<K, V> p : array)
            if (p != null)
                set.add(p.k);
        return set;
    }
    
    public V put(K key, V val) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        if (val == null)
            throw new NullPointerException("Value is not allowed to be null");
        
        int i = getIndex(key);
        
        if (array[i] == null) { // If we are putting a new key in, increase the size.
            size++;
            array[i] = new Entry<K, V>(key, val);
            resizeIfNeeded(); // If we need to resize, do so.
            return null;
        } else {
            assert key.equals(array[i].k);
            V previousValue = array[i].v;
            array[i].v = val;
            return previousValue;
        }
    }
    
    public void putAll(Map<? extends K, ? extends V> m) throws NullPointerException {
        for (K k : m.keySet())
            put(k, m.get(k));
    }
    
    public V delete(K key) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        List<Entry<K, V>> pairs = new ArrayList<Entry<K, V>>();
        
        // Find our key.
        int i = getIndex(key);
        
        if (array[i] == null)
            return null;
        
        // Remove all the keys that could have been "forced over" by this key.
        while (array[i] != null) {
            pairs.add(array[i]);
            array[i] = null;
            size--;
            i = (i + 1) % capacity;
        }
        
        V value = pairs.remove(0).v; // Remove the key we're deleting.
        
        for (Entry<K, V> p : pairs)
            this.put(p.k, p.v); // Put the rest back in the hashtable.
        
        return value;
    }
    
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            array[i] = null;
        }
        size = 0;
        resizeIfNeeded();
    }
    
    /**
     * Resizes the array and copies over the elements if the size is out of bounds.
     * 
     */
    private void resizeIfNeeded() {
        if (!((size < capacity * minFullness && capacity > MIN_CAPACITY) || size > capacity * maxFullness)) {
            return;
        }
        int newCapacity = (int) (size / setFullness); // The size of the new array
        
        @SuppressWarnings("unchecked")
        Entry<K, V>[] newArray = (Entry<K, V>[]) new Entry[newCapacity];
        
        for (int j = 0; j < capacity; j++) {
            Entry<K, V> q = array[j];
            if (q == null)
                continue;
            
            int i = hash(q.k) % newCapacity;
            while (newArray[i] != null && !q.k.equals(newArray[i].k)) {
                i = (i + 1) % newCapacity; // get next index
            }
            newArray[i] = q;
        }
        this.array = newArray;
        this.capacity = newCapacity;
    }
    
    public String toString() {
        if (setFullness == DEF_SET && maxFullness == DEF_MAX && minFullness == DEF_MIN)
            return String.format("Probing Hashtable");
        else if (setFullness == DEF_SET)
            return String.format("Probing Hashtable (%.2f, %.2f)", maxFullness, minFullness);
        else
            return String.format("Probing Hashtable (%.2f, %.2f, %.2f)", maxFullness, minFullness, setFullness);
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.array);
        result = prime * result + this.capacity;
        long temp;
        temp = Double.doubleToLongBits(this.maxFullness);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.minFullness);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.setFullness);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + this.size;
        return result;
    }
    
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ProbingHashtable))
            return false;
        ProbingHashtable<?, ?> other = (ProbingHashtable<?, ?>) obj;
        if (!Arrays.equals(this.array, other.array))
            return false;
        if (this.capacity != other.capacity)
            return false;
        if (Double.doubleToLongBits(this.maxFullness) != Double.doubleToLongBits(other.maxFullness))
            return false;
        if (Double.doubleToLongBits(this.minFullness) != Double.doubleToLongBits(other.minFullness))
            return false;
        if (Double.doubleToLongBits(this.setFullness) != Double.doubleToLongBits(other.setFullness))
            return false;
        if (this.size != other.size)
            return false;
        return true;
    }
    
    /**
     * A key-value pair
     * 
     * @param <K> Key
     * @param <V> Value
     */
    static class Entry<K, V> implements Map.Entry<K, V> {
        final K k;
        V v;
        
        public Entry(K key, V val) {
            k = key;
            v = val;
        }
        
        public K getKey() {
            return k;
        }
        
        public V getValue() {
            return v;
        }
        
        public V setValue(V value) {
            V oldVal = v;
            v = value;
            return oldVal;
        }
        
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.k == null) ? 0 : this.k.hashCode());
            result = prime * result + ((this.v == null) ? 0 : this.v.hashCode());
            return result;
        }
        
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof Entry))
                return false;
            
            @SuppressWarnings("unchecked")
            Entry<K, V> other = (Entry<K, V>) obj;
            
            if (this.k == null) {
                if (other.k != null)
                    return false;
            } else if (!this.k.equals(other.k))
                return false;
            if (this.v == null) {
                if (other.v != null)
                    return false;
            } else if (!this.v.equals(other.v))
                return false;
            return true;
        }
    }
}

class ProbingHashtableSupplier implements DictionarySupplier {
    private double max; // determines how full the array can get before resizing occurs; default 1/2
    private double min; // determines how empty the array can get before resizing occurs; default 3/4
    private double set; // determines how full the array should be made when resizing; default 1/4
    
    /**
     * Constructs empty {@code HashtableB}'s with the specified {@code maximum}, {@code minimum}, and {@code set}
     * fullness ratios
     * 
     * @param maximum the maximum fullness
     * @param minimum the minimum fullness
     * @param setFullness the fullness when the arrays are resized
     * 
     * @see ProbingHashtable
     */
    public ProbingHashtableSupplier(double maximum, double minimum, double setFullness) {
        max = maximum;
        min = minimum;
        set = setFullness;
    }
    
    public ProbingHashtableSupplier(double maximum, double minimum) {
        this(maximum, minimum, ProbingHashtable.DEF_SET);
    }
    
    public ProbingHashtableSupplier() {
        this(ProbingHashtable.DEF_MAX, ProbingHashtable.DEF_MIN);
    }
    
    public <K extends Comparable<K>, V> Dictionary<K, V> getNew() {
        return new ProbingHashtable<K, V>(max, min, set);
    }
    
    public String toString() {
        if (max == ProbingHashtable.DEF_MAX && min == ProbingHashtable.DEF_MIN && set == ProbingHashtable.DEF_SET)
            return "PHT";
        else if (set == 0.5)
            return String.format("PHT(%d/%d)", (int) (max * 100), (int) (min * 100));
        else
            return String.format("PHT(%d/%d/%d)", (int) (max * 100), (int) (min * 100), (int) (set * 100));
    }
}
