package com.pheiffware.lib.utils.dataContainers;

/**
 * Set version of a MapList.  Given a value it looks up the list related to that type.  It then adds/appends the value to list.
 * Backed by LinkedList.
 * Created by Steve on 6/13/2017.
 */

public class SetLinkedList<V> extends SetList<V>
{
    private final MapList<V, V> map;

    public SetLinkedList()
    {
        map = new MapLinkedList<>();
    }

    public SetLinkedList(int capacity)
    {
        map = new MapLinkedList<>(capacity);
    }

    @Override
    protected MapList<V, V> getMap()
    {
        return map;
    }
}