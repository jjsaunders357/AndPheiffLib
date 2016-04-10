package com.pheiffware.lib.graphics.managed.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Comment me!
 * <p/>
 * Created by Steve on 4/10/2016.
 */
public class StaticObjectHandle
{
    final List<StaticMeshHandle> staticMeshHandles = new ArrayList<>();

    public void addMeshHandle(StaticMeshHandle staticMeshHandle)
    {
        staticMeshHandles.add(staticMeshHandle);
    }
}
