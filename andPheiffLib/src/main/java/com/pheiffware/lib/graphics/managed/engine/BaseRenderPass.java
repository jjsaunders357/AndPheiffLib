package com.pheiffware.lib.graphics.managed.engine;

/**
 * Created by Steve on 6/19/2017.
 */

public abstract class BaseRenderPass
{
    protected boolean filter(ObjectHandle objectHandle)
    {
        return true;
    }

    protected void preRender()
    {
    }

    protected void postRender()
    {
    }

    protected void renderObject(MeshHandle[] meshHandles)
    {
        for (MeshHandle meshHandle : meshHandles)
        {
            meshHandle.drawTriangles();
        }
    }

}
