package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.techniques.TechniqueProperty;

import java.util.EnumMap;

/**
 * Holds information about a single mesh which should be rendered with a specific Program and specific values for properties in that Program.  The set of uniform values may be
 * incomplete as some values (such as ViewMatrix) may be specified more globally.
 * <p/>
 * Created by Steve on 4/10/2016.
 */
public class MeshRenderHandle
{
    //The technique to use when rendering this mesh
    final Technique technique;
    //Properties to use when rendering this mesh
    final EnumMap<TechniqueProperty, Object> propertyValues = new EnumMap<>(TechniqueProperty.class);
    //The vertex buffer where mesh data is stored
    final StaticVertexBuffer vertexBuffer;
    //The offset in the index buffer to render at
    final int vertexOffset;
    //The number of vertices to render
    final int numVertices;

    public MeshRenderHandle(Technique technique, EnumMap<TechniqueProperty, Object> propertyValues, StaticVertexBuffer vertexBuffer, int vertexOffset, int numVertices)
    {
        this.technique = technique;
        this.propertyValues.putAll(propertyValues);
        this.vertexBuffer = vertexBuffer;
        this.vertexOffset = vertexOffset;
        this.numVertices = numVertices;
    }

    void setProperties()
    {
        technique.setProperties(propertyValues);
    }
}
