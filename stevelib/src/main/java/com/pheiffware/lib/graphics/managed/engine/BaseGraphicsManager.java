package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.vertexBuffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.StaticVertexBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for managing loading and rendering objects.  Given a set of techniques and unallocated vertex buffers, it allows objects/meshes to be added, then transferred to the
 * graphics card in a single block handling all vertex buffer allocation.
 * <p/>
 * Each object is a logical collection of meshes.  Each mesh can be assigned RenderPropertys which can be interpreted by extending classes in whatever manner is appropriate.
 * Additionally, each mesh is given a generic material M.  M is given to the sub-class during the rendering process.
 * <p/>
 * Usage:
 * <p/>
 * //Add all objects/meshes as appropriate:
 * <p/>
 * graphicsManager.addObject();
 * <p/>
 * //Add meshes, with remembered properties:
 * <p/>
 * graphicsManager.addTransferMesh(meshProperties); ...
 * <p/>
 * //Allocate vertexBuffers and transfer graphicsManager.transfer();
 * <p/>
 * //Render a frame graphicsManager.reset();
 * <p/>
 * //Set default properties for the entire frame, these are overridden by mesh specific properties and/or overrideProperties
 * <p/>
 * graphicsManager.setDefaultPropertyValues(defaultProperties);
 * <p/>
 * //Submit request to render a particular object, with overridden properties
 * <p/>
 * graphicsManager.submitRender(overrideProperties); ...
 * <p/>
 * //Render everything submitted graphicsManager.render();
 * <p/>
 * Created by Steve on 5/15/2016.
 */
public abstract class BaseGraphicsManager<M>
{
    //Holds the list of items to be rendered for a given render() operation
    private final List<RenderItem<M>> renderItems = new ArrayList<>(1000);

    //An array of all techniques used by this class.  This is only used to implement the setDefaultPropertyValues method, which applies default values to all techniques.
    //How techniques are actually used during the rendering process is up to the sub-class.
    private final Technique[] techniques;

    //The index buffer holding all indices of all vertices to be rendered.  Index buffer can hold 32768 indices max as it uses shorts.
    private final IndexBuffer indexBuffer = new IndexBuffer(false);

    //A temporary cache where all data is stored during the initial loading process.  Once this is transferred
    private GraphicsManagerTransferData<M> transferData;

    //Tracks the current logical object being created
    private ObjectRenderHandle<M> currentObject = null;

    public BaseGraphicsManager(StaticVertexBuffer[] vertexBuffers, Technique[] techniques)
    {
        this.techniques = techniques;
        transferData = new GraphicsManagerTransferData<>(indexBuffer, vertexBuffers);
    }


    /**
     * Start a new logical object definition.  Each added mesh will become part of this object render handle.
     *
     * @return A handle to this object which can be used for rendering after transfer() is called.
     */
    public final ObjectRenderHandle<M> startNewObjectDef()
    {
        currentObject = new ObjectRenderHandle<>();
        return currentObject;
    }

    /**
     * End the current logical object definition.
     */
    public final void endObjectDef()
    {
        currentObject = null;
    }

