package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.techniques.TechniqueProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * This class manages storing data in index/vertex buffers and then conveniently/efficiently rendering that data.
 * <p/>
 * The core organizational structures are MeshHandles.  These contain a reference into the buffers where primitives are stored along with default rendering parameters such as color
 * and shininess.
 * <p/>
 * TODO: Finish commenting
 * <p/>
 * Created by Steve on 4/13/2016.
 */
public class BaseGraphicsManager
{
    private final Technique[] techniques;
    private final StaticVertexBuffer[] staticVertexBuffers;
    private final Map<Technique, Integer> techniqueIndexLookup = new HashMap<>();
    private final IndexBuffer indexBuffer = new IndexBuffer(false);

    private GraphicsManagerTransferData transferData;

    public BaseGraphicsManager(Technique[] techniques)
    {
        this.techniques = techniques;
        for (int i = 0; i < techniques.length; i++)
        {
            techniqueIndexLookup.put(techniques[i], i);
        }
        this.staticVertexBuffers = new StaticVertexBuffer[techniques.length];
        createVertexBuffers(techniques);
        transferData = new GraphicsManagerTransferData(indexBuffer, techniques, staticVertexBuffers);
    }

    protected void createVertexBuffers(Technique[] techniques)
    {
        for (int i = 0; i < techniques.length; i++)
        {
            Technique technique = techniques[i];
            staticVertexBuffers[i] = new StaticVertexBuffer(technique.getProgram());
        }
    }

    public final ObjectRenderHandle addObject(Mesh[] meshes, int[] techniqueIndices, PropertyValue[][] defaultPropertyValuesArray)
    {
        ObjectRenderHandle objectRenderHandle = new ObjectRenderHandle();
        for (int i = 0; i < meshes.length; i++)
        {
            int techniqueIndex = techniqueIndices[i];
            Mesh mesh = meshes[i];

            PropertyValue[] defaultPropertyValues = defaultPropertyValuesArray[i];
            MeshRenderHandle meshRenderHandle = addMesh(mesh, techniqueIndex, defaultPropertyValues);
            objectRenderHandle.addMeshHandle(meshRenderHandle);
        }
        return objectRenderHandle;
    }

    public final MeshRenderHandle addMesh(Mesh mesh, Technique technique, PropertyValue[] defaultPropertyValues)
    {
        return addMesh(mesh, technique, techniqueIndexLookup.get(technique), defaultPropertyValues);
    }

    private MeshRenderHandle addMesh(Mesh mesh, int techniqueIndex, PropertyValue[] defaultPropertyValues)
    {
        return addMesh(mesh, techniques[techniqueIndex], techniqueIndex, defaultPropertyValues);
    }

    private MeshRenderHandle addMesh(Mesh mesh, Technique technique, int techniqueIndex, PropertyValue[] defaultPropertyValues)
    {
        int meshIndexOffset = transferData.addMesh(mesh, techniqueIndex);

        TechniqueProperty[] defaultedProperties = new TechniqueProperty[defaultPropertyValues.length];
        Object[] defaultUniformValues = new Object[defaultPropertyValues.length];
        for (int i = 0; i < defaultPropertyValues.length; i++)
        {
            defaultedProperties[i] = defaultPropertyValues[i].property;
            defaultUniformValues[i] = defaultPropertyValues[i].value;
        }
        return new MeshRenderHandle(technique, defaultedProperties, defaultUniformValues, meshIndexOffset, mesh.getNumIndices());
    }

    public final void transfer()
    {
        transferData.transfer();
        transferData = null;
    }

    /**
     * TODO: Implement
     *
     * @param techniqueProperties
     * @param objects
     */
    public void setGlobalProperties(TechniqueProperty[] techniqueProperties, Object[] objects)
    {
//        for(Technique technique:techniques)
//        {
//            technique.setProperties(techniqueProperties,objects);
//        }
    }

    public void render(MeshRenderHandle meshHandle, TechniqueProperty[] properties, Object[] propertyValues)
    {
        Technique technique = meshHandle.technique;
        int techniqueIndex = techniqueIndexLookup.get(meshHandle.technique);
        StaticVertexBuffer staticVertexBuffer = staticVertexBuffers[techniqueIndex];
        technique.bind();
        staticVertexBuffer.bind();
        meshHandle.setDefaultProperties();
        for (int i = 0; i < properties.length; i++)
        {
            technique.setProperty(properties[i], propertyValues[i]);
        }
        technique.applyPropertiesToUniforms();
        indexBuffer.drawTriangles(meshHandle.vertexOffset, meshHandle.numVertices);
    }

    public void render(ObjectRenderHandle objectHandle, TechniqueProperty[] properties, Object[] propertyValues)
    {
        for (int i = 0; i < objectHandle.meshRenderHandles.size(); i++)
        {
            render(objectHandle.meshRenderHandles.get(i), properties, propertyValues);
        }
    }

    public final void bindTechnique(MeshRenderHandle meshHandle)
    {
        Technique technique = meshHandle.technique;
        int techniqueIndex = techniqueIndexLookup.get(meshHandle.technique);
        StaticVertexBuffer staticVertexBuffer = staticVertexBuffers[techniqueIndex];
        technique.bind();
        staticVertexBuffer.bind();
    }

    public final void setDefaultUniformValues(MeshRenderHandle meshHandle)
    {
        meshHandle.setDefaultProperties();
    }

    public final void renderIndexBuffer(MeshRenderHandle meshHandle)
    {
        indexBuffer.drawTriangles(meshHandle.vertexOffset, meshHandle.numVertices);
    }

}
