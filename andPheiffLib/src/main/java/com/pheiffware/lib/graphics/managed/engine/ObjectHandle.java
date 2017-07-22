package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.program.RenderProperty;

/**
 * Created by Steve on 6/19/2017.
 */

public class ObjectHandle
{
    MeshHandle[] meshHandles;

    public void setProperty(RenderProperty renderProperty, Object value)
    {
        for (MeshHandle meshHandle : meshHandles)
        {
            meshHandle.setProperty(renderProperty, value);
        }
    }

    public ObjectHandle copy()
    {
        //TODO: There should be central modelMatrix value shared by all meshes
        ObjectHandle copy = new ObjectHandle();
        copy.meshHandles = new MeshHandle[meshHandles.length];
        for (int i = 0; i < meshHandles.length; i++)
        {
            copy.meshHandles[i] = meshHandles[i].copy();
        }
        return copy;
    }
}
