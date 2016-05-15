package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.techniques.TechniqueProperty;

import java.util.EnumMap;

/**
 * TODO: Comment me!
 * <p/>
 * Created by Steve on 5/15/2016.
 */
public abstract class BaseGraphicsManager<M>
{
    private final Technique[] techniques;
    private final IndexBuffer indexBuffer = new IndexBuffer(false);

    private GraphicsManagerTransferData<M> transferData;

    public BaseGraphicsManager(StaticVertexBuffer[] vertexBuffers, Technique[] techniques)
    {
        this.techniques = techniques;
        transferData = new GraphicsManagerTransferData<>(indexBuffer, vertexBuffers);
    }

    //TODO: Temporary method.  Will be removed once renderNow is converted to queueing scheme
    protected abstract void bindMeshHandle(MeshRenderHandle<M> meshHandle, EnumMap<TechniqueProperty, Object> propertyValues);

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

    public MeshRenderHandle addMesh(Mesh mesh, StaticVertexBuffer vertexBuffer, M material)
    {
        int meshIndexOffset = transferData.addMesh(mesh, vertexBuffer);

        MeshRenderHandle<M> meshRenderHandle = new MeshRenderHandle<>(vertexBuffer, meshIndexOffset, mesh.getNumIndices(), material);
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

    //TODO: Remove renderNow.  Instead should support queueing.

    /**
     * Renders a given mesh.  Explicitly overrides properties with those given.
     *
     * @param meshHandle
     * @param propertyValues
     */
    public final void renderNow(MeshRenderHandle<M> meshHandle, TechniqueProperty[] properties, Object[] propertyValues)
    {
        EnumMap<TechniqueProperty, Object> propertyValuesMap = new EnumMap<>(TechniqueProperty.class);
        for (int i = 0; i < properties.length; i++)
        {
            propertyValuesMap.put(properties[i], propertyValues[i]);
        }
        renderNow(meshHandle, propertyValuesMap);
    }

    /**
     * Renders a given mesh.  Explicitly overrides properties with those given.
     *
     * @param meshHandle
     * @param propertyValues
     */
    public final void renderNow(MeshRenderHandle<M> meshHandle, EnumMap<TechniqueProperty, Object> propertyValues)
    {
        bindMeshHandle(meshHandle, propertyValues);
        indexBuffer.drawTriangles(meshHandle.vertexOffset, meshHandle.numVertices);
    }

    public final void renderNow(ObjectRenderHandle<M> objectHandle, TechniqueProperty[] properties, Object[] propertyValues)
    {
        for (int i = 0; i < objectHandle.meshRenderHandles.size(); i++)
        {
            renderNow(objectHandle.meshRenderHandles.get(i), properties, propertyValues);
        }
    }
}
