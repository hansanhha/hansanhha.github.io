package hansanhha;

import java.util.*;

public abstract class SimpleAbstractMap<K, V> implements SimpleMap<K, V> {

    @Override
    public int size() {
        return entrySet().size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        var it = entrySet().iterator();
        if (key == null) {
            while (it.hasNext()) {
                var e = it.next();
                if (e.getKey() == null) return true;
            }
        } else {
            while (it.hasNext()) {
                var e = it.next();
                if (e.getKey().equals(key)) return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        var it = entrySet().iterator();
        if (value == null) {
            while (it.hasNext()) {
                var e = it.next();
                if (e.getValue() == null) return true;
            }
        } else {
            while (it.hasNext()) {
                var e = it.next();
                if (e.getValue().equals(value)) return true;
            }
        }

        return false;
    }

    @Override
    public V get(Object key) {
        var it = entrySet().iterator();
        if (key == null) {
            while (it.hasNext()) {
                var e = it.next();
                if (e.getKey() == null) return e.getValue();
            }
        } else {
            while (it.hasNext()) {
                var e = it.next();
                if (e.getKey().equals(key)) return e.getValue();
            }
        }

        return null;
    }

    // 수정 연산

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object key) {
        var it = entrySet().iterator();
        SimpleMap.Entry<K, V> correctEntry = null;
        if (key == null) {
            while (correctEntry == null && it.hasNext()) {
                var e = it.next();
                if (e.getKey() == null) {
                    correctEntry = e;
                }
            }
        } else {
            while (correctEntry == null && it.hasNext()) {
                var e = it.next();
                if (e.getKey().equals(key)) {
                    correctEntry = e;
                }
            }
        }

        V oldValue = null;
        if (correctEntry != null) {
            oldValue = correctEntry.getValue();
            it.remove();
        }

        return oldValue;
    }

    @Override
    public void putAll(SimpleMap<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
    }

    @Override
    public void clear() {
        entrySet().clear();
    }

    // Views

    Set<K> keySet;
    Collection<V> values;

    @Override
    public Set<K> keySet() {
        Set<K> ks = keySet;
        if (ks == null) {
            ks = new AbstractSet<K>() {
                @Override
                public Iterator<K> iterator() {
                    return new Iterator<K>() {
                        private Iterator<Entry<K, V>> it = entrySet().iterator();

                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public K next() {
                            return it.next().getKey();
                        }

                        @Override
                        public void remove() {
                            it.remove();
                        }
                    };
                }

                @Override
                public int size() {
                    return SimpleAbstractMap.this.size();
                }

                @Override
                public boolean isEmpty() {
                    return SimpleAbstractMap.this.isEmpty();
                }

                @Override
                public void clear() {
                    SimpleAbstractMap.this.clear();
                }

                @Override
                public boolean contains(Object k) {
                    return SimpleAbstractMap.this.containsKey(k);
                }
            };
            keySet = ks;
        }
        return ks;
    }

    @Override
    public Collection<V> values() {
        Collection<V> vals = values;
        if (vals == null) {
            vals = new AbstractCollection<V>() {
                @Override
                public Iterator<V> iterator() {
                    return new Iterator<V>() {
                        private Iterator<Entry<K, V>> it = entrySet().iterator();

                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public V next() {
                            return it.next().getValue();
                        }

                        @Override
                        public void remove() {
                            it.remove();
                        }
                    };
                }

                @Override
                public int size() {
                    return SimpleAbstractMap.this.size();
                }

                @Override
                public boolean isEmpty() {
                    return SimpleAbstractMap.this.isEmpty();
                }

                @Override
                public void clear() {
                    SimpleAbstractMap.this.clear();
                }

                @Override
                public boolean contains(Object k) {
                    return SimpleAbstractMap.this.containsKey(k);
                }
            };
            values = vals;
        }

        return vals;
    }

    public abstract Set<Entry<K, V>> entrySet();

    // 비교, 해싱

    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (! (o instanceof Map<?, ?> m))
            return false;
        if (m.size() != size())
            return false;

        try {
            for (Entry<K, V> e : entrySet()) {
                K key = e.getKey();
                V value = e.getValue();

                if (value == null) {
                    if (m.get(key) != null || !m.containsKey(key))
                        return false;
                } else {
                    if (!value.equals(m.get(key)))
                        return false;
                }
            }
        } catch (ClassCastException | NullPointerException unused) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int h = 0;
        for (Entry<K, V> e : entrySet()) {
            h += e.hashCode();
        }
        return h;
    }

    public String toString() {
        var it = entrySet().iterator();
        if (!it.hasNext())
            return "empty";

        var sb = new StringBuilder();
        sb.append('{');
        for (; ; ) {
            var e = it.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key == this ? "(this Map)" : key);
            sb.append("=");
            sb.append(value == this ? "(this Map)" : value);
            if(!it.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
        }
    }

    protected Object clone() throws CloneNotSupportedException {
        SimpleAbstractMap<?, ?> clone = (SimpleAbstractMap<?, ?>) super.clone();
        clone.keySet = null;
        clone.values = null;
        return clone;
    }


}
