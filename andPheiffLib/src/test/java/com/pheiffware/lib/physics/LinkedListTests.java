package com.pheiffware.lib.physics;

import com.pheiffware.lib.utils.OpenLinkedList;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Tests the OpenLinkedList class
 * <p/>
 * Created by Steve on 5/25/2016.
 */
public class LinkedListTests
{
    @Test
    public void add()
    {
        OpenLinkedList<Integer> list = new OpenLinkedList<>();
        list.addToFront(2);
        list.addToBack(3);
        list.addToFront(1);
        list.addToBack(4);
        list.addToFront(0);
        list.addToBack(5);
        for (int i = 0; i < 6; i++)
        {
            assertEquals((int) list.getFirst(), i);
            list.moveToBack(list.getFirstNode());
        }
    }
}
