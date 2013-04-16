import java.util.HashSet;
import java.util.Set;

/**
 * A linked list implementation.
 * 
 * @version 2013-03-19
 * @author Jackson Scholl
 * 
 * @param <K> The key type
 * @param <V> The value type
 */
public class LinkedList<K extends Comparable<K>, V> implements Dictionary<K, V> {
    private Node head;
    private int size;
    
    /**
     * Makes a new linked list.
     * 
     */
    public LinkedList() {
        head = null;
        size = 0;
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public V get(K key) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        
        Node n = head;
        while (n != null) {
            if (n.key.equals(key))
                return n.val;
            n = n.next;
        }
        return null;
    }
    
    public boolean containsKey(K key) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        return get(key) != null;
    }
    
    public boolean containsValue(V value) throws NullPointerException {
        if (value == null)
            throw new NullPointerException("Value is not allowed to be null");
        Node n = head;
        while (n != null) {
            if (value.equals(n.val))
                return true;
            n = n.next;
        }
        return false;
    }
    
    public Set<K> getAllKeys() {
        Set<K> keys = new HashSet<K>();
        Node n = head;
        while (n != null) {
            keys.add(n.key);
            n = n.next;
        }
        return keys;
    }
    
    public V put(K key, V val) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        if (val == null)
            throw new NullPointerException("Value is not allowed to be null");
        
        Node toPut = new Node(key, val);
        if (head == null) {
            head = toPut;
            size++;
            return null;
        }
        
        Node n = head;
        while (true) {
            if (n.key.equals(key)) {
                V previousValue = n.val;
                n.val = val;
                return previousValue;
            }
            if (n.next != null)
                n = n.next;
            else
                break;
        }
        // n is now last node.
        n.next = toPut;
        size++;
        return null;
    }
    
    public V delete(K key) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        
        if (key.equals(head.key)) {
            V value = head.val;
            head = head.next;
            size--;
            return value;
        }
        
        Node n = head;
        
        while (n.next != null && !key.equals(n.next.key)) {
            n = n.next;
        }
        
        if (n.next != null) {
            V value = n.next.val;
            n.next = n.next.next;
            size--;
            return value;
        }
        
        return null;
    }
    
    public void clear() {
        for (K key : getAllKeys())
            delete(key);
    }
    
    public String toString() {
        return String.format("Linked List", size);
    }
    
    class Node {
        K key;
        V val;
        Node next;
        
        public Node(K k, V v) {
            key = k;
            val = v;
        }
    }
}

class LinkedListSupplier implements DictionarySupplier {
    public LinkedListSupplier() {}
    
    public <K extends Comparable<K>, V> Dictionary<K, V> getNew() {
        return new LinkedList<K, V>();
    }
    
    public String toString() {
        return "LL";
    }
}
