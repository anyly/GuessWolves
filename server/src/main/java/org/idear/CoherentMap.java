package org.idear;

/**
 * Created by idear on 2018/9/23.
 */

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Key-Value Only Map
 * also record order of put into
 */
public class CoherentMap<K, V> extends LinkedHashMap<K, V> {
    protected LinkedHashMap<V, K> valueMap = new LinkedHashMap<>();

    @Override
    public V put(K key, V value) {
        V v = super.put(key, value);
        valueMap.put(value, key);
        return v;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m);
        Map.Entry<? extends K, ? extends V> entry = null;
        Iterator<? extends Map.Entry<? extends K, ? extends V>> iterator = m.entrySet().iterator();
        while ((entry = iterator.next()) != null) {
            valueMap.put(entry.getValue(), entry.getKey());
        }
    }

    @Override
    public V remove(Object key) {
        V value = super.remove(key);
        valueMap.remove(value);
        return value;
    }

    @Override
    public Object clone() {
        CoherentMap<K, V> coherentMap = new CoherentMap<>();
        coherentMap.putAll(this);
        return coherentMap;
    }

    public K key(V value) {
        return valueMap.get(value);
    }
}
