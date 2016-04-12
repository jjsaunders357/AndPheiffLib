package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.mesh.MeshGroup;

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
    private List<StaticObjectHandle> staticObjectHandles = new ArrayList<>();

    public StaticObjectManager(Program program, String[] staticAttributes)
    {
        this.program = program;
        staticVertexBuffer = new StaticVertexBuffer(program, staticAttributes);
    }

    public StaticObjectHandle addMeshGroup(MeshGroup meshGroup)
    {
        StaticObjectHandle staticObjectHandle = new StaticObjectHandle();
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
            StaticMeshHandle meshHandle = new StaticMeshHandle(material, meshListOffset, meshListLength);
            staticObjectHandle.addMeshHandle(meshHandle);
        }
        staticObjectHandles.add(staticObjectHandle);
        return staticObjectHandle;
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

    public void render(StaticObjectHandle objectHandle)
    {
        staticVertexBuffer.bind();
        for (StaticMeshHandle meshHandle : objectHandle.staticMeshHandles)
        {
            indexBuffer.drawTriangles(meshHandle.vertexOffset, meshHandle.numVertices);
        }
    }
}
