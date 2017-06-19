package com.pheiffware.lib.graphics.managed.engine.newEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steve on 6/19/2017.
 */

public abstract class Renderer
{
    private final List<ObjectHandle> renderList = new ArrayList<>(1000);

    public final void add(ObjectHandle objectHandle)
    {
        renderList.add(objectHandle);
    }

    public final void add(List<ObjectHandle> objectHandles)
    {
        renderList.addAll(objectHandles);
    }

    public final void render()
    {
        renderImplement();
        renderList.clear();
    }

    protected void renderPass(BaseRenderPass renderPass)
    {
        renderPass.preRender();
        for (ObjectHandle objectHandle : renderList)
        {
            if (renderPass.filter(objectHandle))
            {
                renderPass.renderObject(objectHandle.meshHandles);
            }
        }
        renderPass.postRender();
    }

    protected abstract void renderImplement();
}
