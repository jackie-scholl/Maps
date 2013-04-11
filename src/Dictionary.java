/*
 * Dictionary.java
 * 
 * Copyright (c) 2013 Jackson Scholl
 */

import java.util.*;

/**
 * A map; maps keys to values.
 * 
 * @param <K> Key type
 * @param <V> Value type
 * 
 */
public interface Dictionary<K extends Comparable<K>, V> {
    /**
     * Returns the current number of key-value mappings.
     * 
     * @return the number of key-value mappings
     */
    int size();
    
    /**
     * Returns {@code true} if there are no key-value mappings in this map.
     * 
     * @return {@code true} if there are no key-value mappings in this map
     */
    boolean isEmpty();
    
    /**
     * Returns the value that is mapped to the given key.
     *
     * @param key the key to locate
     * @return the value mapped to {@code key} or {@code null} if not found
     * 
     * @throws NullPointerException if the specified key is null
     */
    V get(K key) throws NullPointerException;
    
    /**
     * Returns {@code true} if this map contains a mapping for the specified key.
     *
     * @param key key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified key
     * @throws NullPointerException if the specified key is null
     */
    boolean containsKey(K key) throws NullPointerException;
    
    /**
     * Returns {@code true} if this map maps one or more keys to the specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the
     *         specified value
     * @throws NullPointerException if the specified value is null
     */
    boolean containsValue(V value);
    
    /**
     * Returns the set of all the keys contained in this map.
     * 
     * @return the set of all the keys contained in this map
     */
    Set<K> getAllKeys();
    
    /**
     * Associates the specified value with the specified key in this map.  If the map previously
     * contained a mapping for the key, the old value is replaced by the specified value.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * 
     * @return the value previously associated with the key, or {@code null} if there was none.
     * 
     * @throws NullPointerException if the specified key or value is null
     */
    V put(K key, V value) throws NullPointerException;
    
    /**
     * Removes the mapping for a key from this map if it is present.
     *
     * <p>The map will not contain a mapping for the specified key once the call returns.
     *
     * @param key key whose mapping is to be removed from the map
     * 
     * @return the value previously associated with {@code key}, or {@code null} if there was none.
     * 
     * @throws NullPointerException if key is null
     * @throws UnsupportedOperationException if the {@code remove} operation is not supported by this map
     */
    V remove(K key) throws NullPointerException, UnsupportedOperationException;
    
    /**
     * Returns whether or not this dictionary supports the {@code remove} operation.
     * 
     * @return whether or not the dictionary can remove
     */
    boolean canRemove();
    
    
    /**
     * Remove all mappings. The map will be empty after this call returns.
     * 
     */
    void clear();
}


/**
 * Makes dictionaries.
 * 
 * @author Jackson
 */
interface DictionarySupplier {
    /**
     * Returns a new dictionary
     * 
     * @return new dictionary
     */
    <K extends Comparable<K>, V> Dictionary<K, V> getNew();
}
