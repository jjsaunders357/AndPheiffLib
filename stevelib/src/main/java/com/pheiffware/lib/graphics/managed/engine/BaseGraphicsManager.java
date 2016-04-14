package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.mesh.MeshGroup;

import java.util.List;
import java.util.Map;

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
        transferData = new GraphicsManagerTransferData(indexBuffer, manGL, programs, staticVertexBuffers)
    }


    public ObjectRenderHandle addMeshGroup(MeshGroup meshGroup)
    {
        ObjectRenderHandle objectRenderHandle = new ObjectRenderHandle();
        Map<Material, List<Mesh>> meshMap = meshGroup.getMeshMap();
        for (Map.Entry<Material, List<Mesh>> meshEntry : meshMap.entrySet())
        {
            Material material = meshEntry.getKey();
            int meshListOffset = indexBufferLength;
            int meshListLength = 0;
            List<Mesh> meshList = meshEntry.getValue();
            for (Mesh mesh : meshList)
            {
                transferMeshes.add(mesh);

                indexBufferLength += mesh.getNumVertexIndices();
                meshListLength += mesh.getNumVertexIndices();
                vertexBufferLength += mesh.getNumUniqueVertices();
            }
            MeshRenderHandle meshHandle = new MeshRenderHandle(material, meshListOffset, meshListLength);
            objectRenderHandle.addMeshHandle(meshHandle);
        }
        objectRenderHandles.add(objectRenderHandle);
        return objectRenderHandle;
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
            indexWriteOffset += transferMesh.getNumVertexIndices();
            vertexOffset += transferMesh.getNumUniqueVertices();
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
