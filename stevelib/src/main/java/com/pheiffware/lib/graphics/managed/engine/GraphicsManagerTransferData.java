package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.ManGL;
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
 * Created by Steve on 4/14/2016.
 */
public class GraphicsManagerTransferData
{
    private final IndexBuffer indexBuffer;
    private final ManGL manGL;
    private final Program[] programs;
    private final StaticVertexBuffer[] staticVertexBuffers;

    private int indexBufferLength = 0;
    private int vertexBufferLength = 0;
    private final List<Mesh> transferMeshes = new ArrayList<>();
    private final List<ObjectRenderHandle> objectRenderHandles = new ArrayList<>();

    public GraphicsManagerTransferData(IndexBuffer indexBuffer, ManGL manGL, Program[] programs, StaticVertexBuffer[] staticVertexBuffers)
    {
        this.indexBuffer = indexBuffer;
        this.manGL = manGL;
        this.programs = programs;
        this.staticVertexBuffers = staticVertexBuffers;
    }

    public MeshRenderHandle addMeshes()
    {

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
}
