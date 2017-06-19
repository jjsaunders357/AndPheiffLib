package com.pheiffware.lib.graphics.managed.engine.newEngine;

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

    protected abstract void renderObject(MeshHandle[] meshHandles);

}
