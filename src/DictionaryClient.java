import java.util.*;
import java.io.*;

@SuppressWarnings("javadoc")
public class DictionaryClient {
    private static Random r;
    private static final double SIZE = 100.0;
    private static PrintStream outStream;

    private static STSupplier RBTsup = new RedBlackTreeSupplier();
    private static STSupplier LLsup = new LinkedListSupplier();

    private static STSupplier[] mainSTSups = new STSupplier[] {
        LLsup,
        RBTsup,
        new ProbingHashtableSupplier(),
        new ChainingHashtableSupplier(LLsup),
        new ChainingHashtableSupplier(RBTsup),
        new ChainingHashtableSupplier(new ProbingHashtableSupplier())
    };

    public static void main(String[] args) throws IOException {
        String fileName = "out.txt";
        outStream = new PrintStream(new File(fileName));

        long start = System.currentTimeMillis();

        for (STSupplier stSup : mainSTSups) {
            r = new Random(1176072517698283250L);
            
            System.out.printf("====%s====%n", stSup.<String, String>getNew().toString());
            test1h(stSup);
            test2h(stSup);
            test3h(stSup);
            test4h(stSup, 200);

            for (int i=0; i<5; i++)
                test6h(stSup, 500);

            System.out.println();
        }

        long middle = System.currentTimeMillis();
        System.out.printf("%.3f seconds for correctness testing%n%n", (middle-start)/1000.0);
        
        if(middle-start<1000){
            test7(30, 10, 3, 0);
            //test7(100, 10, 3, 0);
            //test7(1000, 10, 3, 1, 0);
            //test7(10000, 10, 3, 1, 0);
            //test7(100000, 10, 3, 1, 0);
        }

        long end = System.currentTimeMillis();
        System.out.printf("%.3f seconds total%n", (end-start)/1000.0);
    }

    private static double[] getLimits(double[] amounts) {
        int length = amounts.length;
        double asum = 0.0;
        for(double x : amounts)
            asum += x;

        double[] limits = new double[length];
        double cur = 0.0;
        for(int i=0; i<length; i++){
            cur += amounts[i]/asum;
            limits[i] = cur;
        }
        return limits;
    }

    private static void test1h(STSupplier stSup) {
        ST<String, String> dict = stSup.getNew();

        dict.put("orange", "fruit");
        dict.put("bread", "starch");
        dict.put("giraffe", "animal");
        dict.put("pear", "fruit");

        assert dict.get("bread").equals("starch");
        assert dict.get("giraffe").equals("animal");
        assert dict.get("orange").equals("fruit");
        assert dict.get("pear").equals("fruit");

        System.out.printf("Test #1: passed%n");
    }

    private static void test2h(STSupplier stSup){
        ST<String, String> dict = stSup.getNew();

        dict.put("orange", "fruit");
        dict.put("bread", "starch");
        dict.put("giraffe", "animal");
        dict.put("pear", "fruit");
        dict.put("mango", "thing");

        assert dict.get("bread").equals("starch");
        assert dict.get("giraffe").equals("animal");
        assert dict.get("orange").equals("fruit");
        assert dict.get("pear").equals("fruit");
        assert dict.get("mango").equals("thing");
        assert dict.size() == 5;

        dict.put("bread", "wheat");
        dict.put("pear", "yummy");

        assert dict.get("bread").equals("wheat");
        assert dict.get("giraffe").equals("animal");
        assert dict.get("orange").equals("fruit");
        assert dict.get("pear").equals("yummy");
        assert dict.get("mango").equals("thing");
        assert dict.size() == 5;

        System.out.printf("Test #2: passed%n", dict.toString());
    }

    private static void test3h(STSupplier stSup){
        ST<String, String> dict = stSup.getNew();

        if (!dict.canRemove()){
            System.out.printf("Test #3: CAN'T DELETE%n");
            return;
        }
        dict.put("orange", "fruit");
        dict.put("bread", "starch");
        dict.put("giraffe", "animal");
        dict.put("pear", "fruit");
        dict.put("mango", "thing");

        assert dict.get("bread").equals("starch");
        assert dict.get("giraffe").equals("animal");
        assert dict.get("orange").equals("fruit");
        assert dict.get("pear").equals("fruit");
        assert dict.get("mango").equals("thing");
        assert dict.size() == 5;

        dict.remove("giraffe");
        dict.remove("mango");

        assert dict.get("bread").equals("starch");
        assert dict.get("orange").equals("fruit");
        assert dict.get("pear").equals("fruit");
        assert dict.get("mango") == null;
        assert dict.get("giraffe") == null;
        assert dict.size() == 3;
        assert !dict.isEmpty();

        dict.clear();

        assert dict.get("bread") == null;
        assert dict.get("orange") == null;
        assert dict.get("pear") == null;
        assert dict.get("mango") == null;
        assert dict.get("giraffe") == null;
        assert dict.size() == 0;
        assert dict.isEmpty();

        System.out.printf("Test #3: passed%n");
    }

