package com.pheiffware.lib.physics;

import com.pheiffware.lib.utils.dataContainers.MapLinkedList;
import com.pheiffware.lib.utils.dataContainers.MapList;
import com.pheiffware.lib.utils.dataContainers.SetLinkedList;
import com.pheiffware.lib.utils.dataContainers.SetList;

import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertFalse;

/**
 * Tests the OpenLinkedList class
 * <p/>
 * Created by Steve on 5/25/2016.
 */
public class MapListTests
{
    @Test
    public void testMapList()
    {
        MapList<String, Integer> mapList = new MapLinkedList<>();
        mapList.append("List1", 1);
        mapList.append("List1", 2);
        mapList.append("List1", 3);
        mapList.append("List1", 4);
        mapList.append("List1", 5);
        mapList.append("List2", 1);
        mapList.append("List2", 2);
        mapList.append("List2", 3);
        mapList.add("List2", 0, 0);
        mapList.set("List2", 2, 200);
        //Creates list as side effect
        mapList.get("List3");
        assertEquals(1, (int) mapList.get("List1", 0));
        assertEquals(2, (int) mapList.get("List1", 1));
        assertEquals(3, (int) mapList.get("List1", 2));
        assertEquals(4, (int) mapList.get("List1", 3));
        assertEquals(5, (int) mapList.get("List1", 4));

        assertEquals(0, (int) mapList.get("List2", 0));
        assertEquals(1, (int) mapList.get("List2", 1));
        assertEquals(200, (int) mapList.get("List2", 2));
        assertEquals(3, (int) mapList.get("List2", 3));

        assertEquals(0, mapList.get("List3").size());

        assertEquals(3, mapList.size());
        Set<String> keys = mapList.keySet();
        assertTrue(keys.contains("List1"));
        assertTrue(keys.contains("List2"));
        assertTrue(keys.contains("List3"));
        assertFalse(keys.contains("List4"));
    }

    @Test
    public void testSetList()
    {
        SetList<TestObject> set = new SetLinkedList<>();
        set.append(new TestObject("Type1", 0));
        set.append(new TestObject("Type1", 1));
        set.append(new TestObject("Type1", 2));
        set.append(new TestObject("Type1", 3));

        set.append(new TestObject("Type2", 1));
        set.append(new TestObject("Type2", 2));
        set.add(0, new TestObject("Type2", 0));
        set.set(2, new TestObject("Type2", 200));

        Collection<List<TestObject>> lists = set.getLists();
        for (List<TestObject> list : lists)
        {
            if (list.size() == 4)
            {
                //List1
                assertEquals(0, list.get(0).value);
                assertEquals(1, list.get(1).value);
                assertEquals(2, list.get(2).value);
                assertEquals(3, list.get(3).value);
            }
            else if (list.size() == 3)
            {
                //List2
                assertEquals(0, list.get(0).value);
                assertEquals(1, list.get(1).value);
                assertEquals(200, list.get(2).value);
            }
        }
    }
}

class TestObject
{
    final String name;
    final int value;

    public TestObject(String name, int value)
    {
        this.name = name;
        this.value = value;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof TestObject))
        {
            return false;
        }
        return name.equals(((TestObject) o).name);
    }
}