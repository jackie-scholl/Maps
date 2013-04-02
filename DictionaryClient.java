import java.util.*;
import java.io.*;

@SuppressWarnings("javadoc")
public class DictionaryClient {
    private static Random r;
    private static final double SIZE = 100.0;
    private static PrintStream outStream;
    
    private static STSupplier RBTsup = new RedBlackTreeSupplier();
    private static STSupplier LLsup = new LinkedListSupplier();
    
    public static void main(String[] args) throws IOException {
        String fileName = "out.txt";
        outStream = new PrintStream(new File(fileName));
        //System.setOut(new PrintStream(new File(fileName)));
        
        r = new Random();
        
        long start = System.currentTimeMillis();
        test1();
        test2(100);
        
        test3();
        
        test4();
        test5();
        
        test6(5);
        
        long middle = System.currentTimeMillis();
        if(middle-start<500){
            //test7(30, 10, 3, 1, 0);
            test7(100, 10, 3, 1, 0);
            //test7(1000, 10, 3, 1, 0);
            //test7(10000, 10, 3, 1, 0);
            //test7(100000, 10, 3, 1, 0);
            
            //test8(5, 10, 3, 0, 2);
            //test8(100, 10, 3, 0, 2);
            //test8(250, 10, 3, 0, 2);
            //test8(1000, 10, 3, 0, 2);
            //test8(5000, 10, 3, 1, 0.5);
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
    
    private static void test1() {
        System.out.println("Test 1 started");
        test1h(new LinkedList<String, String>());
        test1h(new RedBlackTree<String, String>());
        test1h(new HashtableA<String, String>(RBTsup));
        test1h(new HashtableA<String, String>(LLsup));
        test1h(new HashtableB<String, String>());
        
        System.out.println("Test 1 completed successfully\n");
    }
    
    private static void test1h(ST<String, String> dict){
        System.out.printf("Test #1, %s%n", dict.toString());
        dict.put("orange", "fruit");
        dict.put("bread", "starch");
        dict.put("giraffe", "animal");
        dict.put("pear", "fruit");
        
        assert dict.get("bread").equals("starch");
        assert dict.get("giraffe").equals("animal");
        assert dict.get("orange").equals("fruit");
        assert dict.get("pear").equals("fruit");
        //check(dict);
    }
    
    private static void test2(int n){
        System.out.printf("Test 2 started with n=%d\n", n);
        
        test2h(new LinkedList<Integer, Integer>(), n);  
        test2h(new RedBlackTree<Integer, Integer>(), n);
        test2h(new HashtableA<Integer, Integer>(LLsup), n);
        test2h(new HashtableA<Integer, Integer>(RBTsup), n);
        test2h(new HashtableB<Integer, Integer>(), n);
        
        System.out.println("Test 2 completed successfully\n");
    }
    
    private static void test2h(ST<Integer, Integer> st, int n) {
        System.out.printf("Test #2, %s%n", st.toString());
        
        for (int i=0; i<n; i++) {
            int x = Math.abs(r.nextInt());
            st.put(x, x+1);
        }
        //check(st);
        
        Set<Integer> ls = st.getAllKeys();
        
        for (int x : ls)
            assert st.get(x)==x+1;
    }
    
    private static void test3() {
        int[] nl = {100, 400, 1600};
        //int[] nl = {100, 400, 1600, 6400};
        //int[] nl = {50000, 100000};
        
        double[] factorsA = {5.0};
        double[] marginsA = {0.5};
        
        STSupplier[] suppliers = (STSupplier[]) new STSupplier[] {RBTsup, LLsup};
        
        double[] maxsB = {0.75};
        double[] minsB = {0.25};
        
        for (int n : nl) {
            System.out.printf("Test 3 started with n=%d\n", n);
            
            if(n < 1500)
                test3h(new LinkedList<Integer, Integer>(), n);
            test3h(new RedBlackTree<Integer, Integer>(), n);
            
            for(STSupplier supplier : suppliers)
            for(double fac : factorsA)
            for(double marg : marginsA)
                test3h(new HashtableA<Integer, Integer>(supplier, 11, fac, marg), n);
            
            for(double max : maxsB)
            for(double min : minsB)
                test3h(new HashtableB<Integer, Integer>(max, min), n);
            
            System.out.println("Test 3 completed successfully\n");
        }
    }
    
    private static double test3h(ST<Integer, Integer> st, int n){
        long start = System.nanoTime();
        
        for(int i=0; i<n; i++){
            int x = Math.abs(r.nextInt());
            st.put(x, 0);
        }
        
        List<Integer> ls = new ArrayList<Integer>(st.getAllKeys());
        //assert ls.size() == n: ls.size()+":"+n;
        for(int i=0; i<10; i++){
            Collections.shuffle(ls);
            for(int x : ls)
                st.get(x);
        }
        
        long end = System.nanoTime();
        double diff = end-start;
        
        System.out.printf("%-28s -> %.4f (%6.0f) %n", st.toString(), diff/Math.pow(10,9), diff/n);
        return diff;
    }
    
    private static void test4(){
        System.out.println("Test 4 started");
        test4h(new LinkedList<String, String>());
        test4h(new RedBlackTree<String, String>());
        test4h(new HashtableA<String, String>(LLsup));
        test4h(new HashtableA<String, String>(RBTsup));
        test4h(new HashtableB<String, String>());
        
        System.out.println("Test 4 completed successfully\n");
    }
    
    private static void test4h(ST<String, String> dict){
        System.out.printf("Test #4, %s%n", dict.toString());
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
        assert dict.size() == 5 : dict.size();
        //check(dict);
        
        dict.put("bread", "wheat");
        dict.put("pear", "yummy");
        
        assert dict.get("bread").equals("wheat");
        assert dict.get("giraffe").equals("animal");
        assert dict.get("orange").equals("fruit");
        assert dict.get("pear").equals("yummy");
        assert dict.get("mango").equals("thing");
        assert dict.size() == 5;        
        //check(dict);
    }
    
    private static void test5(){
        System.out.println("Test 5 started");
        test5h(new LinkedList<String, String>());
        //test4h(new RedBlackTree<String, String>());
        //test4h(new HashtableA<String, String>(STMode.rbt));
        //test5h(new HashtableA<String, String>(new RedBlackTreeSupplier<String, String>()));
        test5h(new HashtableA<String, String>(LLsup));
        test5h(new HashtableB<String, String>());
        
        System.out.println("Test 5 completed successfully\n");
    }
    
    private static void test5h(ST<String, String> dict){
        System.out.printf("Test #5, %s%n", dict.toString());
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
        //check(dict);
        
        dict.remove("giraffe");
        dict.remove("mango");
        
        assert dict.get("bread").equals("starch");
        assert dict.get("orange").equals("fruit");
        assert dict.get("pear").equals("fruit");
        assert dict.size() == 3;        
        //check(dict);
    }
    
    private static void test6(int REP){
        System.out.printf("Test 6 started\n");
        
        double[] amounts = {2.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0};
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
        
        int[] nl = {500};
        
        for(int n : nl){
            System.out.printf("%nTest 6, n=%d%n", n);
            System.out.println("LL:     HT-LL:  HT-B:   RBT:    HT-RBT:");
            
            double[] sums = {0.0, 0.0, 0.0, 0.0, 0.0};
            
            for (int i=1; i<=REP; i++) {
                for (int j=0; j<5; j++) {
                    ST<Integer, Integer> st;
                    
                    if (j==0)
                        st = new LinkedList<Integer, Integer>();
                    else if (j==1)
                        st = new HashtableA<Integer, Integer>(LLsup);
                    else if (j==2)
                        st = new HashtableB<Integer, Integer>();
                    else if (j==3)
                        st = new RedBlackTree<Integer, Integer>();
                    else if (j==4)
                        st = new HashtableA<Integer, Integer>(RBTsup);
                    else
                        throw new RuntimeException("WTF");
                    
                    sums[j] += test6h(st, n, limits);
                }
                System.out.printf("%5.0f   %5.0f   %5.0f   %5.0f   %5.0f%n", sums[0]/i, sums[1]/i, sums[2]/i, sums[3]/i, sums[4]/i);
            }
            System.out.println("LL      HT-LL   HT-B    RBT     HT-RBT ");
        }
        
        System.out.println("Test 6 completed successfully\n");
    }
    
    private static double test6h(ST<Integer, Integer> st, int n, double[] limits){
        final int MAX = 3*n;
        
        boolean print = false;
        
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        long start = System.nanoTime();
        
        for(int i=0; i<n; i++){
            double d = r.nextDouble();
            
            int c = 0;
            
            while (c<limits.length && d<limits[c++]);
            
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
            } else if (c==3) {
                int v = (int) (r.nextDouble()*MAX);
                boolean x = map.containsValue(v);
                boolean y = st.containsValue(v);
                assert x == y;
            } else if (c==5) {
                Set<Integer> x = map.keySet();
                Set<Integer> y = st.getAllKeys();
                assert x.equals(y);
            } else {
                //if(print)
                    System.out.println("? " + c);
            }
        }
        
        long end = System.nanoTime();
        double diff = end-start;
        
        int x = st.size();
        int y = map.size();
        
        assert x==y: x+" "+y;
        
        return diff/n;
    } 
    
    private static void test7 (int REP, double... amounts) {
        double[] limits = getLimits(amounts);
        int n = 10000;
        
        System.out.printf("Test 7 started; n=%d, rep=%d%n", n, REP);
        long startMillis = System.currentTimeMillis();
        
        STSupplier[] confs = (STSupplier[]) new STSupplier[] {
            new LinkedListSupplier(),
                new RedBlackTreeSupplier(),
                new HashtableBSupplier(0.55, 0.45),
                new HashtableBSupplier(0.60, 0.40),
                new HashtableBSupplier(0.65, 0.38),
                new HashtableBSupplier(0.70, 0.36),
                new HashtableBSupplier(0.80, 0.30),
                new HashtableBSupplier(0.90, 0.27),
                new HashtableBSupplier(0.95, 0.15),
                new HashtableASupplier(LLsup),
                new HashtableASupplier(RBTsup),
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
        Average avg = new Average();
        
        TreeSet<Integer> set = new TreeSet<Integer>(st.getAllKeys());
        
        for(int i=0; i<n; i++){
            long start = System.nanoTime();
            
            double d = r.nextDouble();
            int c;
            for(c=0; c<limits.length; c++)
                if(d < limits[c])
                break;
            
            // If deletes are on and delete is chosen, decide whether to delete or put new
            // based on difference from intended size.
            if(c==3){
                double ratio = (set.size()/SIZE)/2;
                if(r.nextDouble()<ratio)
                    c = 3;
                else
                    c = 2;
            }
            
            if (c==0) { // Get
                if(set.size() > 0){
                    Integer rn;
                    do
                        rn = (int) (r.nextDouble()*5*SIZE);
                    while (!set.contains(rn));
                    assert set.contains(rn);
                    st.get(rn);
                }
            }else if (c==1) { //change existing
                if(set.size() > 0){
                    Integer k;
                    do
                        k = (int) (r.nextDouble()*5*SIZE);
                    while (!set.contains(k));
                    int v = r.nextInt();
                    st.put(k, v);
                }
            } else if (c==2){ //add new
                int k = (int) (r.nextDouble()*5*SIZE);
                int v = r.nextInt();
                set.add(k);
                st.put(k, v);
            } else if (c==3) { //delete
                if(set.size() > 0){
                    Integer k;
                    do
                        k = (int) (r.nextDouble()*5*SIZE);
                    while (!set.contains(k));
                    set.remove(k);
                    st.remove(k);
                }
            }
            long end = System.nanoTime();
            avg.add(end-start);
        }
        return avg.mean();
    }
    
    private static void test8(int REP, double... amounts){
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
            /*{.94, .16},
             {.94, .16},
             {.93, .17},
             {.92, .18},
             {.91, .19},
             {.90, .20},
             {}   */
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
                sts[j] = new HashtableB<Integer, Integer>(conf[0], conf[1]);
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
}

class Average{
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
        /*String[] strs = new String[list.size()];
        for (int i=0; i<list.size(); i++)
            strs[i] = String.format("%6.2f", list.get(i));*/
        return String.format("%6.3f (%6.3f)", mean(), stddevMean());
    }
}
/*
 enum STType {
 LL ("LL"), RBT ("RBT"), HT_B ("HT-B"), HT_LL ("HT:LL"), HT_RBT ("HT:RBT"), Mock ("Mock");
 private final String name;
 STType(String str){  name = str; }
 public String toString(){  return name; }
 }*/
/*
 class Config<K extends Comparable<K>, V> {
 STType type;
 double c1, c2;
 
 Config(STType t, double config1, double config2){
 type = t;
 c1 = config1;
 c2 = config2;
 }
 
 Config(STType t){  this(t, -1.0, -1.0); }
 
 ST<K, V> makeNew() {
 switch (type) {
 case LL:
 return new LinkedList<K, V>();
 case RBT:
 return new RedBlackTree<K, V>();
 case HT_B:
 return new HashtableB<K, V>();
 case HT_LL:
 return new HashtableA<K, V>(new LinkedListSupplier<K, V>());
 case HT_RBT:
 return new HashtableA<K, V>(new RedBlackTreeSupplier<K, V>());
 case Mock:
 return new Mock<K, V>();
 default:
 return new Mock<K, V>();
 }
 }
 public String toString(){
 if(c1 == -1.0 && c2 == -1.0)
 return type.toString();
 else
 return String.format("%s(%d/%d)", type.toString(), (int) (c1*100), (int) (c2*100));
 }
 }*/
/*
 class MultiStream extends OutputStream {
 private static OutputStream[] outs;
 
 public MultiStream(OutputStream... outputStreams) {
 outs = outputStreams;
 }
 
 public void close() throws IOException {
 for(OutputStream out : outs)
 out.close();
 }
 
 public void write(byte[] b, int off, int len) throws IOException {
 for(OutputStream out : outs)
 out.write(b, off, len);
 }
 
 public void write(byte[] b) throws IOException {
 for(OutputStream out : outs)
 out.write(b);
 }
 
 public void write(int b) throws IOException {
 for(OutputStream out : outs)
 out.write(b);
 }
 }*/


//System.out.println("80/30/50:  50/30:     60/40/50:  90/20/50:  50/20:   ");
//System.out.printf("%5.0f   %5.0f   %5.0f   %5.0f   %5.0f   %5.0f%n", sums[0]/i, sums[1]/i, sums[2]/i, sums[3]/i, sums[4]/i, sums[5]/i);

/*
 Object[][] ints = {set.toArray(), map.keySet().toArray(), st.getAll().toArray()};
 
 for(Object[] arr : ints){
 Arrays.sort(arr);
 //System.out.println(Arrays.toString(arr));
 }
 
 //System.out.println(Arrays.toString(set.toArray()));
 //System.out.println(Arrays.toString(map.keySet().toArray()));
 //System.out.println(Arrays.toString(st.getAll().toArray()));*/



//assert x==y && y==z: Arrays.toString(set.toArray())+" "+Arrays.toString(map.keySet().toArray())+" "+Arrays.toString(st.getAll().toArray());
//assert ((x==y) && (y==z)): set.toString()+" "+map.toString()+" "+st.toString();


/*
 mean = 0.0;
 for(int i=0; i<REP; i++)
 mean += test6h(new LinkedList<Integer, Integer>(), n);
 System.out.println(mean/10.0);
 
 mean = 0.0;
 for(int i=0; i<REP; i++)
 mean += test6h(new HashtableA<Integer, Integer>(STMode.ll), n);
 System.out.println(mean/10.0);
 
 
 mean = 0.0;
 for(int i=0; i<REP; i++)
 mean += test6h(new HashtableB<Integer, Integer>(), n);
 System.out.println(mean/10.0);
 */

/* if(i%3 == 0){
 System.out.println();
 
 for (int j=0; j<5; j++) {
 String s = "ERR";
 if(j==0) s = "Linked List:";
 if(j==1) s = "HashTable(ll):";
 if(j==2) s = "HashTableB:";
 if(j==3) s = "RedBlackTree:";
 if(j==4) s = "HashTable(rbt):";
 
 System.out.printf("%-17s %5.0f %n", s, sums[j]/REP);
 }
 
 System.out.println();
 }
 
 ,
 {.50, .10, .30},
 {.20, .50},
 {.10, .80},
 {.03, .80}
 
 
 */
/*if(conf.length == 2)
 s += String.format("   %2.0f/%2.0f:        ", conf[0]*100, conf[1]*100);
 if(conf.length == 3)
 s += String.format("   %2.0f/%2.0f/%2.0f:     ", conf[0]*100, conf[1]*100, conf[2]*100);*/


/*ST<Integer, Integer> st;
 * 
 if (j==confs.length)
 st = new Mock<Integer, Integer>();
 else{
 double[] conf = confs[j];
 if(conf.length == 2)
 st = new HashtableB<Integer, Integer>(conf[0], conf[1]);
 else if(conf.length == 3)
 st = new HashtableB<Integer, Integer>(conf[0], conf[1], conf[2]);
 else
 throw new RuntimeException("WTF");
 }*/

//System.out.printf("    (%4.2f%%)%n", avgDiff.mean()*100.0);
/*
 else if (c==7) {
 assert map.keySet().equals(st.getAllKeys());
 /*Object[][] ints = {map.keySet().toArray(), st.getAllKeys().toArray()};
 
 for(Object[] arr : ints)
 Arrays.sort(arr);
 
 for(int j=0; j<map.size(); j++){
 Object x = ints[0][j];
 Object y = ints[1][j];
 assert x.equals(y): String.format("%s : %s", x.toString(), y.toString());
 }*
 
 if(print)
 System.out.println("Getall!");
 } */
/*for(Config<Integer, Integer> conf : confs){
 System.out.printf("%-9s", conf.toString());
 }
 System.out.println();
 //System.out.println("LL:      HT-LL:  HT-B:   RBT:    HT-RBT:");*/
/*
 double[] sums = new double[confs.length];
 
 for (int i=1; i<=REP; i++) {
 for (int j=0; j<confs.length; j++) {
 ST<Integer, Integer> st;
 st = confs[j].makeNew();
 
 /*
 if (j==0)
 st = new LinkedList<Integer, Integer>();
 else if (j==1)
 st = new HashtableA<Integer, Integer>(STMode.ll);
 else if (j==2)
 st = new HashtableB<Integer, Integer>();
 else if (j==3)
 st = new RedBlackTree<Integer, Integer>();
 else if (j==4)
 st = new HashtableA<Integer, Integer>(STMode.rbt);
 else if (j==5)
 st = new Mock<Integer, Integer>();
 else
 throw new RuntimeException("WTF");
 
 sums[j] += test7h(st, n, limits);
 }
 if(i%25 == 0){
 for(int j=0; j<confs.length; j++)
 System.out.printf("%5.2f   ", (sums[j])/i);
 System.out.println();
 }
 }
 for(Config<Integer, Integer> conf : confs){
 System.out.printf("%-9s", conf.toString());
 }
 System.out.println();
 //System.out.println("LL       HT-LL   HT-B    RBT     HT-RBT");
 * */