    private static void test4h(STSupplier stSup, int n) {
        ST<Integer, Integer> st = stSup.getNew();

        for (int i=0; i<n; i++) {
            int x = Math.abs(r.nextInt());
            st.put(x, x+1);
        }

        for (int x : st.getAllKeys()) {
            assert st.get(x)==x+1;
            assert st.containsKey(x);
            assert st.containsValue(x+1);
        }

        System.out.printf("Test #4, n=%d: passed%n", n);
    }

    private static void test6h(STSupplier stSup, int n) {
        final int MAX = 2*n;
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        ST<Integer, Integer> st = stSup.getNew();

        for(int i=0; i<n; i++){
            int c = (int) (r.nextDouble()*6);

            if (c==0) { // Get
                int k = (int) (r.nextDouble()*MAX);
                Integer x = map.get(k);
                Integer y = st.get(k);
                if(x == null)
                    assert y == null;
                else
                    assert x.equals(y);
            } else if (c==1) { //put
                int k = (int) (r.nextDouble()*MAX);
                int v = (int) (r.nextDouble()*MAX);
                Integer x = map.put(k, v);

                Integer y = st.put(k, v);
                if(x == null)
                    assert y == null;
                else
                    assert x.equals(y);
            } else if (c==2) { // size, isEmpty
                assert map.size() == st.size();
                assert map.isEmpty() == st.isEmpty();
            } else if (c==3) {
                int k = (int) (r.nextDouble()*MAX);
                boolean x = map.containsKey(k);
                boolean y = st.containsKey(k);
                assert x == y;
            } else if (c==4) {
                int v = (int) (r.nextDouble()*MAX);
                boolean x = map.containsValue(v);
                boolean y = st.containsValue(v);
                assert x == y;
            } else if (c==5) {
                Set<Integer> x = map.keySet();
                Set<Integer> y = st.getAllKeys();
                assert x.equals(y);
            } else {
                System.out.println("? " + c);
            }
        }

        System.out.printf("Test #6, n=%d: passed%n", n);
    } 

    private static void test7(int REP, double... amounts) {
        double[] limits = getLimits(amounts);
        int n = 10000;

        System.out.printf("Test 7 started; n=%d, rep=%d%n", n, REP);
        long startMillis = System.currentTimeMillis();

        STSupplier[] confs = (STSupplier[]) new STSupplier[] {
            new LinkedListSupplier(),
            new RedBlackTreeSupplier(),
            new ProbingHashtableSupplier(0.55, 0.45),
            new ProbingHashtableSupplier(0.60, 0.40),
            new ProbingHashtableSupplier(0.65, 0.38),
            new ProbingHashtableSupplier(0.70, 0.36),
            new ProbingHashtableSupplier(0.80, 0.30),
            new ProbingHashtableSupplier(0.90, 0.27),
            new ProbingHashtableSupplier(0.95, 0.15),
            new ChainingHashtableSupplier(LLsup),
            new ChainingHashtableSupplier(RBTsup),
            new MockSupplier()};

        int len = confs.length;

        String s1 = "";
        String s2 = "";
        for (STSupplier conf : confs) {
            s1 += String.format("  %-13s", conf.toString());
            s2 += String.format("%s,", conf.toString());
        }
        System.out.println(s1);
        outStream.println(s2);  

        StatsList[] lists = new StatsList[len];
        for (int i=0; i<len; i++)
            lists[i] = new StatsList();

        for (int i=1; i<=REP; i++) {
            for (int j=0; j<len; j++) {
                ST<Integer, Integer> st = confs[j].getNew();
                double mean = test7h(st, n, limits);
                outStream.printf("%.5f,", mean);
                lists[j].add(mean);
            }

            outStream.println();

            if (i%(REP/100+1) == 0) {
                for (int j=0; j<len; j++) {
                    StatsList l = lists[j];
                    System.out.printf("%6.2f (%5.3g) ", l.mean(), l.stddevMean());
                }
                System.out.println();
            }
        }
        System.out.println(s1+"\n");  
        outStream.println(s2+"\n");

        for (int j=0; j<len; j++) {
            for(int i=0; i<3; i++)
                lists[j].remove(0);

            System.out.printf("%-12s %s%n", confs[j], lists[j]);
            outStream.printf("%-12s %s%n", confs[j], lists[j]);
        }

        StatsList mock = lists[len-1];
        outStream.printf("%nSubtracting the mock (%.3f [%.5f]):%n", mock.mean(), mock.stddevMean());


        for (int j=0; j<len-1; j++) {
            outStream.printf("%-12s %.3f (%.5f)%n", confs[j], lists[j].mean()-mock.mean(), lists[j].stddevMean());
        }

        long endMillis = System.currentTimeMillis();
        System.out.printf("Test 7 completed successfully; took %.3f seconds%n%n", (endMillis-startMillis)/1000.0);
        outStream.printf("Test 7 completed successfully; took %.3f seconds%n%n", (endMillis-startMillis)/1000.0);
    }

