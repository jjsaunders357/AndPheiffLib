package com.pheiffware.lib.utils;

import java.util.HashMap;

/**
 * Convenience class for the very common case of needing to count values stored at various keys.  Deals with the awkwardness of initial null value for each key, by instead making
 * the key's value default to the value specified.
 * <p/>
 * Created by Steve on 4/15/2016.
 */
public class MapCounter<K> extends HashMap<K, Integer>
{
    private final int defaultValue;

    public MapCounter(int defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public final int getCount(K key)
    {
        Integer count = get(key);
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

    public final void addCount(K key, int add)
    {
        put(key, getCount(key) + add);
    }
}
