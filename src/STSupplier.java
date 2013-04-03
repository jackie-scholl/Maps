
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
    
    /**
     * Returns whether or not this supplier makes symbol tables that support the {@code delete} operation.
     * 
     * @return whether or not the symbol tables can delete.
     */
    boolean canDelete();
}