    private static double test7h(ST<Integer, Integer> st, int n, double[] limits){
        final int MAX = (int) (1.5*SIZE);

        TreeSet<Integer> set = new TreeSet<Integer>(st.getAllKeys());
        long start = System.nanoTime();

        for(int i=0; i<n; i++){
            double d = r.nextDouble();
            int c;
            for(c=0; c<limits.length; c++)
                if(d < limits[c])
                    break;

            if (c==0) { // Get
                if(set.size() > 0){
                    Integer rn = (int) (r.nextDouble()*MAX);
                    st.get(rn);
                }
            } else if (c==1) { // put
                int k = (int) (r.nextDouble()*MAX);
                int v = r.nextInt();
                st.put(k, v);
            } else if (c==2) { //delete
                Integer k = (int) (r.nextDouble()*MAX);
                st.remove(k);
            }
        }
        long end = System.nanoTime();
        return ((double) (end-start))/n;
    }
}

class StatsList{
    private final List<Double> list;

    /**
     * Make a new StatsList
     * 
     */
    public StatsList() {
        list = new ArrayList<Double>();
    }

    /**
     * Add the given number to the list.
     * 
     * @param x number to add. 
     */
    public void add(double x) {
        if(Double.isNaN(x)){
            x = 0.0;
            System.out.print("ERR");
        }
        list.add(x);
    }

    /**
     * Remove the n'th entry. 
     * 
     * @param n the entry to remove
     */
    public void remove(int n) {
        list.remove(n);
    }

    /**
     * Returns the number of entries in the list.
     * 
     * @return number of entries in list
     */
    public int size() {
        return list.size();
    }

    /**
     * Return average value in array, NaN if no such value.
     * 
     * @return the mean of the list
     */
    public double mean() {
        if (size() == 0) return Double.NaN;
        double sum = 0.0;
        for (double x : list) {
            sum += x;
        }
        return sum / size();
    }

    /**
     * Return the sample variance of the array, NaN if no such value.
     * 
     * @return sample variance
     */
    private double var() {
        if (size() == 0)
            return Double.NaN;
        double avg = mean();
        double sum = 0.0;
        for (double x : list) {
            double diff = x - avg;
            sum += diff * diff;
        }
        return sum / (size() - 1);
    }

    /**
     * Return the standard deviation of the array, NaN if no such value.
     * 
     * @return standard deviation
     */
    private double stddev() {
        return Math.sqrt(var());
    }

    /**
     * Return the standard deviation of mean of the array, NaN if no such value.
     * 
     * @return standard deviation of the mean
     */
    public double stddevMean() {
        return stddev()/Math.sqrt(size());
    }

    public String toString() {
        return String.format("%6.3f (%6.3f)", mean(), stddevMean());
    }
}

//412+263+450+162+30+202+98+18

/*class Average{
private double sum;
private int num;

public Average(){
    sum = 0.0;
    num = 0;
}

void add(double x){
    sum += x;
    num++;
}

void addAll(double[] xs){
    for(double x : xs)
        add(x);
}

double mean(){
    return sum/num;
}
}*/


//System.out.printf("Test %d completed successfully%n%n", testNum);



