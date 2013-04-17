import java.util.HashSet;
import java.util.Set;

/**
 * A left-leaning red-black binary search tree implementation.
 * <p>
 * In adding deletion functionality, I used Robert Sedgewick's [TITLE] - {@link http://www.cs.princeton.edu/~rs/talks/LLRB/LLRB.pdf}.
 * 
 * @author Jackson Scholl
 * 
 * @param <K> The key type
 * @param <V> The value type
 */
public class RedBlackTree<K extends Comparable<K>, V> implements Dictionary<K, V> {
    private static final boolean BLACK = false;
    private static final boolean RED = true;
    
    private Node root;
    private int size;
    
    /**
     * Makes a new red-black tree.
     */
    public RedBlackTree() {
        root = null;
        size = 0;
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public V get(K key) {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        
        return get(root, key);
    }
    
    private V get(Node n, K key) {
        if (n == null)
            return null;
        if (key.equals(n.key))
            return n.val;
        return get(key.compareTo(n.key) < 0 ? n.l : n.r, key);
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
        if (n == null)
            return false;
        return val.equals(n.val) || containsValue(n.l, val) || containsValue(n.r, val);
    }
    
    public Set<K> getAllKeys() {
        return getAllKeys(root);
    }
    
    private Set<K> getAllKeys(Node n) {
        if (n == null)
            return new HashSet<K>();
        Set<K> set = getAllKeys(n.l);
        set.add(n.key);
        set.addAll(getAllKeys(n.r));
        return set;
    }
    
    public V put(K key, V val) throws NullPointerException {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        if (val == null)
            throw new NullPointerException("Value is not allowed to be null");
        
        V previousValue = get(key);
        
        root = put(root, key, val);
        root.color = BLACK;
        
        return previousValue;
    }
    
    private Node put(Node n, K key, V value) {
        if (n == null)
            return new Node(key, value);
        
        int cmp = key.compareTo(n.key);
        
        if (cmp == 0) {
            n.val = value;
        } else if (cmp < 0) {
            n.l = put(n.l, key, value);
        } else if (cmp > 0) {
            n.r = put(n.r, key, value);
        }
        
        return fixUp(n);
    }
    
    public V delete(K key) {
        if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        
        V previousValue = get(key);
        
        root = delete(root, key);
        if (root != null)
            root.color = BLACK;
        
        return previousValue;
    }
    
    /**
     * Delete the {@code key}, assuming it's a descendant of n.
     * <p>
     * Maintains the invariant that n or n's left child is red.
     * 
     * @param n
     * @param key
     * @return the replacement for {@code n}
     */
    private Node delete(Node n, K key) {
        if (n == null)
            return null;
        if (key.compareTo(n.key) < 0) {
            if (!isRed(n.l) && n.l != null && !isRed(n.l.l)) {
                n = moveRedLeft(n);
            }
            n.l = delete(n.l, key);
        } else {
            if (isRed(n.l))
                n = rotateRight(n);
            if (key.compareTo(n.key) == 0 && n.r == null) {
                size--;
                return null;
            }
            if (!isRed(n.r) && !isRed(n.r.l))
                n = moveRedRight(n);
            if (key.compareTo(n.key) == 0) {
                n.val = get(n.r, min(n.r).key);
                n.key = min(n.r).key;
                n.r = deleteMin(n.r);
            } else {
                n.r = delete(n.r, key);
            }
        }
        n = fixUp(n);
        return n;
    }
    
    /**
     * Deletes the minimum in n's tree.
     * <p>
     * Maintains the invariant that n or n's left child is red.
     * 
     * @param n node to delete minimum of
     * @return replacement for n
     */
    private Node deleteMin(Node n) {
        if (n.l == null) {
            size--;
            return null;
        }
        
        if (!isRed(n.l) && !isRed(n.l.l)) {
            n = moveRedLeft(n);
        }
        
        n.l = deleteMin(n.l);
        return fixUp(n);
    }
    
    /**
     * Kinda confusing. See the example in the paper.
     * 
     * @param n node that is red and whose left child and left-left granchild are both black
     * @return replacement for n
     */
    private Node moveRedLeft(Node n) {
        flipColors(n);
        if (isRed(n.r.l)) {
            n.r = rotateRight(n.r);
            n = rotateLeft(n);
            flipColors(n);
        }
        return n;
    }
    
    /**
     * Kinda confusing. See the example in the paper.
     * 
     * @param n node that is red and whose right child and right-left granchild are both black
     * @return replacement for n
     */
    private Node moveRedRight(Node n) {
        flipColors(n);
        if (isRed(n.l.l)) {
            n = rotateRight(n);
            flipColors(n);
        }
        return n;
    }
    
    /**
     * Returns the minimum in n's tree.
     * 
     * @param n node to find minimum of
     * @return the minimum node
     */
    private Node min(Node n) {
        if (n == null)
            return null;
        Node x = min(n.l);
        return x == null ? n : x;
    }
    
    /**
     * Fixes the node as it goes back up the tree on the tail end of {@code put} or {@code delete}.
     * 
     * @param n the node to be "fixed"
     * @return the new n
     */
    private Node fixUp(Node n) {
        if (isRed(n.r))
            n = rotateLeft(n);
        if (isRed(n.l) && isRed(n.l.l))
            n = rotateRight(n);
        if (isRed(n.l) && isRed(n.r))
            n = flipColors(n);
        
        return n;
    }
    
    /**
     * Rotate n left, return the new top
     * 
     * @param n node to rotate left
     * @return new node in place of n
     */
    private Node rotateLeft(Node n) {
        Node x = n.r; // x is the new top
        n.r = x.l;
        x.l = n;
        x.color = n.color;
        n.color = RED;
        return x;
    }
    
    /**
     * Rotate n right, return the new top
     * 
     * @param n node to rotate right
     * @return new node in place of n
     */
    private Node rotateRight(Node n) {
        Node x = n.l; // New top
        n.l = x.r;
        x.r = n;
        x.color = n.color;
        n.color = RED;
        return x;
    }
    
    /**
     * Flip the colors on this node and its children.
     * 
     * @param n The node whose color will be flipped, along with its children
     * @return the newly color-flipped node
     */
    private Node flipColors(Node n) {
        n.color = !n.color;
        n.l.color = !n.l.color;
        n.r.color = !n.r.color;
        return n;
    }
    
    private boolean isRed(Node n) {
        return n != null && n.color == RED;
    }
    
    public void clear() {
        for (K key : getAllKeys())
            delete(key);
    }
    
    public String toString() {
        return "Red-Black Tree";
    }
    
    class Node {
        private K key;
        private V val;
        private Node l;
        private Node r;
        private boolean color;
        
        public Node(K word, V def) {
            key = word;
            val = def;
            color = RED;
            size++;
        }
        
        public String toString() {
            return String.format("%s (%c)", key.toString(), color == BLACK ? 'B' : 'R');
        }
    }
}

class RedBlackTreeSupplier implements DictionarySupplier {
    public RedBlackTreeSupplier() {}
    
    public <K extends Comparable<K>, V> Dictionary<K, V> getNew() {
        return new RedBlackTree<K, V>();
    }
    
    public String toString() {
        return "RBT";
    }
}
