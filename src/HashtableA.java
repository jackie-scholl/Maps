import java.util.*;

enum STMode {rbt, ll};

/**
 * A general-chaining hash table.
 * 
 * @version 2013-03-19
 * @author Jackson Scholl
 *
 * @param <K> The key type
 * @param <V> The value type
 */
public class HashtableA<K extends Comparable<K>, V> implements ST<K, V>  {
    final static int DEF_SIZE = 11;
    final static double DEF_MAX = 7.0;
    final static double DEF_MIN = 5.0;
    final static double DEF_SET = 3.0;
    final static STSupplier DEF_SUPPLIER = new LinkedListSupplier();
    
    private ST<K, V>[] arr;
    private int size;
    private int cap;
    
    private final double max;
    private final double min;
    private final double set;
    
    private final STSupplier supplier;
    
    
    /**
     * Primary constructor.
     * 
     * @param delegateSupplier 
     * @param capacity
     * @param maximum
     * @param minimum
     * @param setFactor
     */
    public HashtableA(STSupplier delegateSupplier, int capacity, double maximum, double minimum, double setFactor) {
        supplier = delegateSupplier;
        size = 0;
        cap = capacity;
        max = maximum;
        min = minimum;
        set = setFactor;
        
        @SuppressWarnings("unchecked")
        ST<K, V>[] a = (ST<K, V>[]) new ST[capacity];
        arr = a;
        
        for(int i=0; i<cap; i++)
            arr[i] = newST();
    }
    
    /**
     * Constructor.
     * @param delegateSupplier 
     * @param capacity
     * @param factor
     * @param margin
     */
    public HashtableA(STSupplier delegateSupplier, int capacity, double factor, double margin){
        this(delegateSupplier, capacity, factor*(1.0+margin), factor/(1.0+margin), factor);
    }
    
    
    /**
     * Constructor.
     * @param delegateSupplier 
     */
    public HashtableA(STSupplier delegateSupplier){
        this(delegateSupplier, DEF_SIZE, DEF_MAX, DEF_MIN, DEF_SET);
    }
    
    /**
     * Default constructor.
     */
    public HashtableA(){
        this(DEF_SUPPLIER);
    }
    
    
    public int size(){
        return size;
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    private int hash(K key){
        return Math.abs(key.hashCode());
    }
    
    private ST<K, V> getMap(K key) throws NullPointerException {
        if(key == null)
            throw new NullPointerException("Key is not allowed to be null");
        int index = hash(key) % cap;
        return arr[index];
    }
    
    /**
     * Returns the value that is mapped to the given key.
     *
     * @param key the key to locate
     * @return the value mapped to {@code key} or {@code null} if not found
     * 
     * @throws NullPointerException if the specified key is null
     */
    public V get(K key) throws NullPointerException {
        if(key == null)
            throw new NullPointerException("Key is not allowed to be null");
        return getMap(key).get(key);
    }
    
    public Set<K> getAllKeys() {
        Set<K> keySet = new HashSet<K>(size);
        for(ST<K,V> st : arr)
            if(st != null)
            keySet.addAll(st.getAllKeys());
        return keySet;
    }
    
    public boolean containsKey(K key) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        return getMap(key).containsKey(key);
    }
    
    public boolean containsValue(V value) throws NullPointerException {
        if (value == null)
            throw new NullPointerException("Value is not allowed to be null");
        for(ST<K, V> st : arr)
            if(st.containsValue(value))
            return true;
        return false;
    }
    
    /**
     * Associates the specified value with the specified key in this map.  If the map previously
     * contained a mapping for the key, the old value is replaced by the specified value.
     *
     * @param key key with which the specified value is to be associated
     * @param val value to be associated with the specified key
     * 
     * @throws NullPointerException if the specified key or value is null
     */
    public V put(K key, V val) throws NullPointerException {
        if(key == null)
            throw new NullPointerException("Key is not allowed to be null");
        if(val == null)
            throw new NullPointerException("Value is not allowed to be null");
        
        V value = getMap(key).put(key, val);
        if(value == null){
            size++;
            resize();
        }
        return value;
    }
    
    public V remove(K key) throws NullPointerException {
        if(key == null)
            throw new NullPointerException("Key is not allowed to be null");
        
        V value = getMap(key).remove(key);
        if(value != null){
            size--;
            resize();
        }
        return value;
    }
    
    public boolean canRemove() {
        return newST().canRemove();
    }
    
    private ST<K, V> newST(){
        return supplier.<K, V>getNew();
    }
    
    private void resize(){
        if(!(size<cap*min && cap>11) && !(size>cap*max))
            return;
        
        int newcap = (int) (size/set);
        
        @SuppressWarnings("unchecked")
        ST<K, V>[] a = (ST<K, V>[]) new ST[newcap];
        
        for(int i=0; i<newcap; i++)
            a[i] = newST();
        
        for(K key : this.getAllKeys()){
            V val = this.get(key);
            int index = hash(key) % newcap;
            a[index].put(key, val);
        }
        
        this.arr = a;
        this.cap = newcap;
    }
    
    public String toString(){
        return String.format("Hashtable(%s, %2.0f, %2.0f, %2.0f)", supplier.<K, V>getNew(), max, min, set, size);
    }
}

class HashtableASupplier implements STSupplier {
    private final int cap;
    private final double max;
    private final double min;
    private final double set;
    private final STSupplier supplier;
    
    /**
     * Constructs empty {@code HashtableB}'s with the specified {@code maximum}, {@code minimum}, and {@code set} fullness ratios
     * 
     * @param delegateSupplier 
     * @param capacity
     * @param  maximum the maximum fullness
     * @param  minimum the minimum fullness
     * @param setFactor 
     * 
     * @see HashtableA
     */
    public HashtableASupplier(STSupplier delegateSupplier, int capacity, double maximum, double minimum, double setFactor) {     
        supplier = delegateSupplier;
        cap = capacity;
        max = maximum;
        min = minimum;
        set = setFactor;
    }
    
    public HashtableASupplier(STSupplier delegateSupplier, double maximum, double minimum, double setFactor){
        this(delegateSupplier, HashtableA.DEF_SIZE, maximum, minimum, setFactor);
    }
    
    public HashtableASupplier(STSupplier delegateSupplier, double maximum, double minimum){
        this(delegateSupplier, maximum, minimum, HashtableA.DEF_SET);
    }
    
    public HashtableASupplier(STSupplier delegateSupplier){
        this(delegateSupplier, HashtableA.DEF_MAX, HashtableA.DEF_MIN);
    }
    
    public HashtableASupplier(double maximum, double minimum){
        this(HashtableA.DEF_SUPPLIER, maximum, minimum);
    }
    
    public HashtableASupplier(){
        this(HashtableA.DEF_SUPPLIER);
    }
    
    public <K extends Comparable<K>, V> ST<K, V> getNew() {
        return new HashtableA<K, V>(supplier, cap, max, min, set);
    }
    
    public String toString() {
        return String.format("HT:%s", supplier.toString());
    }
    
    public boolean canDelete() {
        return true;
    }
}
