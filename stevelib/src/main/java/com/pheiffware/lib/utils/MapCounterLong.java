package com.pheiffware.lib.utils;

import java.util.HashMap;

/**
 * Convenience class for the very common case of needing to count values stored at various keys.  Deals with the awkwardness of initial null value for each key, by instead making
 * the key's value default to the value specified.
 * <p/>
 * Created by Steve on 4/15/2016.
 */
public class MapCounterLong<K> extends HashMap<K, Long>
{
    private final long defaultValue;

    public MapCounterLong()
    {
        this(0);
    }

    public MapCounterLong(long defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public MapCounterLong(int capacity, long defaultValue)
    {
        super(capacity);
        this.defaultValue = defaultValue;
    }

    public final long getCount(K key)
    {
        Long count = get(key);
        if (count == null)
        {
            count = defaultValue;
        }
        return count;
    }

    public final void incCount(K key)
    {
        put(key, getCount(key) + 1);
    }

    public final void addCount(K key, long add)
    {
        put(key, getCount(key) + add);
    }
}
