package com.pheiffware.lib.utils.dataContainers;

import java.util.Collection;
import java.util.List;

/**
 * Set version of a MapList.  Given a value it looks up the list related to that type.  It then adds/appends the value to list.
 * Created by Steve on 6/13/2017.
 */

public abstract class SetList<V>
{
    /**
     * Gets the list corresponding to the value and adds value to it.
     *
     * @param index location to add value in the list
     * @param value value to add
     */
    public void add(int index, V value)
    {
        getMap().add(value, index, value);
    }

    /**
     * Gets the list corresponding to the value and appends the value to it.
     *
     * @param value value to add
     */
    public void append(V value)
    {
        getMap().append(value, value);
    }

    /**
     * Gets the list corresponding to the value and sets the given index to the value.
     *
     * @param index location to add value in the list
     * @param value value to add
     */
    public void set(int index, V value)
    {
        getMap().set(value, index, value);
    }

    /**
     * Gets the list corresponding to the value.
     *
     * @param value value to add
     */
    public List<V> get(V value)
    {
        return getMap().get(value);
    }

    /**
     * Return a collection of the lists
     * @return
     */
    public Collection<List<V>> getLists()
    {
        return getMap().values();
    }

    /**
     * Implementing classes should return the MapList backing the class.
     *
     * @return backing MapList
     */
    protected abstract MapList<V, V> getMap();
}