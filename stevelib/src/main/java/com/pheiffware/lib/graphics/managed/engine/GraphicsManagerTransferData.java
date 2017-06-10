package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.vertexBuffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.StaticVertexBuffer;
import com.pheiffware.lib.utils.MapCounter;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the job of transferring a collection of meshes/3D objects to the given index buffer and vertex buffers.  Each added mesh specifies the vertex buffer it will be stored
 * in.  All indices are put into the single provided index buffer.
 * <p/>
 * This is a one use class.  The usage pattern of the class is to repeatedly call addTransferMesh() and then call transfer() once to move all data.  Afterwards, this clears all references
 * to Meshes so they can be garbage collected if not needed elsewhere.
 * <p/>
 * Created by Steve on 4/14/2016.
 */
public class GraphicsManagerTransferData<M>
{
    //Where indices are stored for transfer to the GL
    private final IndexBuffer indexBuffer;

    //Where vertices are stored for transfer to the GL
    private final StaticVertexBuffer[] vertexBuffers;

    //Tracks how much has been added for transfer via the vertex buffers
    private final MapCounter<StaticVertexBuffer> vertexBufferLengths;

    //The number of indices stored in the index buffer
    private int indexBufferLength = 0;

    //List of added meshes, in order of addition
    private final List<Mesh> meshesForTransfer = new ArrayList<>();

    //VertexBuffer each added mesh is associated with
    private final List<StaticVertexBuffer> meshVertexBuffers = new ArrayList<>();

    //The current object being constructed (meshes are being added to it)
    private ObjectRenderHandle<M> currentObject = null;

    public GraphicsManagerTransferData(IndexBuffer indexBuffer, StaticVertexBuffer[] vertexBuffers)
    {
        this.indexBuffer = indexBuffer;
        this.vertexBuffers = vertexBuffers;
        vertexBufferLengths = new MapCounter<>();
    }

    /**
     * Adds a mesh for transfer to the given vertex buffer.
     *
     * @param mesh
     * @param vertexBuffer
     * @return the location in the index buffer where this mesh will be (specified in terms of vertex offset, *2 will give byte offset)
     */
    public int addTransferMesh(Mesh mesh, StaticVertexBuffer vertexBuffer)
    {
        meshesForTransfer.add(mesh);
        meshVertexBuffers.add(vertexBuffer);

        int meshIndexOffset = indexBufferLength;
        indexBufferLength += mesh.getNumIndices();
        vertexBufferLengths.addCount(vertexBuffer, mesh.getNumVertices());
        return meshIndexOffset;
    }

    /**
     * Allocate all buffers and transfer data to the GL.  After this, it forgets all references to meshes transferred.
     */
    public void transfer()
    {
        indexBuffer.allocate(indexBufferLength);
        for (StaticVertexBuffer vertexBuffer : vertexBuffers)
        {
            vertexBuffer.allocate(vertexBufferLengths.getCount(vertexBuffer));
        }
        int indexWriteOffset = 0;
        MapCounter<StaticVertexBuffer> vertexBufferOffsets = new MapCounter<>();
        for (int i = 0; i < meshesForTransfer.size(); i++)
        {
            Mesh transferMesh = meshesForTransfer.get(i);
            StaticVertexBuffer vertexBuffer = meshVertexBuffers.get(i);
            int vertexWriteOffset = vertexBufferOffsets.getCount(vertexBuffer);
            indexBuffer.putIndicesWithOffset(transferMesh.getVertexIndices(), indexWriteOffset, (short) vertexWriteOffset);
            vertexBuffer.putVertexAttributes(transferMesh, vertexWriteOffset);
            indexWriteOffset += transferMesh.getNumIndices();
            vertexBufferOffsets.addCount(vertexBuffer, transferMesh.getNumVertices());
        }
        indexBuffer.transfer();
        for (StaticVertexBuffer vertexBuffer : vertexBuffers)
        {
            vertexBuffer.transfer();
        }
        meshesForTransfer.clear();
        meshVertexBuffers.clear();
    }



}
