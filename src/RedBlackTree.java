import java.util.*;

/**
 * A left-leaning red-black binary search tree implementation.
 * 
 * @author Jackson
 *
 * @param <K> The key type
 * @param <V> The value type
 */
public class RedBlackTree<K extends Comparable<K>, V> implements ST<K, V>{
    private Node root; // The root node.
    private static final boolean BLACK = false;
    private static final boolean RED = true;
    private int size;
    
    /**
     * Makes a new red-black tree.
     */
    public RedBlackTree(){
        root = null;
        size = 0;
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    // Gets the definition of the given word.
    public V get(K key) {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        
        return get(root, key);
    }
    
    private V get(Node n, K key){
        if(n == null)
            return null;
        if(key.equals(n.k))
            return n.v;
        return get(key.compareTo(n.k)>0? n.l : n.r, key);
    }
    
    public boolean containsKey(K key) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        
        return get(key) != null;
    }
    
    public boolean containsValue(V value) {
        if (value == null)
            throw new NullPointerException("Value is not allowed to be null");
        
        return containsValue(root, value);
    }
    
    private boolean containsValue(Node n, V val) {
        if(n == null)
            return false;
        return val.equals(n.v) || containsValue(n.l, val) || containsValue(n.r, val);
    }
    
    public Set<K> getAllKeys(){
        return getAllKeys(root);
    }
    
    private Set<K> getAllKeys(Node n){
        if(n==null)
            return new HashSet<K>();
        Set<K> set = getAllKeys(n.l);
        set.add(n.k);
        set.addAll(getAllKeys(n.r));
        return set;
    }
    
    // Sets the definition of the given word
    public V put(K key, V val) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        if (val == null)
            throw new NullPointerException("Value is not allowed to be null");
        
        V previousValue = get(key);
        if(root == null)
            root = new Node(key, val);
        else
            root = put(root, key, val);
        root.col = BLACK;
        return previousValue;
    }
    
    private Node put(Node n, K key, V val){
        int c = key.compareTo(n.k);
        
        if(c == 0){
            n.v = val; // If n matches word, just change n's definition
            //size--;
        } else if(c > 0)
            n.l = (n.l==null? new Node(key, val) : put(n.l, key, val)); // If n is greater than word, set n's left to either the new node if n has no left, or the result of putting n in the left node.
        else if(c < 0)
            n.r = (n.r==null? new Node(key, val) : put(n.r, key, val)); // If n is less than word, set n's right to either the new node if n has no right, or the result of putting n in the right node.    
        
        return fix(n);
    }
    
    public boolean canRemove() {
	    return false;
	}

	public V remove(K key) {
        throw new UnsupportedOperationException("Red-black trees do not support deletion; key="+key.toString());
    }
    
    public void clear() {
		for (K key : getAllKeys())
			remove(key);
	}
    
    private Node fix(Node n){
        if(n.l!=null && n.l.l!=null && n.l.col == RED && n.l.l.col == RED) // If left child, left grandchild are red, rotate right.
            n = rotright(n);
        if(n.l!=null && n.r!=null && n.l.col == RED && n.r.col == RED) // If both left and right are red, flip colors.
            n = flipcols(n);
        if(n.r!=null && n.r.col == RED) // If the right is red, rotate left.
            n = rotleft(n);
        return n;
    }
    
    // Rotate n left, return the new top.
    private Node rotleft(Node n){
        Node ntop = n.r; // New top
        assert ntop!=null;
        swapCols(n, ntop);
        n.r = (ntop.l==null)? null : ntop.l;
        ntop.l = n;
        return ntop;
    }
    
    // Rotate n right, return the new top
    private Node rotright(Node n){
        Node ntop = n.l; // New top
        assert ntop!=null;
        swapCols(n, ntop);
        n.l = ntop.r;
        ntop.r = n;
        return ntop;
    }
    
    // Swap the colors of nodes a and b.
    private void swapCols(Node a, Node b){
        if(a==null || b==null)
            return;
        boolean temp = a.col;
        a.col = b.col;
        b.col = temp;
    }
    
    // Flip the colors on this node and its children.
    private Node flipcols(Node n){
        n.l.col = BLACK;
        n.r.col = BLACK;
        n.col = RED;
        return n;
    }
    
    public String toString(){
        return String.format("Red-Black Tree", size);
    }
    
    class Node {
        private final K k;
        private V v;
        private Node l;
        private Node r;
        private boolean col;
        
        public Node(K word, V def) {
            k = word;
            v = def;
            col = RED;
            size++;
        }
        
        public String toString(){
            return k.toString();
        }
    }
}

class RedBlackTreeSupplier implements STSupplier {
    public RedBlackTreeSupplier() {
    }
    
    public <K extends Comparable<K>, V> ST<K, V> getNew() {
        return new RedBlackTree<K, V>();
    }
    
    public String toString() {
        return "RBT";
    }
}
