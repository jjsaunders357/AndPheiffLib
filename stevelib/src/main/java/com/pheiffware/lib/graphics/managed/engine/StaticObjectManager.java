package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.geometry.collada.ColladaMaterial;
import com.pheiffware.lib.geometry.collada.MeshGroup;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO: Comment me!
 * <p/>
 * Created by Steve on 4/10/2016.
 */
public class StaticObjectManager
{
    private final Program program;
    private final StaticVertexBuffer staticVertexBuffer;
    private final IndexBuffer indexBuffer = new IndexBuffer(false);

    private int indexBufferLength = 0;
    private int vertexBufferLength = 0;
    private List<Mesh> transferMeshes = new ArrayList<>();
    private List<ObjectRenderHandle> objectRenderHandles = new ArrayList<>();

    public StaticObjectManager(Program program, String[] staticAttributes)
    {
        this.program = program;
        staticVertexBuffer = new StaticVertexBuffer(program, staticAttributes);
    }

    public ObjectRenderHandle addMeshGroup(MeshGroup meshGroup)
    {
        ObjectRenderHandle objectRenderHandle = new ObjectRenderHandle();
        Map<ColladaMaterial, List<Mesh>> meshMap = meshGroup.getMeshMap();
        for (Map.Entry<ColladaMaterial, List<Mesh>> meshEntry : meshMap.entrySet())
        {
            ColladaMaterial colladaMaterial = meshEntry.getKey();
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
            MeshRenderHandle meshHandle = new MeshRenderHandle(colladaMaterial, meshListOffset, meshListLength);
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
