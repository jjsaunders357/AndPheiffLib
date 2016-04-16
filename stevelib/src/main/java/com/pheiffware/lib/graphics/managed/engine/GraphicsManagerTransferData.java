package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.utils.MapCounter;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Comment me!
 * <p/>
 * Created by Steve on 4/14/2016.
 */
public class GraphicsManagerTransferData
{
    private final IndexBuffer indexBuffer;
    private final ManGL manGL;
    private final Program[] programs;
    private final StaticVertexBuffer[] staticVertexBuffers;

    private int indexBufferLength = 0;
    private final MapCounter<Program> vertexBufferLength = new MapCounter<>();
    private final List<Mesh> transferMeshes = new ArrayList<>();
    private final List<ObjectRenderHandle> objectRenderHandles = new ArrayList<>();

    public GraphicsManagerTransferData(IndexBuffer indexBuffer, ManGL manGL, Program[] programs, StaticVertexBuffer[] staticVertexBuffers)
    {
        this.indexBuffer = indexBuffer;
        this.manGL = manGL;
        this.programs = programs;
        this.staticVertexBuffers = staticVertexBuffers;
    }

    public MeshRenderHandle addMesh(Mesh mesh, Program program, String[] attributes, Object[] attributeValues)
    {
        int meshNumIndices = mesh.getNumIndices();
        int meshIndexOffset = indexBufferLength;
        transferMeshes.add(mesh);
        indexBufferLength += meshNumIndices;
        vertexBufferLength.addCount(program, mesh.getNumVertices());
        return new MeshRenderHandle(program, attributes, attributeValues, meshIndexOffset, meshNumIndices);
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
//    public ObjectRenderHandle addMeshGroup(MeshGroup meshGroup)
//    {
//        ObjectRenderHandle objectRenderHandle = new ObjectRenderHandle();
//        Map<Material, List<Mesh>> meshMap = meshGroup.getMeshMap();
//        for (Map.Entry<Material, List<Mesh>> meshEntry : meshMap.entrySet())
//        {
//            Material material = meshEntry.getKey();
//            int meshListOffset = indexBufferLength;
//            int meshListLength = 0;
//            List<Mesh> meshList = meshEntry.getValue();
//            for (Mesh mesh : meshList)
//            {
//                transferMeshes.add(mesh);
//
//                indexBufferLength += mesh.getNumIndices();
//                meshListLength += mesh.getNumIndices();
//                vertexBufferLength += mesh.getNumVertices();
//            }
//            MeshRenderHandle meshHandle = new MeshRenderHandle(material, meshListOffset, meshListLength);
//            objectRenderHandle.addMeshHandle(meshHandle);
//        }
//        objectRenderHandles.add(objectRenderHandle);
//        return objectRenderHandle;
//    }
}
