package hansanhha;

import java.util.Objects;

public final class SimpleNullableKeyValueHolder<K, V> implements SimpleMap.Entry<K, V>{

    final K key;
    final V value;

    public SimpleNullableKeyValueHolder(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public SimpleNullableKeyValueHolder(SimpleMap.Entry<K, V> entry) {
        Objects.requireNonNull(entry);
        key = entry.getKey();
        value = entry.getValue();
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException("not supported");
    }

    public boolean equals(Object o) {
        if (o instanceof SimpleMap.Entry<?, ?> e) {
            return Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue());
        }

        return false;
    }

    private int hash(Object obj) {
        return obj == null ? 0 : obj.hashCode();
    }

    public int hashCode() {
        return hash(key) ^ hash(value);
    }

    public String toString() {
        return key + "=" + value;
    }

}