    /**
     * Add a mesh to be transferred.  All meshes must be added before a single transfer so that buffer sizes can be allocated.
     *
     * @param mesh                 the mesh to add
     * @param vertexBuffer         the vertex buffer this mesh should be stored in
     * @param material             the graphics manager specific material to use to render this
     * @param renderPropertyValues the values of render properties to use when rendering this
     * @return A handle to this mesh which can be used for rendering after transfer() is called.
     */
    public MeshRenderHandle<M> addTransferMesh(Mesh mesh, StaticVertexBuffer vertexBuffer, M material, RenderPropertyValue[] renderPropertyValues)
    {
        int meshIndexOffset = transferData.addTransferMesh(mesh, vertexBuffer);

        MeshRenderHandle<M> meshRenderHandle = new MeshRenderHandle<>(vertexBuffer, meshIndexOffset, mesh.getNumIndices(), material, renderPropertyValues);
        if (currentObject != null)
        {
            currentObject.addMeshHandle(meshRenderHandle);
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
        currentObject = null;
    }

    /**
     * Sets default values for properties across all techniques.  These values are used for every MeshRenderHandle unless it has specified its own value for the property or the
     * property is overridden during rendering.
     *
     * @param techniqueProperties
     * @param objects
     */
    public void setDefaultPropertyValues(RenderProperty[] techniqueProperties, Object[] objects)
    {
        for (Technique technique : techniques)
        {
            technique.setDefaultPropertyValues(techniqueProperties, objects);
        }
    }

    /**
     * Should be called once before calling submitRender repeatedly.
     */
    public void resetRender()
    {
        renderItems.clear();
    }


    public final void submitRenderWithMatrix(ObjectRenderHandle<M> objectHandle)
    {
        for (int i = 0; i < objectHandle.meshRenderHandles.size(); i++)
        {
            submitRender(objectHandle.meshRenderHandles.get(i), new RenderProperty[]{RenderProperty.MODEL_MATRIX}, new Object[]{objectHandle.matrix});
        }
    }

    /**
     * Submit an object for rendering once.
     *
     * @param objectHandle           handle to the object to render
     * @param overrideProperties     any properties which are either unspecified from the underlying meshes or which should be overridden.  The ModelMatrix property is typically
     *                               specified every time for an object which is moving.
     * @param overridePropertyValues corresponding values for properties
     */
    public final void submitRender(ObjectRenderHandle<M> objectHandle, RenderProperty[] overrideProperties, Object[] overridePropertyValues)
    {
        for (int i = 0; i < objectHandle.meshRenderHandles.size(); i++)
        {
            submitRender(objectHandle.meshRenderHandles.get(i), overrideProperties, overridePropertyValues);
        }
    }

    /**
     * Submit an individual mesh for rendering once.
     *
     * @param meshHandle             handle to mesh to render
     * @param overrideProperties     any properties which are either unspecified from the underlying meshes or which should be overridden.  The ModelMatrix property is typically
     *                               specified every time for an object which is moving.
     * @param overridePropertyValues corresponding values for properties
     */
    public final void submitRender(MeshRenderHandle<M> meshHandle, RenderProperty[] overrideProperties, Object[] overridePropertyValues)
    {
        renderItems.add(new RenderItem<M>(meshHandle, overrideProperties, overridePropertyValues));
    }


    /**
     * Render everything that was submitted.
     */
    public void render()
    {
        sortRenderList(renderItems);
        for (RenderItem<M> renderItem : renderItems)
        {
            MeshRenderHandle<M> meshHandle = renderItem.meshHandle;
            renderItem(meshHandle, meshHandle.material, meshHandle.vertexBuffer, meshHandle.propertyValues, renderItem.overrideProperties, renderItem.overridePropertyValues);
        }
    }

    /**
     * Sort the list of items that were submitted for rendering if necessary
     *
     * @param renderItems
     */
    protected void sortRenderList(List<RenderItem<M>> renderItems)
    {
        //Default is do nothing
    }

    /**
     * Must be implemented by the extending class to actually render a given mesh
     *
     * @param meshHandle
     * @param material
     * @param vertexBuffer
     * @param meshPropertyValues
     * @param overrideProperties
     * @param overridePropertyValues
     */
    protected abstract void renderItem(MeshRenderHandle<M> meshHandle, M material, StaticVertexBuffer vertexBuffer, RenderPropertyValue[] meshPropertyValues, RenderProperty[] overrideProperties, Object[] overridePropertyValues);

    /**
     * Draws the primitives in the index buffer, referenced by the given mesh handle
     *
     * @param meshHandle
     */
    protected final void drawTriangles(MeshRenderHandle<Technique> meshHandle)
    {
        meshHandle.drawTriangles(indexBuffer);
    }

    /**
     * Used to hold information about a single submitted item for rendering.
     *
     * @param <M>
     */
    protected static class RenderItem<M>
    {
        public final MeshRenderHandle<M> meshHandle;
        public final RenderProperty[] overrideProperties;
        public final Object[] overridePropertyValues;

        public RenderItem(MeshRenderHandle<M> meshHandle, RenderProperty[] overrideProperties, Object[] overridePropertyValues)
        {
            this.meshHandle = meshHandle;
            this.overrideProperties = overrideProperties;
            this.overridePropertyValues = overridePropertyValues;
        }
    }
}
