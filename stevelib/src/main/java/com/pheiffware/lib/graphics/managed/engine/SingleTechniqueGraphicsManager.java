package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.techniques.TechniqueProperty;

import java.util.EnumMap;

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
public class SingleTechniqueGraphicsManager extends BaseGraphicsManager<SingleTechniqueGraphicsManager.Material>
{
    public SingleTechniqueGraphicsManager(StaticVertexBuffer[] vertexBuffers, Technique[] techniques)
    {
        super(vertexBuffers, techniques);
    }

    @Override
    protected void bindMeshHandle(MeshRenderHandle<Material> meshHandle, EnumMap<TechniqueProperty, Object> propertyValues)
    {
        Technique technique = meshHandle.material.technique;
        technique.bind();
        meshHandle.vertexBuffer.bind(technique.getProgram());
        technique.setProperties(meshHandle.material.propertyValues);
        technique.setProperties(propertyValues);
        technique.applyProperties();
    }

    public final MeshRenderHandle addMesh(Mesh mesh, StaticVertexBuffer vertexBuffer, Technique technique, PropertyValue[] propertyValues)
    {
        Material material = new Material(technique, propertyValues);
        return addMesh(mesh, vertexBuffer, material);
    }

    public static class Material
    {
        public final Technique technique;
        public final EnumMap<TechniqueProperty, Object> propertyValues;

        public Material(Technique technique, EnumMap<TechniqueProperty, Object> propertyValues)
        {
            this.technique = technique;
            this.propertyValues = propertyValues;
        }

        public Material(Technique technique, PropertyValue[] propertyValues)
        {
            this.technique = technique;
            this.propertyValues = new EnumMap<>(TechniqueProperty.class);
            for (PropertyValue propertyValue : propertyValues)
            {
                this.propertyValues.put(propertyValue.property, propertyValue.value);
            }
        }
    }
}
