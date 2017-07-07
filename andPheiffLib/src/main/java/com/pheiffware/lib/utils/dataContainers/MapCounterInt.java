package com.pheiffware.lib.utils.dataContainers;

/**
 * Convenience class for the very common case of needing to count values stored at various keys.  Deals with the awkwardness of initial null value for each key, by instead making
 * the key's value default to the value specified.
 * <p/>
 * Created by Steve on 4/15/2016.
 */
public class MapCounterInt<K> extends MapCounter<K, Integer>
{
    public MapCounterInt()
    {
        this(0);
    }

    public MapCounterInt(int defaultValue)
    {
        super(defaultValue);
    }

    public MapCounterInt(int capacity, int defaultValue)
    {
        super(capacity, defaultValue);
    }

    public final void incCount(K key)
    {
        map.put(key, get(key) + 1);
    }

    public final void addCount(K key, int add)
    {
        map.put(key, get(key) + add);
    }

}
