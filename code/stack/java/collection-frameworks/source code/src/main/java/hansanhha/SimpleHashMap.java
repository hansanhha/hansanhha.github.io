package hansanhha;

import java.util.Set;

public class SimpleHashMap<K, V> extends SimpleAbstractMap<K, V>
        implements SimpleMap<K, V>, Cloneable {



    @Override
    public Set<Entry<K, V>> entrySet() {
        return Set.of();
    }


}
