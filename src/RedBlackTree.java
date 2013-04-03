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
    
    public V remove(K key){
        throw new RuntimeException("Red-black trees do not support deletion; key="+key.toString());
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

/*
 // Returns the number of words to the left of the given word
 public int rank(K word){
 return rank(word, root, 0);
 }
 
 private int rank(K word, Node n, int rankUpLeft){
 if(n.compareTo(word) >= 0){
 return n.l==null? rankUpLeft : rank(word, n.l, rankUpLeft);
 } else {
 int size = rankUpLeft+1+size2(n.l);
 return n.r==null? size : rank(word, n.r, size);
 }
 }
 
 // Return the lowest word greater than or equal to the given word.
 public K ceiling(K word){
 return ceiling(word, root);
 }
 
 private K ceiling(K word, Node n){
 if(n == null || n.equals(null))
 return null; // We're at the end of the line. Return the best we have so far.
 
 if(n.compareTo(word) < 0)
 return ceiling(word, n.r); // If we're too low, we must go right.
 else
 return min(ceiling(word, n.l), n.w); // If we're greater than the word, we have a shot at being the best. We use min to find the new best.
 }
 
 // Return the greatest word less than or equal to the given word.
 public K floor(K word){        return floor(word, root);    }
 
 private K floor(K word, Node n){
 if(n == null)
 return null; // We're at the end of the line. Return the best we have so far.
 return (n.compareTo(word)>0)? floor(word, n.l) : max(floor(word, n.r), n.w); // If we're too high, we must go left. If we're less than word, we have a chance of being the best.
 }
 */

/*
 // Utility function. Returns the minimum string from the input.
 private K min(K s1, K s2){
 assert s1!=null && s2!=null && s2!=null;
 return (s1!=null && s1.compareTo(s2)<0)? s1 : s2; 
 }
 
 // Utility function. Returns the maximum string from the input.
 private K max(K s1, K s2){
 assert s1!=null && s2!=null && s2!=null;
 return (s1!=null && s1.compareTo(s2)>0)? s1 : s2; 
 }*/
/*
 // Swap the colors of nodes a and b.
 // Experimental. Should work.
 private void swapCols2(Node a, Node b){
 a.col ^= b.col;
 b.col ^= a.col;
 a.col ^= b.col;
 }*/
/*
 // Returns all the words, in order.
 public String print(){
 return print(root);
 }
 
 // Prints this word and all words below it, in order.
 private String print(Node n){
 String s = "";
 s += (n.l==null? "" : print(n.l)+" "); // First add the left, if there's anything there.
 s += n.w.toString(); // Next add our word
 s += (n.r==null? "" : " "+print(n.r)); // Then add the right, if there's anything there.
 return s;
 }
 
 // A debugger printer.
 public String debugPrint2(){
 String str = "     "+root.s2()+"\n";
 str += "  "+(root.l!=null? root.l.s2() : "  ")+"    "+(root.r!=null? root.r.s2() : "  ")+"\n";
 str += ""+(root.l!=null && root.l.l!=null? root.l.l.s2() : "  ")+" "+(root.l!=null && root.l.r!=null? root.l.r.s2() : "  ");
 str += "  "+(root.r!=null && root.r.l!=null? root.r.l.s2() : "  ")+" "+(root.r!=null && root.r.r!=null? root.r.r.s2() : "  ");
 return str;
 }*/


// Checks that the tree is following its guarantees
/* public void checkCorrect(){
 assert root != null;
 assert height(root) <= 2*Math.log(size2(root))/Math.log(2)+1; // Checks maximum height properties.
 checkCorrect(root);
 }
 
 // Checks n and all descendents of n.
 private void checkCorrect(Node n){
 if(n==null)
 return;
 assert n.r==null || n.r.col==BLACK; // Right is not RED.
 assert n==root || n.l==null || n.col!=RED || n.l.col!=RED; // Either left is null or this and left are not both RED.
 
 assert get(n.k).equals(n.v);
 assert size2(n) == size2(n.l)+1+size2(n.r);
 //assert ceiling(n.w).equals(n.w);
 //assert floor(n.w).equals(n.w);
 if(n.r!=null)
 assert n.equals(rotright(rotleft(n)));
 if(n.l!=null)
 assert n.equals(rotleft(rotright(n)));
 
 checkCorrect(n.l);
 checkCorrect(n.r);
 }*/

/*
 // Utility to get the "size" of a node - 1 plus the number of children (direct and indirect).
 private int size2(Node n){
 return n==null? 0 : size2(n.l)+1+size2(n.r);
 }
 
 // Utility to get the "height" of a node - the longest path below this.    
 private int height(Node n){
 return n==null? 0 : 1+Math.max(height(n.l), height(n.r));
 }  
 
 
 // Used for debugPrint2.
 public String s2(){
 return k.toString().substring(0,1) + (col==RED? "R":"B");
 }
 */ 

/*
 public int compareTo(K word) {
 return k.compareTo(word);
 }
 
 public boolean equals(Node n) {
 boolean b = true;
 b &= k.equals(n.k);
 b &= v.equals(n.v);
 b &= (l==null) == (n.l==null);
 b &= (l!=null && n.l!= null)? l.equals(n.l): true;
 b &= (r==null) == (n.r==null);
 b &= (r!=null && n.r!= null)? r.equals(n.r): true;
 b &= (col==n.col);
 return b;
 }*/

/*
 * <<<<<<< HEAD:src/RedBlackTree.java
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
 =======
*/