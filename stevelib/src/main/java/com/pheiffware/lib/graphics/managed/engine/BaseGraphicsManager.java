package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;

/**
 * TODO: Comment me!
 * <p/>
 * Created by Steve on 4/13/2016.
 */
public abstract class BaseGraphicsManager
{
    private final IndexBuffer indexBuffer = new IndexBuffer(false);
    private final ManGL manGL;
    private final Program[] programs;
    private final StaticVertexBuffer[] staticVertexBuffers;

    private GraphicsManagerTransferData transferData;

    public BaseGraphicsManager(ManGL manGL, Program[] programs)
    {
        this.manGL = manGL;
        this.programs = programs;
        this.staticVertexBuffers = new StaticVertexBuffer[programs.length];
        for (int i = 0; i < programs.length; i++)
        {
            Program program = programs[i];
            staticVertexBuffers[i] = new StaticVertexBuffer(program);
        }
        transferData = new GraphicsManagerTransferData(indexBuffer, manGL, programs, staticVertexBuffers);
    }

    public void transfer()
    {
        indexBuffer.allocate(indexBufferLength);
        staticVertexBuffer.allocate(vertexBufferLength);
        int indexWriteOffset = 0;
        int vertexOffset = 0;
        for (Mesh transferMesh : transferMeshes)
        {
            indexBuffer.putIndicesWithOffset(transferMesh.vertexIndices, indexWriteOffset, (short) vertexOffset);
            staticVertexBuffer.putAttributeFloats("vertexPosition", transferMesh.getPositionData(), vertexOffset);
            staticVertexBuffer.putAttributeFloats("vertexNormal", transferMesh.getNormalData(), vertexOffset);
            //TODO: Put texture coordinates if applicable
            indexWriteOffset += transferMesh.getNumIndices();
            vertexOffset += transferMesh.getNumVertices();
        }
        indexBuffer.transfer();
        staticVertexBuffer.transfer();
        transferMeshes = null;
    }

    public Program getProgram()
    {
        return program;
    }

    public void render(ObjectRenderHandle objectHandle)
    {
        staticVertexBuffer.bind();
        for (MeshRenderHandle meshHandle : objectHandle.meshRenderHandles)
        {

            indexBuffer.drawTriangles(meshHandle.vertexOffset, meshHandle.numVertices);
        }
    }


}
