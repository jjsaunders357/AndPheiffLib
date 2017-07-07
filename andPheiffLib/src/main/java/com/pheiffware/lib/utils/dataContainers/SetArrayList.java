package com.pheiffware.lib.utils.dataContainers;

/**
 * Set version of a MapList.  Given a value it looks up the list related to that type.  It then adds/appends the value to list.
 * Backed by ArrayList.
 * Created by Steve on 6/13/2017.
 */

public class SetArrayList<V> extends SetList<V>
{
    private final MapList<V, V> map;

    public SetArrayList()
    {
        map = new MapArrayList<>();
    }

    public SetArrayList(int capacity)
    {
        map = new MapArrayList<>(capacity);
    }

    @Override
    protected MapList<V, V> getMap()
    {
        return map;
    }
}