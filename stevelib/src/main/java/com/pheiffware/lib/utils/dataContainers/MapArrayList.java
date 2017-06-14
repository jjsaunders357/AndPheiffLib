package com.pheiffware.lib.utils.dataContainers;

import java.util.ArrayList;
import java.util.List;

/**
 * Tool for handling the common case of having a map of lists and wanting to append/add values to lists stored at each key.
 * Backed by ArrayLists.
 * Created by Steve on 6/13/2017.
 */

public class MapArrayList<K, V> extends MapList<K, V>
{
    public MapArrayList()
    {
    }

    public MapArrayList(int capacity)
    {
        super(capacity);
    }

    @Override
    protected List<V> createEmptyList()
    {
        return new ArrayList<>();
    }
}
