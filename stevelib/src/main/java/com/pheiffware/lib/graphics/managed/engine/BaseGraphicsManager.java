package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.techniques.TechniqueProperty;

/**
 * This class manages storing data in index/vertex buffers and then conveniently/efficiently rendering that data.
 * <p/>
 * The core organizational structures are MeshHandles.  These contain a reference into the buffers where primitives are stored along with default rendering parameters such as color
 * and shininess.  An ObjectHandle is a reference to a collection of meshes, possibly rendered with different techniques, which share properties such as ModelMatrix.
 * <p/>
 * Typical usage:
 * <p/>
 * 1. Call addObject() and addMesh() over and over.
 * <p/>
 * 2. Call transfer()
 * <p/>
 * 3. Call setDefaultPropertyValues()
 * <p/>
 * TODO: Comment once we advance further
 * <p/>
 * 4. Call renderNow() over and over again
 * <p/>
 * <p/>
 * Created by Steve on 4/13/2016.
 */
public class BaseGraphicsManager
{
    private final Technique[] techniques;
    private final IndexBuffer indexBuffer = new IndexBuffer(false);

    //TODO: Should be combined vertex buffer
    private final StaticVertexBuffer[] vertexBuffers;

    private GraphicsManagerTransferData transferData;

    public BaseGraphicsManager(Technique[] techniques, StaticVertexBuffer[] vertexBuffers)
    {
        this.techniques = techniques;
        this.vertexBuffers = vertexBuffers;
        transferData = new GraphicsManagerTransferData(indexBuffer, vertexBuffers);
    }

    /**
     * Start a new logical object definition.  Each added mesh will become part of this object render handle.
     *
     * @return
     */
    public final ObjectRenderHandle startNewObjectDef()
    {
        return transferData.startNewObjectDef();
    }

    /**
     * End the object definition.
     */
    public final void endObjectDef()
    {
        transferData.endObjectDef();
    }

    //TODO: This should pick the buffer to put the data in for you!
    /**
     * Add a mesh to be rendered with a particular technique and specific property values.  The is added to the current object definition if one is being defined.
     *
     * @param mesh
     * @param technique
     * @param propertyValues
     * @return
     */
    public final MeshRenderHandle addMesh(Mesh mesh, StaticVertexBuffer vertexBuffer, Technique technique, PropertyValue[] propertyValues)
    {
        int meshIndexOffset = transferData.addMesh(mesh, vertexBuffer);

        TechniqueProperty[] properties = new TechniqueProperty[propertyValues.length];
        Object[] values = new Object[propertyValues.length];
        for (int i = 0; i < propertyValues.length; i++)
        {
            properties[i] = propertyValues[i].property;
            values[i] = propertyValues[i].value;
        }

        MeshRenderHandle meshRenderHandle = new MeshRenderHandle(technique, properties, values, vertexBuffer, meshIndexOffset, mesh.getNumIndices());
        if (transferData.getCurrentObjectDef() != null)
        {
            transferData.getCurrentObjectDef().addMeshHandle(meshRenderHandle);
        }
        return meshRenderHandle;
    }

    /**
     * Transfer added mesh data to the GL.
     */
    public final void transfer()
    {
        transferData.transfer();
        transferData = null;
    }

    /**
     * Sets default values for properties across all techniques.
     *
     * @param techniqueProperties
     * @param objects
     */
    public void setDefaultPropertyValues(TechniqueProperty[] techniqueProperties, Object[] objects)
    {
        for (Technique technique : techniques)
        {
            technique.setDefaultPropertyValues(techniqueProperties, objects);
        }
    }

    /**
     * Renders a given mesh.  Explicitly overrides properties with those given.
     *
     * @param meshHandle
     * @param properties
     * @param propertyValues
     */
    public void renderNow(MeshRenderHandle meshHandle, TechniqueProperty[] properties, Object[] propertyValues)
    {
        meshHandle.technique.bind();
        meshHandle.vertexBuffer.bind(meshHandle.technique.getProgram());
        meshHandle.setProperties();
        for (int i = 0; i < properties.length; i++)
        {
            meshHandle.technique.setProperty(properties[i], propertyValues[i]);
        }
        meshHandle.technique.applyProperties();
        indexBuffer.drawTriangles(meshHandle.vertexOffset, meshHandle.numVertices);
    }

    public void renderNow(ObjectRenderHandle objectHandle, TechniqueProperty[] properties, Object[] propertyValues)
    {
        for (int i = 0; i < objectHandle.meshRenderHandles.size(); i++)
        {
            renderNow(objectHandle.meshRenderHandles.get(i), properties, propertyValues);
        }
    }

    public final void bindTechnique(MeshRenderHandle meshHandle)
    {
        Technique technique = meshHandle.technique;
        technique.bind();
    }

    public final void setDefaultUniformValues(MeshRenderHandle meshHandle)
    {
        meshHandle.setProperties();
    }

    public final void renderIndexBuffer(MeshRenderHandle meshHandle)
    {
        indexBuffer.drawTriangles(meshHandle.vertexOffset, meshHandle.numVertices);
    }

}
