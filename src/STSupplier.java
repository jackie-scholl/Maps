
/**
 * Makes symbol tables.
 * 
 * @author Jackson
 *
 * @param <K> Key type of resulting symbol tables
 * @param <V> Value type of resulting Symbol tables
 */
public interface STSupplier {
    /**
     * Returns a new symbol table
     * 
     * @return new symbol table
     */
    <K extends Comparable<K>, V> ST<K, V> getNew();
    
}

