import java.util.HashSet;
import java.util.Set;

class Mock<K extends Comparable<K>, V> implements ST<K, V> {
    public Mock() { }
    public int size() {  return -1; }
    public boolean isEmpty() {  return false; }
    public V get(K key) {  return null; }
    public boolean containsKey(K key) throws NullPointerException {  return false; }
    public boolean containsValue(V value) {  return false; }
    public Set<K> getAllKeys() {  return new HashSet<K>(); }
    public V put(K key, V val) {     return null;    }
    public boolean canRemove() {        return true;    }
    public V remove(K key) {     return null;    }
    public void clear() {}
    public String toString() {  return "Mock"; }
}

class MockSupplier implements STSupplier {
    public MockSupplier(){}
    
    public <K extends Comparable<K>, V> ST<K, V> getNew() {
        return new Mock<K, V>();
    }
    
    public String toString() {
        return "Mock";
    }
}
