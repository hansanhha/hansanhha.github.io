package hansanhha;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedMap;

public interface SimpleSortedMap<K, V> extends SimpleSequencedMap<K, V> {

    Comparator<? super K> comparator();

    SortedMap<K, V> subMap(K fromKey, K toKey);

    SortedMap<K, V> headMap(K toKey);

    SortedMap<K, V> tailMap(K fromKey);

    K firstKey();

    K lastKey();

    Set<K> keySet();

    Collection<V> values();

    Set<Entry<K, V>> entrySet();

    default V putFirst(K k, V v) {
        throw new UnsupportedOperationException();
    }

    default V putLast(K k, V v) {
        throw new UnsupportedOperationException();
    }

//    default SortedMap<K, V> reversed() {
//
//    }
}
