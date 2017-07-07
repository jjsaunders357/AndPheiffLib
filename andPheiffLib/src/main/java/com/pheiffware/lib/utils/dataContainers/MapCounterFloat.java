package com.pheiffware.lib.utils.dataContainers;

/**
 * Convenience class for the very common case of needing to count values stored at various keys.  Deals with the awkwardness of initial null value for each key, by instead making
 * the key's value default to the value specified.
 * <p/>
 * Created by Steve on 4/15/2016.
 */
public class MapCounterFloat<K> extends MapCounter<K, Float>
{
    public MapCounterFloat()
    {
        this(0f);
    }

    public MapCounterFloat(float defaultValue)
    {
        super(defaultValue);
    }

    public MapCounterFloat(int capacity, float defaultValue)
    {
        super(capacity, defaultValue);
    }

    public final void addCount(K key, float add)
    {
        map.put(key, get(key) + add);
    }

    public final void mulCount(K key, float mul)
    {
        map.put(key, get(key) * mul);
    }
}
