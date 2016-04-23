package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.managed.program.Uniform;

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
    private final Program[] programs;
    private final Map<Program, Integer> programIndexLookup = new HashMap<>();
    private final IndexBuffer indexBuffer = new IndexBuffer(false);
    private final StaticVertexBuffer[] staticVertexBuffers;

    private GraphicsManagerTransferData transferData;

    public BaseGraphicsManager(Program[] programs)
    {
        this.programs = programs;
        for (int i = 0; i < programs.length; i++)
        {
            programIndexLookup.put(programs[i], i);
        }
        this.staticVertexBuffers = new StaticVertexBuffer[programs.length];
        createVertexBuffers(programs);
        transferData = new GraphicsManagerTransferData(indexBuffer, programs, staticVertexBuffers);
    }

    protected void createVertexBuffers(Program[] programs)
    {
        for (int i = 0; i < programs.length; i++)
        {
            Program program = programs[i];
            staticVertexBuffers[i] = new StaticVertexBuffer(program);
        }
    }

    public final ObjectRenderHandle addObject(Mesh[] meshes, int[] programIndices, UniformNameValue[][] defaultUniformNameValuesArray)
    {
        ObjectRenderHandle objectRenderHandle = new ObjectRenderHandle();
        for (int i = 0; i < meshes.length; i++)
        {
            int programIndex = programIndices[i];
            Mesh mesh = meshes[i];

            UniformNameValue[] defaultUniformNameValues = defaultUniformNameValuesArray[i];
            MeshRenderHandle meshRenderHandle = addMesh(mesh, programIndex, defaultUniformNameValues);
            objectRenderHandle.addMeshHandle(meshRenderHandle);
        }
        return objectRenderHandle;
    }

    public final MeshRenderHandle addMesh(Mesh mesh, Program program, UniformNameValue[] defaultUniformNameValues)
    {
        return addMesh(mesh, program, programIndexLookup.get(program), defaultUniformNameValues);
    }

    private MeshRenderHandle addMesh(Mesh mesh, int programIndex, UniformNameValue[] defaultUniformNameValues)
    {
        return addMesh(mesh, programs[programIndex], programIndex, defaultUniformNameValues);
    }

    private MeshRenderHandle addMesh(Mesh mesh, Program program, int programIndex, UniformNameValue[] defaultUniformNameValues)
    {
        int meshIndexOffset = transferData.addMesh(mesh, programIndex);

        Uniform[] defaultUniforms = new Uniform[defaultUniformNameValues.length];
        Object[] defaultUniformValues = new Object[defaultUniformNameValues.length];
        for (int i = 0; i < defaultUniformNameValues.length; i++)
        {
            defaultUniforms[i] = program.getUniform(defaultUniformNameValues[i].name);
            defaultUniformValues[i] = defaultUniformNameValues[i].value;
        }
        return new MeshRenderHandle(programIndex, defaultUniforms, defaultUniformValues, meshIndexOffset, mesh.getNumIndices());
    }

    public final void transfer()
    {
        transferData.transfer();
        transferData = null;
    }

    public void render(MeshRenderHandle meshHandle, String[] uniformNames, Object[] uniformValues)
    {
        Program program = programs[meshHandle.programIndex];
        StaticVertexBuffer staticVertexBuffer = staticVertexBuffers[meshHandle.programIndex];
        program.bind();
        staticVertexBuffer.bind();
        meshHandle.setUniforms();
        for (int i = 0; i < uniformNames.length; i++)
        {
            program.setUniformValue(uniformNames[i], uniformValues[i]);
        }
        indexBuffer.drawTriangles(meshHandle.vertexOffset, meshHandle.numVertices);
    }

    public void render(ObjectRenderHandle objectHandle, String[] uniformNames, Object[] uniformValues)
    {
        for (int i = 0; i < objectHandle.meshRenderHandles.size(); i++)
        {
            render(objectHandle.meshRenderHandles.get(i), uniformNames, uniformValues);
        }

    }

    public final void bindProgram(MeshRenderHandle meshHandle)
    {
        Program program = programs[meshHandle.programIndex];
        StaticVertexBuffer staticVertexBuffer = staticVertexBuffers[meshHandle.programIndex];
        program.bind();
        staticVertexBuffer.bind();
    }

    public final void setDefaultUniformValues(MeshRenderHandle meshHandle)
    {
        meshHandle.setUniforms();
    }

    public final void renderIndexBuffer(MeshRenderHandle meshHandle)
    {
        indexBuffer.drawTriangles(meshHandle.vertexOffset, meshHandle.numVertices);
    }
}
