package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

/**
 * Created by Steve on 6/19/2017.
 */

public abstract class Renderer
{
    private final List<ObjectHandle> renderList = new ArrayList<>(1000);

    private final List<Technique> techniques;

    //Properties which will set once for every technique which supports them (such as the perspective matrix)
    private final EnumMap<RenderProperty, Object> constantRenderProperties = new EnumMap<>(RenderProperty.class);

    public Renderer(Technique... techniques)
    {
        this(Arrays.asList(techniques));
    }

    public Renderer(List<Technique> techniques)
    {
        this.techniques = new ArrayList<>(techniques);
    }

    public final void setConstantProperty(RenderProperty renderProperty, Object value)
    {
        constantRenderProperties.put(renderProperty, value);
    }

    public final Technique getTechnique(int techniqueIndex)
    {
        return techniques.get(techniqueIndex);
    }

    public final void applyConstantProperties()
    {
        for (Technique technique : techniques)
        {
            technique.setProperties(constantRenderProperties);
            technique.applyConstantProperties();
        }
    }

    public final void reset()
    {
        clearRenderObjects();
        clearProperties();
    }

    public final void clearRenderObjects()
    {
        renderList.clear();
    }

    public final void clearProperties()
    {
        constantRenderProperties.clear();
    }


    public final void add(ObjectHandle objectHandle)
    {
        renderList.add(objectHandle);
    }

    public final void add(List<ObjectHandle> objectHandles)
    {
        renderList.addAll(objectHandles);
    }

    protected void renderPass()
    {
        for (ObjectHandle objectHandle : renderList)
        {
            if (filter(objectHandle))
            {
                renderObject(objectHandle.meshHandles);
            }
        }
    }

    protected boolean filter(ObjectHandle objectHandle)
    {
        return true;
    }

    protected void renderObject(MeshHandle[] meshHandles)
    {
        for (MeshHandle meshHandle : meshHandles)
        {
            meshHandle.drawTriangles();
            try
            {
                PheiffGLUtils.assertNoError();
            }
            catch (GraphicsException e)
            {
                e.printStackTrace();
            }
        }
    }
}
