package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.Program;
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
    final Program program;
    //Reference to Uniform objects from the program which should be set
    final Uniform[] uniforms;
    //Reference to corresponding uniform values to set uniforms to
    final Object[] uniformValues;
    //The offset in the index buffer to render at
    final int vertexOffset;
    //The number of vertices to render
    final int numVertices;

    private static Uniform[] uniformsFromNames(Program program, String[] uniformNames)
    {
        Uniform[] uniforms = new Uniform[uniformNames.length];
        for (int i = 0; i < uniforms.length; i++)
        {
            uniforms[i] = program.getUniform(uniformNames[i]);
        }
        return uniforms;
    }

    public MeshRenderHandle(Program program, String[] uniformNames, Object[] uniformValues, int vertexOffset, int numVertices)
    {
        this(program, uniformsFromNames(program, uniformNames), uniformValues, vertexOffset, numVertices);
    }

    public MeshRenderHandle(Program program, Uniform[] uniforms, Object[] uniformValues, int vertexOffset, int numVertices)
    {
        this.program = program;
        this.uniforms = uniforms;
        this.uniformValues = uniformValues;
        this.vertexOffset = vertexOffset;
        this.numVertices = numVertices;
    }
}
