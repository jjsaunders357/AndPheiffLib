package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.techniques.PropertyValue;
import com.pheiffware.lib.graphics.techniques.TechniqueProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Comment Class and methods!
 * <p/>
 * Created by Steve on 5/15/2016.
 */
public abstract class BaseGraphicsManager<M>
{
    private final List<RenderItem<M>> renderItems = new ArrayList<>(1000);
    private final Technique[] techniques;
    private final IndexBuffer indexBuffer = new IndexBuffer(false);

    private GraphicsManagerTransferData<M> transferData;

    public BaseGraphicsManager(StaticVertexBuffer[] vertexBuffers, Technique[] techniques)
    {
        this.techniques = techniques;
        transferData = new GraphicsManagerTransferData<>(indexBuffer, vertexBuffers);
    }

    /**
     * Start a new logical object definition.  Each added mesh will become part of this object render handle.
     *
     * @return
     */
    public final ObjectRenderHandle<M> startNewObjectDef()
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

    public MeshRenderHandle<M> addMesh(Mesh mesh, StaticVertexBuffer vertexBuffer, M material, PropertyValue[] propertyValues)
    {
        int meshIndexOffset = transferData.addMesh(mesh, vertexBuffer);

        MeshRenderHandle<M> meshRenderHandle = new MeshRenderHandle<>(vertexBuffer, meshIndexOffset, mesh.getNumIndices(), material, propertyValues);
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

    public void resetRender()
    {
        renderItems.clear();
    }

    public final void submitRender(MeshRenderHandle<M> meshHandle, TechniqueProperty[] overrideProperties, Object[] overridePropertyValues)
    {
        renderItems.add(new RenderItem<M>(meshHandle, overrideProperties, overridePropertyValues));
    }

    public final void submitRender(ObjectRenderHandle<M> objectHandle, TechniqueProperty[] overrideProperties, Object[] overridePropertyValues)
    {
        for (int i = 0; i < objectHandle.meshRenderHandles.size(); i++)
        {
            submitRender(objectHandle.meshRenderHandles.get(i), overrideProperties, overridePropertyValues);
        }
    }

    protected void sortRenderList(List<RenderItem<M>> renderItems)
    {
        //Default is do nothing
    }

    protected abstract void renderItem(MeshRenderHandle<M> meshHandle, TechniqueProperty[] overrideProperties, Object[] overridePropertyValues);

    protected final void drawIndexBuffer(MeshRenderHandle<Technique> meshHandle)
    {
        meshHandle.drawTriangles(indexBuffer);
    }

    public void render()
    {
        sortRenderList(renderItems);
        for (RenderItem<M> renderItem : renderItems)
        {
            renderItem(renderItem.meshHandle, renderItem.overrideProperties, renderItem.overridePropertyValues);
        }
    }

    private static class RenderItem<M>
    {
        public final MeshRenderHandle<M> meshHandle;
        public final TechniqueProperty[] overrideProperties;
        public final Object[] overridePropertyValues;

        public RenderItem(MeshRenderHandle<M> meshHandle, TechniqueProperty[] overrideProperties, Object[] overridePropertyValues)
        {
            this.meshHandle = meshHandle;
            this.overrideProperties = overrideProperties;
            this.overridePropertyValues = overridePropertyValues;
        }
    }
}
