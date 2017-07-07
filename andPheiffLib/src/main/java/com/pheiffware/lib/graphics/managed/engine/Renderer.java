package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.program.RenderProperty;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Created by Steve on 6/19/2017.
 */

public abstract class Renderer
{
    private final List<ObjectHandle> renderList = new ArrayList<>(1000);

    //Properties which will should set once for every technique which supports them (such as the perspective matrix)
    private final EnumMap<RenderProperty, Object> constantRenderProperties = new EnumMap<>(RenderProperty.class);

    public final void setRenderProperty(RenderProperty renderProperty, Object value)
    {
        constantRenderProperties.put(renderProperty, value);
    }

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
        constantRenderProperties.clear();
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
