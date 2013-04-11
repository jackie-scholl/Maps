import java.util.*;
import java.io.*;

@SuppressWarnings("javadoc")
public class DictionaryClient {
    private static Random r;
    private static final double SIZE = 100.0;
    private static PrintStream outStream;

    private static DictionarySupplier RBTsup = new RedBlackTreeSupplier();
    private static DictionarySupplier LLsup = new LinkedListSupplier();

    private static DictionarySupplier[] mainDictSups = new DictionarySupplier[] {
        LLsup,
        RBTsup,
        new ProbingHashtableSupplier(),
        new ChainingHashtableSupplier(LLsup),
        new ChainingHashtableSupplier(RBTsup),
        new ChainingHashtableSupplier(new ProbingHashtableSupplier())
    };
    
    public static final boolean VERBOSE = true;

    public static void main(String[] args) throws IOException {
        String fileName = "out.txt";
        outStream = new PrintStream(new File(fileName));

        long start = System.currentTimeMillis();

        for (DictionarySupplier stSup : mainDictSups) {
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

    private static void test1h(DictionarySupplier stSup) {
        Dictionary<String, String> dict = stSup.getNew();

        dict.put("orange", "fruit");
        dict.put("bread", "starch");
        dict.put("giraffe", "animal");
        dict.put("pear", "fruit");

        assert dict.get("bread").equals("starch");
        assert dict.get("giraffe").equals("animal");
        assert dict.get("orange").equals("fruit");
        assert dict.get("pear").equals("fruit");

        if (VERBOSE)
            System.out.printf("Test #1: passed%n");
    }

    private static void test2h(DictionarySupplier stSup){
        Dictionary<String, String> dict = stSup.getNew();

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

        if (VERBOSE)
            System.out.printf("Test #2: passed%n", dict.toString());
    }

    private static void test3h(DictionarySupplier stSup){
        Dictionary<String, String> dict = stSup.getNew();

        if (!dict.canRemove()){
            if (VERBOSE)
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

        if (VERBOSE)
            System.out.printf("Test #3: passed%n");
    }

    private static void test4h(DictionarySupplier stSup, int n) {
        Dictionary<Integer, Integer> st = stSup.getNew();

        for (int i=0; i<n; i++) {
            int x = Math.abs(r.nextInt());
            st.put(x, x+1);
        }

        for (int x : st.getAllKeys()) {
            assert st.get(x)==x+1;
            assert st.containsKey(x);
            assert st.containsValue(x+1);
        }

        if (VERBOSE)
            System.out.printf("Test #4, n=%d: passed%n", n);
    }

    private static void test6h(DictionarySupplier stSup, int n) {
        final int MAX = 2*n;
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        Dictionary<Integer, Integer> st = stSup.getNew();

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

        if (VERBOSE)
            System.out.printf("Test #6, n=%d: passed%n", n);
    } 

    private static void test7(int REP, double... amounts) {
        int PRINTS;
        if (VERBOSE)
            PRINTS = 100;
        else
            PRINTS = 5;
        
        double[] limits = getLimits(amounts);
        int n = 10000;

        System.out.printf("Test 7 started; n=%d, rep=%d%n", n, REP);
        long startMillis = System.currentTimeMillis();

        DictionarySupplier[] confs = (DictionarySupplier[]) new DictionarySupplier[] {
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
        for (DictionarySupplier conf : confs) {
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
                Dictionary<Integer, Integer> st = confs[j].getNew();
                double mean = test7h(st, n, limits);
                outStream.printf("%.5f,", mean);
                lists[j].add(mean);
            }

            outStream.println();

            if (i%(REP/PRINTS+1) == 0) {
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

    private static double test7h(Dictionary<Integer, Integer> st, int n, double[] limits){
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