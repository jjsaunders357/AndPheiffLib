package com.pheiffware.lib.graphics.managed;

import com.pheiffware.lib.graphics.managed.engine.BaseGraphicsManager;
import com.pheiffware.lib.graphics.managed.engine.ItemRenderer;
import com.pheiffware.lib.graphics.managed.engine.MeshRenderHandle;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.vertexBuffer.StaticVertexBuffer;
import com.pheiffware.lib.utils.Utils;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages storing data in index/vertex buffers and then conveniently/efficiently rendering that data.
 * <p/>
 * The core organizational structures are MeshHandles.  These contain a reference into the buffers where primitives are stored along with default rendering parameters such as color
 * and shininess.  An ObjectHandle is a reference to a collection of meshes, possibly rendered with different techniques, which share properties such as ModelMatrix.
 * <p/>
 * Typical usage:
 * <p/>
 * Setup:
 * <p/>
 * 1. Call addObject() and addTransferMesh() over and over.
 * <p/>
 * 2. Call transfer()
 * <p/>
 * Per frame:
 * <p/>
 * 1. Call resetRender()
 * <p/>
 * 2. Call setDefaultPropertyValues
 * <p/>
 * 3. Call submitRender() over and over again for each mesh/object to be rendered this frame
 * <p/>
 * 4. Call render() to render all submitted objects
 * <p/>
 * Created by Steve on 4/13/2016.
 */
public class SingleTechniqueGraphicsManager extends BaseGraphicsManager<Technique>
{
    private EnumMap<RenderProperty, Object> defaultProperties = new EnumMap<>(RenderProperty.class);

    public SingleTechniqueGraphicsManager(StaticVertexBuffer[] vertexBuffers, Technique[] techniques)
    {
        super(vertexBuffers, techniques);
    }

    /**
     * Sets default values for properties across all techniques.  These values are used for every MeshRenderHandle unless it has specified its own value for the property or the
     * property is overridden during rendering.
     *
     * @param techniqueProperties
     * @param defaultValues
     */
    public void setDefaultPropertyValues(RenderProperty[] techniqueProperties, Object[] defaultValues)
    {
        defaultProperties = Utils.enumMapFromArrays(techniqueProperties, defaultValues, RenderProperty.class);
    }

    @Override
    public void preRender()
    {
        for (Technique technique : getTechniques())
        {
            technique.setDefaultPropertyValues(defaultProperties);
        }
    }

    @Override
    public void renderItem(MeshRenderHandle<Technique> meshHandle, Technique technique, StaticVertexBuffer vertexBuffer, RenderPropertyValue[] propertyValues, RenderProperty[] overrideProperties, Object[] overridePropertyValues)
    {
        technique.bind();
        vertexBuffer.bind(technique);
        technique.setProperties(propertyValues);
        technique.setProperties(overrideProperties, overridePropertyValues);
        technique.applyProperties();
        drawTriangles(meshHandle);
    }

    @Override
    public void postRender()
    {

    }

    protected Object getDefaultValue(RenderProperty renderProperty)
    {
        return defaultProperties.get(renderProperty);
    }
}
