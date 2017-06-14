package com.pheiffware.lib.utils.dataContainers;

/**
 * Convenience class for the very common case of needing to count values stored at various keys.  Deals with the awkwardness of initial null value for each key, by instead making
 * the key's value default to the value specified.
 * <p/>
 * Created by Steve on 4/15/2016.
 */
public class MapCounterLong<K> extends MapCounter<K, Long>
{
    public MapCounterLong()
    {
        this(0);
    }

    public MapCounterLong(long defaultValue)
    {
        super(defaultValue);
    }

    public MapCounterLong(int capacity, long defaultValue)
    {
        super(capacity, defaultValue);
    }

    public final void incCount(K key)
    {
        map.put(key, get(key) + 1);
    }

    public final void addCount(K key, long add)
    {
        map.put(key, get(key) + add);
    }

}
