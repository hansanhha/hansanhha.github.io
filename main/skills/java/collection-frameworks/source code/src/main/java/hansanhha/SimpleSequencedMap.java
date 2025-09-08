package hansanhha;

public interface SimpleSequencedMap<K, V> extends SimpleMap<K, V> {

    SimpleSequencedMap<K, V> reversed();

    default SimpleMap.Entry<K, V> firstEntry() {
        var it = entrySet().iterator();
        return it.hasNext() ? new SimpleNullableKeyValueHolder<>(it.next()) : null;
    }

    default SimpleMap.Entry<K, V> lastEntry() {
        var it = reversed().entrySet().iterator();
        return it.hasNext() ? new SimpleNullableKeyValueHolder<>(it.next()) : null;
    }

    default SimpleMap.Entry<K, V> pollFirstEntry() {
        var it = entrySet().iterator();

        if (it.hasNext()) {
             var entry = new SimpleNullableKeyValueHolder<>(it.next());
             it.remove();
             return entry;
        }

        return null;
    }

    default SimpleMap.Entry<K, V> pollLastEntry() {
        var it = reversed().entrySet().iterator();

        if (it.hasNext()) {
            var entry = new SimpleNullableKeyValueHolder<>(it.next());
            it.remove();
            return entry;
        }

        return null;

    }

    default V putFirst(K k, V v) {
        throw new UnsupportedOperationException();
    }

    default V putLast(K k, V v) {
        throw new UnsupportedOperationException();
    }

//    default SequencedSet<K> sequencedKeySet() {
//
//    }
//
//    default SequencedCollection<V> sequencedCollection() {
//
//    }
//
//    default SequencedSet<MyMap.Entry<K, V>> sequencedEntrySet() {
//
//    }
}
