package hansanhha;

import java.util.*;
import java.util.function.BiConsumer;

public interface SimpleMap<K, V> {

    // 검색 작업 (Query Operation)

    int size();

    boolean isEmpty();

    boolean containsKey(Object key);

    boolean containsValue(Object value);

    V get(Object key);

    V put(K key, V value);

    V remove(Object key);

    // 벌크 연산 (Bulk Operation)

    void putAll(SimpleMap<? extends K, ? extends V> m);

    void clear();

    // Views

    Set<K> keySet();

    Collection<V> values();

    Set<Entry<K,V>> entrySet();

    interface Entry<K, V> {

        K getKey();

        V getValue();

        V setValue(V value);

        boolean equals(Object o);

        int hashCode();
    }

    // 비교, 해싱

    boolean equals(Object o);

    int hashCode();

    // default 메서드

    default void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);

        for (Entry<K, V> entry : entrySet()) {
            K k;
            V v;
            try {
                k = entry.getKey();
                v = entry.getValue();
            } catch (IllegalStateException ise) {
                throw new ConcurrentModificationException(ise);
            }
            action.accept(k, v);
        }
    }
}