/*private static void test3(){
    //int testNum = 3;
    //System.out.printf("Test %d started%n", testNum);
    //System.out.printf("Test %d completed successfully%n%n", testNum);
    //System.out.printf("Test #%d, %s%n", testNum, st.toString());
    //ST<String, String> st = stSup.getNew();

    for (STSupplier stSup : mainSTSups) {
        test3h(stSup);
    }
    System.out.println();
}

private static void test1() {
        //int testNum = 1;
        //System.out.printf("Test %d started%n", testNum);
        //ST<String, String> st = stSup.getNew();

        for (STSupplier stSup : mainSTSups) {
            test1h(stSup);
        }
        System.out.println();
    }


    private static void test2(){
        //int testNum = 2;
        //System.out.printf("Test %d started%n", testNum);
        //System.out.printf("Test %d completed successfully%n%n", testNum);
        //ST<String, String> st = stSup.getNew();
        //System.out.printf("Test #%d, %s%n", testNum, st.toString());

        for (STSupplier stSup : mainSTSups) {
            test2h(stSup);
        }
        System.out.println();
    }


    private static void test4(int n){
        //System.out.printf("Test 4 started with n=%d\n", n);
        //System.out.println("Test 4 completed successfully\n");

        for (STSupplier stSup : mainSTSups)
            test4h(stSup, n);
        System.out.println();
    }

    private static void test6(int rep){
        int n = 500;
        System.out.printf("Test 6 started, n=%d%n", n);

        for (int i=0; i<rep; i++) {
            for(STSupplier stSup : mainSTSups)
                test6h(stSup, n);
        }

        System.out.println("Test 6 completed successfully\n");
    }


        //System.out.printf("Test #4, n=%d, %s%n", n, st.toString());
        test1();
        test2();
        test3();

        test4(100);

        test6(5);


        //System.out.printf("Test #1, %s%n", dict.toString());


            if (maxFullness == DEF_MAX && minFullness == DEF_MIN) {
                return String.format("Probing Hashtable");
            } else {
                return String.format("Probing Hashtable (%.2f, %.2f)", maxFullness, minFullness);
            }
        } else
            return String.format("Probing Hashtable (%.2f, %.2f, %.2f)", maxFullness, minFullness, setFullness);



    private static void test8(int REP, double... amounts) {
        int n = 10000;
        double[] limits = getLimits(amounts);
        long startMillis = System.currentTimeMillis();

        System.out.printf("Test 8 started; n=%d, rep=%d%n", n, REP);

        double[][] confs = {
            {.65, .38},
            {},
            {.70, .36},
            {},
            {.80, .30},
            {},
            {.90, .27},
            {},
            {.95, .15},
            {}
        };

        int len = confs.length;

        String s = "";
        for(double[] conf : confs){
            String temp = "";
            if(conf.length == 0){
                temp = "Mock";
            } else {
                for(double x : conf)
                    temp += String.format("%2.0f/", x*100);
                temp = temp.replaceAll("/\\z", ":");
            }
            s += String.format("  %-13s", temp);
        }
        System.out.println(s);

        StatsList[] lists = new StatsList[len];

        for(int i=0; i<len; i++)
            lists[i] = new StatsList();

        @SuppressWarnings("unchecked")
        ST<Integer, Integer>[] sts = (ST<Integer, Integer>[]) new ST[len];
        for (int j=0; j<len; j++) {
            double[] conf = confs[j];
            if(conf.length == 0)
                sts[j] = new Mock<Integer, Integer>();
            else if(conf.length == 2)
                sts[j] = new ProbingHashtable<Integer, Integer>(conf[0], conf[1]);
            else
                throw new RuntimeException("WTF");
        }

        for (int i=1; i<=REP; i++) {
            for (int j=0; j<len; j++) {
                double mean = test7h(sts[j], n, limits);
                lists[j].add(mean);
            }

            if (i%(REP/100+1) == 0) {
                for(int j=0; j<len; j++){
                    StatsList l = lists[j];
                    System.out.printf("%6.2f (%5.3g) ", l.mean(), l.stddevMean());
                }
                System.out.println();
            }
        }
        System.out.println(s);

        System.out.println();
        for (int j=0; j<len; j++) {
            double[] conf = confs[j];
            String temp = "";
            if (conf.length == 0) {
                temp = "Mock";
            } else {
                for (double x : conf)
                    temp += String.format("%2.0f/", x*100);
                temp = temp.replaceAll("/\\z", ":");
            }

            for (int i=0; i<3; i++)
                lists[j].remove(0);

            System.out.printf("%-6s %s%n", temp, lists[j]);
        }

        long diff = System.currentTimeMillis()-startMillis;
        System.out.printf("%nTest 8 completed successfully in %.3f seconds%n", diff/1000.0);
    }

            //test8(5, 10, 3, 2);
            //test8(100, 10, 3, 0, 2);
            //test8(250, 10, 3, 0, 2);
            //test8(1000, 10, 3, 0, 2);
            //test8(5000, 10, 3, 1, 0.5);
             
        new ChainingHashtableSupplier(new ChainingHashtableSupplier(LLsup)),
        new ChainingHashtableSupplier(new ChainingHashtableSupplier(RBTsup)),
        new ChainingHashtableSupplier(new ChainingHashtableSupplier(new ChainingHashtableSupplier(LLsup))),
        new ChainingHashtableSupplier(new ChainingHashtableSupplier(new ChainingHashtableSupplier(RBTsup)))


    private int getNextEntry(int i) {
        while (array[i] != null)
            i = (i+1) % capacity;
        return i;
    }

    /*
     * These three classes took substantial aid from the JDK. 
     * 
     */
    /*private abstract class HashIterator<E> implements Iterator<E> {
        Entry<K,V> next;        // next entry to return
        int index;              // current slot
        Entry<K,V> current;     // current entry

        HashIterator() {
            index = 0;
            // advance to first entry
            next = array[index++];
            while (index < capacity && next == null)
                next = array[index++];
        }

        public final boolean hasNext() {
            return next != null;
        }

        final Entry<K,V> nextEntry() {
            Entry<K,V> e = next;
            if (e == null)
                throw new NoSuchElementException();

            next = null;
            next = array[getNextEntry(index)];

            current = e;
            return e;
        }

        public void remove() {
            if (current == null)
                throw new IllegalStateException();
            K k = current.k;
            current = null;
            ProbingHashtable.this.remove(k);
        }
    }

    private final class ValueIterator extends HashIterator<V> {
        public V next() {            return nextEntry().v;        }
    }

    private final class KeyIterator extends HashIterator<K> {
        public K next() {            return nextEntry().k;        }
    }

    private final class EntryIterator extends HashIterator<Map.Entry<K,V>> {
        public Map.Entry<K,V> next() {            return nextEntry();        }
    }    

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
     * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     * @return set of keys
     */
    /*public Set<K> keySet() {
        return keySet;
    }

    private final class KeySet extends AbstractSet<K> {
        public Iterator<K> iterator() {            return new KeyIterator();        }
        public int size() {            return size;        }
        public boolean contains(K key) {            return containsKey(key);        }
        public boolean remove(K key) {            return ProbingHashtable.this.remove(key) != null;        }
        public void clear() {            ProbingHashtable.this.clear();        }
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own <tt>remove</tt> operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not
     * support the <tt>add</tt> or <tt>addAll</tt> operations.
     * 
     * @return the values contained in the map
     */
    /*public Collection<V> values() {
        return values;
    }

    private final class Values extends AbstractCollection<V> {
        public Iterator<V> iterator() {            return new ValueIterator();        }
        public int size() {            return size;        }
        public void clear() {            ProbingHashtable.this.clear();        }
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation, or through the
     * <tt>setValue</tt> operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations.  It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this map
     */
    /*public Set<Map.Entry<K, V>> entrySet() {
        return entrySet;
    }

    private final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public Iterator<Map.Entry<K,V>> iterator() {            return new EntryIterator();        }
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Entry<K,V> e = (Entry<K,V>) o;
            V val = get(e.k);
            return val!=null && val.equals(e.v);
        }
        public boolean remove(K key) {            return ProbingHashtable.this.remove(key) != null;        }
        public int size() {            return size;        }
        public void clear() {            ProbingHashtable.this.clear();        }
    }*/
/*public boolean containsValue(Object value) throws NullPointerException {
if (value == null)
    throw new NullPointerException("Value is not allowed to be null");

for (Entry<K, V> p : array) {
    if(p!=null && value.equals(p.v))
        return true;
}

return false;
}*/

/*public V get(Object key) {
    return get((K) key);
}*/




/*@SuppressWarnings("unchecked")
public boolean containsKey(Object key) {
    return containsKey((K) key);
}*/


/*public V remove(Object key) throws NullPointerException, UnsupportedOperationException {
    return remove((K) key);
}*/


/*private Set<Map.Entry<K,V>> entrySet = new EntrySet();
private Set<K> keySet = new KeySet();
private Collection<V> values = new Values();

*/