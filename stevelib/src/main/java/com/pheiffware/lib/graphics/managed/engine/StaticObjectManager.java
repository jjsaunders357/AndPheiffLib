package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.Color4F;
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
    private final String[] staticAttributes;
    private StaticVertexBuffer staticVertexBuffer;
    private IndexBuffer indexBuffer;
    private int indexBufferLength = 0;
    private int vertexBufferLength = 0;
    private List<Mesh> transferMeshes = new ArrayList<>();
    private List<StaticObjectHandle> staticObjectHandles = new ArrayList<>();

    public StaticObjectManager(Program program, String[] staticAttributes)
    {
        this.program = program;
        this.staticAttributes = staticAttributes;
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
        indexBuffer = new IndexBuffer(indexBufferLength, false);
        staticVertexBuffer = new StaticVertexBuffer(program, vertexBufferLength, staticAttributes);
        int indexWriteOffset = 0;
        int vertexOffset = 0;
        for (Mesh transferMesh : transferMeshes)
        {
            indexBuffer.putIndicesWithOffset(transferMesh.vertexIndices, indexWriteOffset, (short) vertexOffset);
            staticVertexBuffer.putAttributeFloats("vertexPosition", transferMesh.getPositionData(), vertexOffset);
            staticVertexBuffer.putAttributeFloats("vertexNormal", transferMesh.getNormalData(), vertexOffset);
            staticVertexBuffer.putAttributeFloats("vertexColor", transferMesh.generateSingleColorData(new Color4F(0.0f, 0.6f, 0.9f, 1.0f)), vertexOffset);
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
