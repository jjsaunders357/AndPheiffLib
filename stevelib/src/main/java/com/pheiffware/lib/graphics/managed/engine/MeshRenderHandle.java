package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.techniques.TechniqueProperty;

/**
 * Holds information about a single mesh which should be rendered with a specific Program and specific values for defaultedProperties in that Program.  The set of uniform values may be
 * incomplete as some values (such as ViewMatrix) may be specified more globally.
 * <p/>
 * Created by Steve on 4/10/2016.
 */
public class MeshRenderHandle
{
    //The technique to use when rendering this mesh
    final Technique technique;
    //Reference to Uniform objects from the program which should be set
    final TechniqueProperty[] defaultedProperties;
    //Reference to corresponding uniform values to set defaultedProperties to
    final Object[] defaultPropertyValues;
    //The offset in the index buffer to render at
    final int vertexOffset;
    //The number of vertices to render
    final int numVertices;

    public MeshRenderHandle(Technique technique, TechniqueProperty[] defaultedProperties, Object[] defaultPropertyValues, int vertexOffset, int numVertices)
    {
        this.technique = technique;
        this.defaultedProperties = defaultedProperties;
        this.defaultPropertyValues = defaultPropertyValues;
        this.vertexOffset = vertexOffset;
        this.numVertices = numVertices;
    }

    void setDefaultProperties()
    {
        for (int i = 0; i < defaultedProperties.length; i++)
        {
            technique.setProperty(defaultedProperties[i], defaultPropertyValues[i]);
        }
    }
}
