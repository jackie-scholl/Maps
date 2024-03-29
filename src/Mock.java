import java.util.HashSet;
import java.util.Set;

class Mock<K extends Comparable<K>, V> implements Dictionary<K, V> {
    public Mock() {}
    
    public int size() {
        return -1;
    }
    
    public boolean isEmpty() {
        return false;
    }
    
    public V get(K key) {
        return null;
    }
    
    public boolean containsKey(K key) throws NullPointerException {
        return false;
    }
    
    public boolean containsValue(V value) {
        return false;
    }
    
    public Set<K> getAllKeys() {
        return new HashSet<K>();
    }
    
    public V put(K key, V val) {
        return null;
    }
    
    public V delete(K key) {
        return null;
    }
    
    public void clear() {}
    
    public String toString() {
        return "Mock";
    }
}

class MockSupplier implements DictionarySupplier {
    public MockSupplier() {}
    
    public <K extends Comparable<K>, V> Dictionary<K, V> getNew() {
        return new Mock<K, V>();
    }
    
    public String toString() {
        return "Mock";
    }
}
