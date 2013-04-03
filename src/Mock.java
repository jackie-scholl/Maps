import java.util.HashSet;
import java.util.Set;

class Mock<K extends Comparable<K>, V> implements ST<K, V> {
    public Mock() { }
    public V get(K key) {  return null; }
    public boolean containsKey(K key) throws NullPointerException {  return false; }
    public boolean containsValue(V value) {  return false; }
    public V put(K key, V val) {     return null;    }
    public Set<K> getAllKeys() {  return new HashSet<K>(); }
    public int size() {  return -1; }
    public V remove(K key) {     return null;    }
    public String toString() {  return "Mock"; }
    public boolean isEmpty() {  return false; }
}

class MockSupplier implements STSupplier {
    public MockSupplier(){
        
    }
    
    public <K extends Comparable<K>, V> ST<K, V> getNew() {
        return new Mock<K, V>();
    }
    
    public String toString() {
        return "Mock";
    }
    
    public boolean canDelete() {
        return false;
    }
}
