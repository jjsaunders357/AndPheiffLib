package com.pheiffware.lib.graphics.managed;

import com.pheiffware.lib.graphics.managed.engine.BaseGraphicsManager;
import com.pheiffware.lib.graphics.managed.engine.MeshRenderHandle;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.vertexBuffer.StaticVertexBuffer;

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
public class ShadowTechniqueGraphicsManager extends BaseGraphicsManager<Technique>
{
    public ShadowTechniqueGraphicsManager(StaticVertexBuffer[] vertexBuffers, Technique[] techniques)
    {
        super(vertexBuffers, techniques);
    }

    @Override
    protected void renderItem(MeshRenderHandle<Technique> meshHandle, Technique technique, StaticVertexBuffer vertexBuffer, RenderPropertyValue[] meshPropertyValues, RenderProperty[] overrideProperties, Object[] overridePropertyValues)
    {
        technique.bind();
        vertexBuffer.bind(technique);
        technique.setProperties(meshPropertyValues);
        technique.setProperties(overrideProperties, overridePropertyValues);
        technique.applyProperties();
        drawTriangles(meshHandle);
    }
}
