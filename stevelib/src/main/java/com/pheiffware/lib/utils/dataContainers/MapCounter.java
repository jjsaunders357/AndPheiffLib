package com.pheiffware.lib.utils.dataContainers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Convenience class for the very common case of needing to count values stored at various keys.  Deals with the awkwardness of initial null value for each key, by instead making
 * the key's value default to the value specified.
 * <p/>
 * Created by Steve on 4/15/2016.
 */
public class MapCounter<K, N extends Number>
{
    private final N defaultValue;
    protected final HashMap<K, N> map;

    public MapCounter(N defaultValue)
    {
        this.defaultValue = defaultValue;
        map = new HashMap<>();
    }

    public MapCounter(int capacity, N defaultValue)
    {
        this.defaultValue = defaultValue;
        map = new HashMap<>(capacity);
    }

    public final N get(K key)
    {
        N count = map.get(key);
        if (count == null)
        {
            count = defaultValue;
        }
        return count;
    }

    public void clear()
    {
        map.clear();
    }

    public Set<K> keySet()
    {
        return map.keySet();
    }

    public Set<Map.Entry<K, N>> entrySet()
    {
        return map.entrySet();
    }
}
