package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

import com.pheiffware.lib.graphics.Mesh;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A class for packing mesh index data into a vertex buffer.  Given a series of meshes this packs index data into a vertex buffer after allocating to an appropriate size.
 * Each mesh is given a handle allowing easy access to the corresponding index data later.
 * <p>
 * Usage pattern:
 * MeshVertexDataPacker x = new MeshVertexDataPacker()
 * x.wrap(vertexBuffer)
 * x.addMesh()
 * ...
 * x.addMesh()
 * x.packBuffer()
 * <p>
 * x.wrapBuffer(otherVertexBuffer)
 * ...
 * <p>
 * Created by Steve on 6/14/2017.
 */

public class MeshVertexIndexPacker
{
    //For each mesh, keep track of the handle given to the user of the class.  This handle must be setup when the meshes are packed so that data can be accessed.
    private final List<Mesh> meshes = new LinkedList<>();
    private final List<VertexIndexHandle> meshHandles = new LinkedList<>();

    /**
     * Adds mesh to list which should be packed.  Returns a handle to data which is invalid until packBuffer() is called.
     *
     * @param mesh the mesh to add to the vertex buffer.
     * @return a handle to use for binding to a program/technique for rendering
     */
    public final VertexIndexHandle addMesh(Mesh mesh)
    {
        VertexIndexHandle meshHandle = new VertexIndexHandle();
        meshes.add(mesh);
        meshHandles.add(meshHandle);
        return meshHandle;
    }

    /**
     * Allocates the given vertex buffer, then packs all data into the buffer.
     * All meshHandles are updated to contain references to the corresponding packed data.
     * All internal references to mesh data are destroyed and class is prepared to wrap another mesh.
     */
    public final void packBuffer(IndexBuffer indexBuffer)
    {
        int spaceRequired = calcRequiredSpace();
        indexBuffer.allocateSoftwareBuffer(spaceRequired);
        ByteBuffer byteBuffer = indexBuffer.editBuffer(0, spaceRequired);

        Iterator<Mesh> meshI = meshes.iterator();
        Iterator<VertexIndexHandle> meshH = meshHandles.iterator();
        while (meshI.hasNext())
        {
            Mesh mesh = meshI.next();
            VertexIndexHandle meshHandle = meshH.next();
            int byteOffset = byteBuffer.position();
            putMesh(byteBuffer, mesh);
            meshHandle.setup(mesh.getNumVertices(), byteOffset, indexBuffer);
        }
        meshes.clear();
        meshHandles.clear();
    }

    /**
     * Calculate the total size of the buffer required to hold all mesh data vertex indices, in bytes.
     *
     * @return
     */
    private int calcRequiredSpace()
    {
        int numVertices = 0;
        for (Mesh mesh : meshes)
        {
            numVertices += mesh.getNumVertices();
        }
        return numVertices * 2;
    }


    /**
     * Puts vertex indices of mesh into the given ByteBuffer at its current position.
     *
     * @param byteBuffer the byte buffer to put the data in.
     * @param mesh       the mesh to store
     */
    private void putMesh(ByteBuffer byteBuffer, Mesh mesh)
    {
        for (int i = 0; i < mesh.getNumVertices(); i++)
        {
            byteBuffer.putShort(mesh.getVertexIndices()[i]);
        }
    }
}
