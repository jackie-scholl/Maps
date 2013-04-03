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
            LLsup, RBTsup, new HashtableASupplier(LLsup), new HashtableASupplier(RBTsup), new HashtableBSupplier() };
    
    private static STSupplier[] deletableSTSups = new STSupplier[] {
            LLsup, new HashtableASupplier(LLsup), new HashtableBSupplier() };
    
    public static void main(String[] args) throws IOException {
        String fileName = "out.txt";
        outStream = new PrintStream(new File(fileName));
        
        r = new Random(1176072517698283250L);
        
        long start = System.currentTimeMillis();
        test1();
        test2();
        test3();
        
        test4(100);
        
        test6(5);
        
        long middle = System.currentTimeMillis();
        if(middle-start<1000){
            test7(30, 10, 3, 0);
            //test7(100, 10, 3, 0);
            //test7(1000, 10, 3, 1, 0);
            //test7(10000, 10, 3, 1, 0);
            //test7(100000, 10, 3, 1, 0);
            
            test8(5, 10, 3, 2);
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
        int testNum = 1;
        System.out.printf("Test %d started%n", testNum);
        
        for (STSupplier stSup : mainSTSups) {
            ST<String, String> st = stSup.getNew();
            System.out.printf("Test #%d, %s%n", testNum, st.toString());
            test1h(st);
        }
        
        System.out.printf("Test %d completed successfully%n%n", testNum);
    }
    
    private static void test1h(ST<String, String> dict){
        dict.put("orange", "fruit");
        dict.put("bread", "starch");
        dict.put("giraffe", "animal");
        dict.put("pear", "fruit");
        
        assert dict.get("bread").equals("starch");
        assert dict.get("giraffe").equals("animal");
        assert dict.get("orange").equals("fruit");
        assert dict.get("pear").equals("fruit");
    }
    
    private static void test2(){
        int testNum = 2;
        System.out.printf("Test %d started%n", testNum);
        
        for (STSupplier stSup : mainSTSups) {
            ST<String, String> st = stSup.getNew();
            System.out.printf("Test #%d, %s%n", testNum, st.toString());
            test2h(st);
        }
        
        System.out.printf("Test %d completed successfully%n%n", testNum);
    }
    
    private static void test2h(ST<String, String> dict){
        //System.out.printf("Test #2, %s%n", dict.toString());
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
    }
    
    private static void test3(){
        int testNum = 3;
        System.out.printf("Test %d started%n", testNum);
        
        for (STSupplier stSup : mainSTSups) {
            ST<String, String> st = stSup.getNew();
            System.out.printf("Test #%d, %s%n", testNum, st.toString());
            test3h(st);
        }
        
        System.out.printf("Test %d completed successfully%n%n", testNum);
    }
    
    private static void test3h(ST<String, String> dict) {
        if (!dict.canRemove())
            return;
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
        assert dict.size() == 3;
    }
    
    private static void test4(int n){
        System.out.printf("Test 4 started with n=%d\n", n);
        
        for (STSupplier stSup : mainSTSups)
            test4h(stSup.<Integer, Integer>getNew(), n);
        
        System.out.println("Test 4 completed successfully\n");
    }
    
    private static void test4h(ST<Integer, Integer> st, int n) {
        System.out.printf("Test #4, %s%n", st.toString());
        
        for (int i=0; i<n; i++) {
            int x = Math.abs(r.nextInt());
            st.put(x, x+1);
        }
        
        for (int x : st.getAllKeys())
            assert st.get(x)==x+1;
    }
    
    private static void test6(int rep){
        int n = 500;
        System.out.printf("Test 6 started, n=%d%n", n);
        
        for (int i=0; i<rep; i++) {
            test6h(mainSTSups, n);
        }
        
        System.out.println("Test 6 completed successfully\n");
    }
    
    private static void test6h(STSupplier[] stSups, int n) {
        final int MAX = 2*n;
        int num = stSups.length;
        
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        
        ST<Integer, Integer> [] sts = (ST<Integer, Integer>[]) new ST[num];
        for(int i=0; i<num; i++)
            sts[i] = stSups[i].getNew();
        
        for(int i=0; i<n; i++){
            int c = (int) (r.nextDouble()*6);
            
            if (c==0) { // Get
                int k = (int) (r.nextDouble()*MAX);
                Integer x = map.get(k);
                for (ST<Integer, Integer> st : sts) {
                    Integer y = st.get(k);
                    if(x == null)
                        assert y == null;
                    else
                        assert x.equals(y);
                }
            } else if (c==1) { //put
                int k = (int) (r.nextDouble()*MAX);
                int v = (int) (r.nextDouble()*MAX);
                Integer x = map.put(k, v);
                
                for (ST<Integer, Integer> st : sts) {
                    Integer y = st.put(k, v);
                    if(x == null)
                        assert y == null;
                    else
                        assert x.equals(y);
                }                
            } else if (c==2) { // size, isEmpty
                for (ST<Integer, Integer> st : sts) {
                    assert map.size() == st.size();
                    assert map.isEmpty() == st.isEmpty();
                }
            } else if (c==3) {
                int k = (int) (r.nextDouble()*MAX);
                boolean x = map.containsKey(k);
                for (ST<Integer, Integer> st : sts) {
                    boolean y = st.containsKey(k);
                    assert x == y;
                }
            } else if (c==4) {
                int v = (int) (r.nextDouble()*MAX);
                boolean x = map.containsValue(v);
                for (ST<Integer, Integer> st : sts) {
                    boolean y = st.containsValue(v);
                    assert x == y;
                }
            } else if (c==5) {
                Set<Integer> x = map.keySet();
                for (ST<Integer, Integer> st : sts) {
                    Set<Integer> y = st.getAllKeys();
                    assert x.equals(y);
                }
            } else {
                System.out.println("? " + c);
            }
        }
    } 
    
    private static void test7(int REP, double... amounts) {
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
//610+270+444+167+35+207+99+25