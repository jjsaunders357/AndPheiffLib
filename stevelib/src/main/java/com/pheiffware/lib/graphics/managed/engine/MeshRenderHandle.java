package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.Uniform;

/**
 * Holds information about a single mesh which should be rendered with a specific Program and specific values for uniforms in that Program.  The set of uniform values may be
 * incomplete as some values (such as ViewMatrix) may be specified more globally.
 * <p/>
 * Created by Steve on 4/10/2016.
 */
public class MeshRenderHandle
{
    //The program to use when rendering this mesh
    final int programIndex;
    //Reference to Uniform objects from the program which should be set
    final Uniform[] uniforms;
    //Reference to corresponding uniform values to set uniforms to
    final Object[] uniformValues;
    //The offset in the index buffer to render at
    final int vertexOffset;
    //The number of vertices to render
    final int numVertices;


    public MeshRenderHandle(int programIndex, Uniform[] uniforms, Object[] uniformValues, int vertexOffset, int numVertices)
    {
        this.programIndex = programIndex;
        this.uniforms = uniforms;
        this.uniformValues = uniformValues;
        this.vertexOffset = vertexOffset;
        this.numVertices = numVertices;
    }

    public void setUniforms()
    {
        for (int i = 0; i < uniforms.length; i++)
        {
            uniforms[i].setValue(uniformValues[i]);
        }
    }
}
