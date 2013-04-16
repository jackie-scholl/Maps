import java.util.*;

/**
 * A general-chaining hash table.
 * 
 * @author Jackson Scholl
 * 
 * @param <K> The key type
 * @param <V> The value type
 */
public class ChainingHashtable<K extends Comparable<K>, V> implements Dictionary<K, V> {
    final static int DEF_SIZE = 11;
    final static double DEF_MAX = 7.0;
    final static double DEF_MIN = 5.0;
    final static double DEF_SET = 3.0;
    final static DictionarySupplier DEF_SUPPLIER = new LinkedListSupplier();
    
    private Dictionary<K, V>[] array;
    private int size;
    private int capacity;
    
    private final double maxFullness;
    private final double minFullness;
    private final double setFullness;
    
    private final DictionarySupplier supplier;
    
    /**
     * Primary constructor.
     * 
     * @param delegateSupplier
     * @param maximum
     * @param minimum
     * @param setFactor
     */
    public ChainingHashtable(DictionarySupplier delegateSupplier, double maximum, double minimum, double setFactor) {
        supplier = delegateSupplier;
        size = 0;
        capacity = DEF_SIZE;
        maxFullness = maximum;
        minFullness = minimum;
        setFullness = setFactor;
        
        @SuppressWarnings("unchecked")
        Dictionary<K, V>[] a = (Dictionary<K, V>[]) new Dictionary[capacity];
        array = a;
        
        for (int i = 0; i < capacity; i++)
            array[i] = newDictionary();
    }
    
    /**
     * Constructor.
     * 
     * @param delegateSupplier
     * @param factor
     * @param margin
     */
    public ChainingHashtable(DictionarySupplier delegateSupplier, double factor, double margin) {
        this(delegateSupplier, factor * (1.0 + margin), factor / (1.0 + margin), factor);
    }
    
    /**
     * Constructor.
     * 
     * @param delegateSupplier
     */
    public ChainingHashtable(DictionarySupplier delegateSupplier) {
        this(delegateSupplier, DEF_MAX, DEF_MIN, DEF_SET);
    }
    
    /**
     * Default constructor.
     */
    public ChainingHashtable() {
        this(DEF_SUPPLIER);
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    private int hash(K key) {
        return Math.abs(key.hashCode());
    }
    
    private Dictionary<K, V> getMap(K key) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        int index = hash(key) % capacity;
        return array[index];
    }
    
    public V get(K key) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        return getMap(key).get(key);
    }
    
    public Set<K> getAllKeys() {
        Set<K> keySet = new HashSet<K>(size);
        for (Dictionary<K, V> st : array)
            if (st != null)
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
        for (Dictionary<K, V> st : array)
            if (st.containsValue(value))
                return true;
        return false;
    }
    
    public V put(K key, V val) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        if (val == null)
            throw new NullPointerException("Value is not allowed to be null");
        
        V value = getMap(key).put(key, val);
        if (value == null) {
            size++;
            resize();
        }
        return value;
    }
    
    public V delete(K key) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        
        V value = getMap(key).delete(key);
        if (value != null) {
            size--;
            resize();
        }
        return value;
    }
    
    public void clear() {
        for (K key : getAllKeys())
            delete(key);
    }
    
    private Dictionary<K, V> newDictionary() {
        return supplier.<K, V> getNew();
    }
    
    private void resize() {
        if (!(size < capacity * minFullness && capacity > 11) && !(size > capacity * maxFullness))
            return;
        
        int newcap = (int) (size / setFullness);
        
        @SuppressWarnings("unchecked")
        Dictionary<K, V>[] a = (Dictionary<K, V>[]) new Dictionary[newcap];
        
        for (int i = 0; i < newcap; i++)
            a[i] = newDictionary();
        
        for (K key : this.getAllKeys()) {
            V val = this.get(key);
            int index = hash(key) % newcap;
            a[index].put(key, val);
        }
        
        this.array = a;
        this.capacity = newcap;
    }
    
    public String toString() {
        if (setFullness == DEF_SET && maxFullness == DEF_MAX && minFullness == DEF_MIN)
            return String.format("Chaining Hashtable (%s)", supplier);
        else if (setFullness == DEF_SET)
            return String.format("Chaining Hashtable (%s, %.0f, %.0f)", supplier, maxFullness, minFullness);
        else
            return String.format("Chaining Hashtable (%s, %.0f, %.0f, %.0f)", supplier, maxFullness, minFullness,
                    setFullness);
    }
}

class ChainingHashtableSupplier implements DictionarySupplier {
    private final double max;
    private final double min;
    private final double set;
    private final DictionarySupplier supplier;
    
    /**
     * Constructs empty {@code ChainingHashtable}'s with the specified {@code maximum}, {@code minimum}, and {@code set}
     * fullness ratios
     * 
     * @param delegateSupplier
     * @param maximum the maximum fullness
     * @param minimum the minimum fullness
     * @param setFactor
     * 
     * @see ChainingHashtable
     */
    public ChainingHashtableSupplier(DictionarySupplier delegateSupplier, double maximum, double minimum,
            double setFactor) {
        supplier = delegateSupplier;
        max = maximum;
        min = minimum;
        set = setFactor;
    }
    
    public ChainingHashtableSupplier(DictionarySupplier delegateSupplier, double maximum, double minimum) {
        this(delegateSupplier, maximum, minimum, ChainingHashtable.DEF_SET);
    }
    
    public ChainingHashtableSupplier(DictionarySupplier delegateSupplier) {
        this(delegateSupplier, ChainingHashtable.DEF_MAX, ChainingHashtable.DEF_MIN);
    }
    
    public ChainingHashtableSupplier(double maximum, double minimum) {
        this(ChainingHashtable.DEF_SUPPLIER, maximum, minimum);
    }
    
    public ChainingHashtableSupplier() {
        this(ChainingHashtable.DEF_SUPPLIER);
    }
    
    public <K extends Comparable<K>, V> Dictionary<K, V> getNew() {
        return new ChainingHashtable<K, V>(supplier, max, min, set);
    }
    
    public String toString() {
        return String.format("HT:%s", supplier.toString());
    }
}
