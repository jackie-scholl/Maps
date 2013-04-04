import java.util.*;

/**
 * A general-chaining hash table.
 * 
 * @version 2013-03-19
 * @author Jackson Scholl
 *
 * @param <K> The key type
 * @param <V> The value type
 */
public class ChainingHashtable<K extends Comparable<K>, V> implements ST<K, V> {
    final static int DEF_SIZE = 11;
    final static double DEF_MAX = 7.0;
    final static double DEF_MIN = 5.0;
    final static double DEF_SET = 3.0;
    final static STSupplier DEF_SUPPLIER = new LinkedListSupplier();
    
    private ST<K, V>[] array;
    private int size;
    private int capacity;
    
    private final double maxFullness;
    private final double minFullness;
    private final double setFullness;
    
    private final STSupplier supplier;
    
    
    /**
     * Primary constructor.
     * 
     * @param delegateSupplier
     * @param maximum
     * @param minimum
     * @param setFactor
     */
    public ChainingHashtable(STSupplier delegateSupplier, double maximum, double minimum, double setFactor) {
        supplier = delegateSupplier;
        size = 0;
        capacity = DEF_SIZE;
        maxFullness = maximum;
        minFullness = minimum;
        setFullness = setFactor;
        
        @SuppressWarnings("unchecked")
        ST<K, V>[] a = (ST<K, V>[]) new ST[capacity];
        array = a;
        
        for(int i=0; i<capacity; i++)
            array[i] = newST();
    }
    
    /**
     * Constructor.
     * @param delegateSupplier
     * @param factor
     * @param margin
     */
    public ChainingHashtable(STSupplier delegateSupplier, double factor, double margin){
        this(delegateSupplier, factor*(1.0+margin), factor/(1.0+margin), factor);
    }
    
    
    /**
     * Constructor.
     * @param delegateSupplier 
     */
    public ChainingHashtable(STSupplier delegateSupplier){
        this(delegateSupplier, DEF_MAX, DEF_MIN, DEF_SET);
    }
    
    /**
     * Default constructor.
     */
    public ChainingHashtable(){
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
        int index = hash(key) % capacity;
        return array[index];
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
        for(ST<K,V> st : array)
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
        for(ST<K, V> st : array)
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
    
    public boolean canRemove() {
        return newST().canRemove();
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
    
    public void clear() {
		for (K key : getAllKeys())
			remove(key);
	}
    
    private ST<K, V> newST(){
        return supplier.<K, V>getNew();
    }
    
    private void resize(){
        if(!(size<capacity*minFullness && capacity>11) && !(size>capacity*maxFullness))
            return;
        
        int newcap = (int) (size/setFullness);
        
        @SuppressWarnings("unchecked")
        ST<K, V>[] a = (ST<K, V>[]) new ST[newcap];
        
        for(int i=0; i<newcap; i++)
            a[i] = newST();
        
        for(K key : this.getAllKeys()){
            V val = this.get(key);
            int index = hash(key) % newcap;
            a[index].put(key, val);
        }
        
        this.array = a;
        this.capacity = newcap;
    }
    
    public String toString(){
    	if (setFullness == DEF_SET && maxFullness == DEF_MAX && minFullness == DEF_MIN)
    		return String.format("Chaining Hashtable (%s)", supplier);
    	else if (setFullness == DEF_SET)
    		return String.format("Chaining Hashtable (%s, %.0f, %.0f)", supplier, maxFullness, minFullness);
    	else
    		return String.format("Chaining Hashtable (%s, %.0f, %.0f, %.0f)", supplier, maxFullness, minFullness, setFullness);
    	
    	//return String.format("Hashtable(%s, %2.0f, %2.0f, %2.0f)", supplier, maxFullness, minFullness, setFullness, size);
    }
}

class ChainingHashtableSupplier implements STSupplier {
    private final double max;
    private final double min;
    private final double set;
    private final STSupplier supplier;
    
    /**
     * Constructs empty {@code ChainingHashtable}'s with the specified {@code maximum}, {@code minimum}, and {@code set} fullness ratios
     * 
     * @param delegateSupplier
     * @param  maximum the maximum fullness
     * @param  minimum the minimum fullness
     * @param setFactor 
     * 
     * @see ChainingHashtable
     */
    public ChainingHashtableSupplier(STSupplier delegateSupplier, double maximum, double minimum, double setFactor) {     
        supplier = delegateSupplier;
        max = maximum;
        min = minimum;
        set = setFactor;
    }
    
    public ChainingHashtableSupplier(STSupplier delegateSupplier, double maximum, double minimum){
        this(delegateSupplier, maximum, minimum, ChainingHashtable.DEF_SET);
    }
    
    public ChainingHashtableSupplier(STSupplier delegateSupplier){
        this(delegateSupplier, ChainingHashtable.DEF_MAX, ChainingHashtable.DEF_MIN);
    }
    
    public ChainingHashtableSupplier(double maximum, double minimum){
        this(ChainingHashtable.DEF_SUPPLIER, maximum, minimum);
    }
    
    public ChainingHashtableSupplier(){
        this(ChainingHashtable.DEF_SUPPLIER);
    }
    
    public <K extends Comparable<K>, V> ST<K, V> getNew() {
        return new ChainingHashtable<K, V>(supplier, max, min, set);
    }
    
    public String toString() {
        return String.format("HT:%s", supplier.toString());
    }
}
