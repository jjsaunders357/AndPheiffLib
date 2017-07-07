package com.pheiffware.lib.utils.dataContainers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tool for handling the common case of having a map of lists and wanting to append/add values to lists stored at each key.
 * Accessing a list, which hasn't been used/accessed before, creates a empty one.  When iterating all accessed lists are visited.
 * Created by Steve on 6/13/2017.
 */

public abstract class MapList<K, V>
{
    private final Map<K, List<V>> map;

    public MapList()
    {
        map = new HashMap<>();
    }

    public MapList(int capacity)
    {
        map = new HashMap<>(capacity);
    }

    /**
     * Get the list referenced by key and add value to it at the specified index.
     *
     * @param key   list to add to
     * @param index location to add value to list
     * @param value value to add to list
     */
    public void add(K key, int index, V value)
    {
        List<V> list = get(key);
        list.add(index, value);
    }

    /**
     * Get the list referenced by key and append value to it.
     *
     * @param key   list to add to
     * @param value value to add to list
     */
    public void append(K key, V value)
    {
        List<V> list = get(key);
        list.add(value);
    }

    /**
     * Get the list referenced by key and set value at the given index.
     *
     * @param key   list to add to
     * @param index location to add value to list
     * @param value value to add to list
     */
    public void set(K key, int index, V value)
    {
        List<V> list = get(key);
        list.set(index, value);
    }

    /**
     * Get the list stored at the given key.  If no list exists this creates an empty one AND stores it.
     *
     * @param key list to retrieve
     * @return the list referenced by the key
     */
    public final List<V> get(K key)
    {
        List<V> list = map.get(key);
        if (list == null)
        {
            list = createEmptyList();
            map.put(key, list);
        }
        return list;
    }

    /**
     * Get an element of the list for a given key and index.
     *
     * @param key   list retrieve
     * @param index index in list to retrieve
     */
    public final V get(K key, int index)
    {
        return get(key).get(index);
    }

    public Set<Map.Entry<K, List<V>>> entrySet()
    {
        return map.entrySet();
    }

    public Set<K> keySet()
    {
        return map.keySet();
    }

    public int size()
    {
        return map.size();
    }

    public Collection<List<V>> values()
    {
        return map.values();
    }

    public void clear()
    {
        map.clear();
    }

    /**
     * Implementing classes override this to define how to construct a new empty list object.
     *
     * @return
     */
    protected abstract List<V> createEmptyList();

}
