package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.utils.MapCounter;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the job of transferring a collection of meshes/3D objects to the given index buffer and vertex buffers.  Each added mesh specifies the program it will be rendered by and
 * gets put in the corresponding vertex buffer.  All indices are put into the single provided index buffer.
 * <p/>
 * This is a one use class.  The usage pattern of the class is to repeatedly call addMesh() and then call transfer() once to move all data.  Afterwards, the reference to this
 * object should be forgotten so it can be garbage collected.
 * <p/>
 * Created by Steve on 4/14/2016.
 */
public class GraphicsManagerTransferData
{
    private final IndexBuffer indexBuffer;
    private final StaticVertexBuffer[] vertexBuffers;
    private final MapCounter<StaticVertexBuffer> vertexBufferLengths;

    private int indexBufferLength = 0;

    //List of added meshes, in order of addition
    private final List<Mesh> meshesForTransfer = new ArrayList<>();

    //VertexBuffer each added mesh is associated with
    private final List<StaticVertexBuffer> meshVertexBuffers = new ArrayList<>();

    //The current object being constructed (meshes are being added to it)
    private ObjectRenderHandle currentObject = null;

    public GraphicsManagerTransferData(IndexBuffer indexBuffer, StaticVertexBuffer[] vertexBuffers)
    {
        this.indexBuffer = indexBuffer;
        this.vertexBuffers = vertexBuffers;
        vertexBufferLengths = new MapCounter<>();
    }

    /**
     * Adds a mesh for transfer to the given technique's vertex buffer.
     *
     * @param mesh      mesh to render
     * @param vertexBuffer
     * @return the location in the index buffer where this mesh will be (specified in terms of vertex offset, *2 will give byte offset)
     */
    public int addMesh(Mesh mesh, StaticVertexBuffer vertexBuffer)
    {
        meshesForTransfer.add(mesh);
        meshVertexBuffers.add(vertexBuffer);

        int meshIndexOffset = indexBufferLength;
        indexBufferLength += mesh.getNumIndices();
        vertexBufferLengths.addCount(vertexBuffer, mesh.getNumVertices());
        return meshIndexOffset;
    }

    public void transfer()
    {
        indexBuffer.allocate(indexBufferLength);
        for (int i = 0; i < vertexBuffers.length; i++)
        {
            vertexBuffers[i].allocate(vertexBufferLengths.getCount(vertexBuffers[i]));
        }
        int indexWriteOffset = 0;
        MapCounter<StaticVertexBuffer> vertexBufferOffsets = new MapCounter<>();
        for (int i = 0; i < meshesForTransfer.size(); i++)
        {
            Mesh transferMesh = meshesForTransfer.get(i);
            StaticVertexBuffer vertexBuffer = meshVertexBuffers.get(i);
            int vertexWriteOffset = vertexBufferOffsets.getCount(vertexBuffer);
            indexBuffer.putIndicesWithOffset(transferMesh.vertexIndices, indexWriteOffset, (short) vertexWriteOffset);
            vertexBuffer.putVertexAttributes(transferMesh, vertexWriteOffset);
            indexWriteOffset += transferMesh.getNumIndices();
            vertexBufferOffsets.addCount(vertexBuffer, transferMesh.getNumVertices());
        }
        indexBuffer.transfer();
        for (int i = 0; i < vertexBuffers.length; i++)
        {
            vertexBuffers[i].transfer();
        }
    }

    ObjectRenderHandle startNewObjectDef()
    {
        currentObject = new ObjectRenderHandle();
        return currentObject;
    }

    ObjectRenderHandle getCurrentObjectDef()
    {
        return currentObject;
    }

    void endObjectDef()
    {
        currentObject = null;
    }
}
