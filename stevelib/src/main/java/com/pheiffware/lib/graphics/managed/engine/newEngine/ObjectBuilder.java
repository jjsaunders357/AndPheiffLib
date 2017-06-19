package com.pheiffware.lib.graphics.managed.engine.newEngine;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Steve on 6/19/2017.
 */

class ObjectBuilder
{
    Collection<MeshHandle> meshHandles = new LinkedList<>();
    ObjectHandle objectHandle = new ObjectHandle();

    public void setupHandle()
    {
        objectHandle.meshHandles = new MeshHandle[meshHandles.size()];
        int index = 0;
        for (MeshHandle handle : meshHandles)
        {
            objectHandle.meshHandles[index] = handle;
            index++;
        }
    }

    public void addMesh(MeshHandle meshHandle)
    {
        meshHandles.add(meshHandle);
    }
}
