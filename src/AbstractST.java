/**
 * @author Jackson
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public abstract class AbstractST<K extends Comparable<K>, V> implements ST<K, V> {
	public boolean isEmpty() {
		return size() == 0;
	}

	public boolean containsKey(K key) throws NullPointerException {
		if (key == null)
            throw new NullPointerException("Key is not allowed to be null");
        return get(key) != null;
	}

	public boolean canRemove() {
		return true;
	}

	public void clear() {
		for (K key : getAllKeys())
			remove(key);
	}

}
