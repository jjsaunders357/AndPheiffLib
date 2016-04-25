package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.techniques.TechniqueProperty;

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
    private final IndexBuffer indexBuffer = new IndexBuffer(false);

    private GraphicsManagerTransferData transferData;

    public BaseGraphicsManager(Technique[] techniques)
    {
        this.techniques = techniques;
        transferData = new GraphicsManagerTransferData(indexBuffer, techniques);
    }

//    public final ObjectRenderHandle addObject(Mesh[] meshes, int[] techniqueIndices, PropertyValue[][] defaultPropertyValuesArray)
//    {
//        ObjectRenderHandle objectRenderHandle = new ObjectRenderHandle();
//        for (int i = 0; i < meshes.length; i++)
//        {
//            int techniqueIndex = techniqueIndices[i];
//            Mesh mesh = meshes[i];
//
//            PropertyValue[] defaultPropertyValues = defaultPropertyValuesArray[i];
//            MeshRenderHandle meshRenderHandle = addMesh(mesh, techniqueIndex, defaultPropertyValues);
//            objectRenderHandle.addMeshHandle(meshRenderHandle);
//        }
//        return objectRenderHandle;
//    }

    public final MeshRenderHandle addMesh(Mesh mesh, Technique technique, PropertyValue[] defaultPropertyValues)
    {
        int meshIndexOffset = transferData.addMesh(mesh, technique);

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
        technique.bind();
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
        technique.bind();
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
