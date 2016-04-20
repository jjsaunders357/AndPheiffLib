package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.managed.program.Uniform;

/**
 * Holds information about a single mesh which should be rendered with a specific Program and specific values for defaultUniforms in that Program.  The set of uniform values may be
 * incomplete as some values (such as ViewMatrix) may be specified more globally.
 * <p/>
 * Created by Steve on 4/10/2016.
 */
public class MeshRenderHandle
{
    //The program to use when rendering this mesh
    final int programIndex;
    //Reference to Uniform objects from the program which should be set
    final Uniform[] defaultUniforms;
    //Reference to corresponding uniform values to set defaultUniforms to
    final Object[] defaultUniformValues;
    //The offset in the index buffer to render at
    final int vertexOffset;
    //The number of vertices to render
    final int numVertices;

    public MeshRenderHandle(int programIndex, Uniform[] defaultUniforms, Object[] defaultUniformValues, int vertexOffset, int numVertices)
    {
        this.programIndex = programIndex;
        this.defaultUniforms = defaultUniforms;
        this.defaultUniformValues = defaultUniformValues;
        this.vertexOffset = vertexOffset;
        this.numVertices = numVertices;
    }

    void setUniforms()
    {
        for (int i = 0; i < defaultUniforms.length; i++)
        {
            defaultUniforms[i].setValue(defaultUniformValues[i]);
        }
    }

    private static Uniform[] uniformsFromNames(Program program, String[] uniformNames)
    {
        Uniform[] uniforms = new Uniform[uniformNames.length];
        for (int i = 0; i < uniforms.length; i++)
        {
            uniforms[i] = program.getUniform(uniformNames[i]);
        }
        return uniforms;
    }
}
