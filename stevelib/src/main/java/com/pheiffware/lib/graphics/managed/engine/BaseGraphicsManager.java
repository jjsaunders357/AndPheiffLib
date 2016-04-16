package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.managed.Uniform;
import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Comment me!
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

    public final ObjectRenderHandle addColladaObject(ColladaObject3D colladaObject3D)
    {
        return null;
    }

    public final ObjectRenderHandle addObject(Mesh[] meshes, Program[] programs, String[][] defaultUniformNamesArray, Object[][] defaultUniformValuesArray)
    {

        ObjectRenderHandle objectRenderHandle = new ObjectRenderHandle();
        for (int i = 0; i < meshes.length; i++)
        {
            Program program = programs[i];
            Mesh mesh = meshes[i];

            String[] defaultUniformNames = defaultUniformNamesArray[i];
            Object[] defaultUniformValues = defaultUniformValuesArray[i];
            MeshRenderHandle meshRenderHandle = addMesh(mesh, programIndexLookup.get(program), uniformsFromNames(program, defaultUniformNames), defaultUniformValues);
            objectRenderHandle.addMeshHandle(meshRenderHandle);
        }
        return objectRenderHandle;
    }

    public final MeshRenderHandle addMesh(Mesh mesh, int programIndex, Uniform[] defaultUniforms, Object[] defaultUniformValues)
    {
        int meshIndexOffset = transferData.addMesh(mesh, programIndex);
        return new MeshRenderHandle(programIndex, defaultUniforms, defaultUniformValues, meshIndexOffset, mesh.getNumIndices());
    }

    public void transfer()
    {
        transferData.transfer();
        transferData = null;
    }

    public void render(MeshRenderHandle meshHandle, String[] uniformNames, Object[] uniformValues)
    {
        Program program = programs[meshHandle.programIndex];
        StaticVertexBuffer staticVertexBuffer = staticVertexBuffers[meshHandle.programIndex];
        staticVertexBuffer.bind();
        program.bind();
        meshHandle.setUniforms();
        for (int i = 0; i < uniformNames.length; i++)
        {
            program.setUniformValue(uniformNames[i], uniformValues[i]);
        }
        indexBuffer.drawTriangles(meshHandle.vertexOffset, meshHandle.numVertices);
    }

    public void renderOverride(MeshRenderHandle meshHandle, String[] uniformNames, Object[] uniformValues)
    {
        //TODO: Make more efficient
        Program program = programs[meshHandle.programIndex];
        StaticVertexBuffer staticVertexBuffer = staticVertexBuffers[meshHandle.programIndex];
        staticVertexBuffer.bind();
        program.bind();
        meshHandle.setUniforms();
        for (int i = 0; i < uniformNames.length; i++)
        {
            program.setUniformValueIfExists(uniformNames[i], uniformValues[i]);
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

    public void renderOverride(ObjectRenderHandle objectHandle, String[] uniformNames, Object[] uniformValues)
    {
        for (int i = 0; i < objectHandle.meshRenderHandles.size(); i++)
        {
            renderOverride(objectHandle.meshRenderHandles.get(i), uniformNames, uniformValues);
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
