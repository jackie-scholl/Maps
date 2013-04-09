
/**
 * Makes symbol tables.
 * 
 * @author Jackson
 *
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

