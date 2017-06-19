package com.pheiffware.lib.graphics.managed.engine.newEngine;

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
}
