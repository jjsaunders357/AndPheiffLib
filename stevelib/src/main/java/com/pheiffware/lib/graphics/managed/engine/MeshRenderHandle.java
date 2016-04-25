package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.techniques.TechniqueProperty;

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
    //Reference to properties which should always be set to specific values for this
    final TechniqueProperty[] properties;
    //Reference to corresponding property values
    final Object[] propertyValues;
    //The offset in the index buffer to render at
    final int vertexOffset;
    //The number of vertices to render
    final int numVertices;

    public MeshRenderHandle(Technique technique, TechniqueProperty[] properties, Object[] propertyValues, int vertexOffset, int numVertices)
    {
        this.technique = technique;
        this.properties = properties;
        this.propertyValues = propertyValues;
        this.vertexOffset = vertexOffset;
        this.numVertices = numVertices;
    }

    void setProperties()
    {
        for (int i = 0; i < properties.length; i++)
        {
            technique.setProperty(properties[i], propertyValues[i]);
        }
    }
}
