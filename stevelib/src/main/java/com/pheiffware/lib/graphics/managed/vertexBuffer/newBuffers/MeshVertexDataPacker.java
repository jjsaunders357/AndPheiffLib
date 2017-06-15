package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.program.VertexAttributes;
import com.pheiffware.lib.utils.MathUtils;
import com.pheiffware.lib.utils.dataContainers.MapLinkedList;

import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class for packing mesh vertex data into a VertexBuffer.  Given a series of meshes this organizes them into
 * similar "types" and packs them into a vertex buffer after allocating to an appropriate size.
 * A "type" is an identical EnumSet:VertexAttribute.  Similar types are packed together.
 * Each mesh is given a handle allowing easy access to the corresponding data later.
 * <p>
 * Usage pattern:
 * MeshVertexDataPacker x = new MeshVertexDataPacker()
 * x.wrap(vertexBuffer)
 * x.addMesh()
 * ...
 * x.addMesh()
 * x.pack()
 * <p>
 * x.wrapBuffer(otherVertexBuffer)
 * ...
 * <p>
 * Created by Steve on 6/12/2017.
 */

class MeshVertexDataPacker
{
    //For each "Type" of mesh encountered, store a corresponding list of meshes of that type. Type is determined by the set of VertexAttributes it contains.
    private final MapLinkedList<EnumSet<VertexAttribute>, Mesh> meshTypeLists = new MapLinkedList<>();

    //For each mesh, keep track of the handle given to the user of the class.  This handle must be setup when the meshes are packed so that data can be accessed.
    private final Map<Mesh, VertexAttributeHandle> meshToHandleMap = new HashMap<>();


    /**
     * Adds mesh to list which should be packed.  Returns a handle to data which is invalid until pack() is called.
     *
     * @param mesh the mesh to add to the vertex buffer.  All vertex attributes will be stored.
     * @return a handle to use for binding to a program/technique for rendering
     */
    VertexAttributeHandle addMesh(Mesh mesh)
    {
        return addMesh(mesh, mesh.getAttributes());
    }

    /**
     * Adds specified vertex attributes of a mesh to list which should be packed.  Returns a handle to data which is invalid until pack() is called.
     *
     * @param mesh             the mesh to add to the vertex buffer
     * @param vertexAttributes the set of vertex attributes, from the mesh, to be stored.
     * @return a handle to use for binding to a program/technique for rendering
     */
    VertexAttributeHandle addMesh(Mesh mesh, EnumSet<VertexAttribute> vertexAttributes)
    {
        meshTypeLists.append(vertexAttributes, mesh);
        VertexAttributeHandle handle = new VertexAttributeHandle();
        meshToHandleMap.put(mesh, handle);
        return handle;
    }

    /**
     * Calculate the total size of the buffer required to hold all mesh data, in bytes.
     *
     * @return
     */
    int calcRequiredSpace()
    {
        int size = 0;
        for (Map.Entry<EnumSet<VertexAttribute>, List<Mesh>> entry : meshTypeLists.entrySet())
        {
            VertexAttributes vertexAttributes = new VertexAttributes(entry.getKey());

            //All meshes, of the same type, are stored in the buffer at an even vertex-size boundary.
            size = MathUtils.calcNextEvenBoundary(size, vertexAttributes.getVertexByteSize());

            List<Mesh> meshList = entry.getValue();
            for (Mesh mesh : meshList)
            {
                size += mesh.getNumVertices() * vertexAttributes.getVertexByteSize();
            }
        }
        return size;
    }

    /**
     * All meshHandles are updated to contain references to the corresponding packed data.
     * All internal references to mesh data are destroyed and class is prepared to wrap another mesh.
     */
    void pack(ByteBuffer byteBuffer)
    {
        for (Map.Entry<EnumSet<VertexAttribute>, List<Mesh>> entry : meshTypeLists.entrySet())
        {
            VertexAttributes vertexAttributes = new VertexAttributes(entry.getKey());
            putAllMeshesOfType(byteBuffer, vertexAttributes, entry.getValue());
        }
        meshTypeLists.clear();
        meshToHandleMap.clear();
    }

    /**
     * Put all meshes of the given "Type" in the buffer at its current position.
     *
     * @param byteBuffer       the byte buffer to put the data in.
     * @param vertexAttributes the type of the meshes
     * @param meshList         the list of meshes, of this type, to transfer
     */
    private void putAllMeshesOfType(ByteBuffer byteBuffer, VertexAttributes vertexAttributes, List<Mesh> meshList)
    {
        int vertexByteSize = vertexAttributes.getVertexByteSize();

        //OpenGL requires us to align vertex data to an even vertex-size boundary.
        int startByteOffset = MathUtils.calcNextEvenBoundary(byteBuffer.position(), vertexByteSize);
        byteBuffer.position(startByteOffset);

        for (Mesh mesh : meshList)
        {
            int vertexOffset = putMesh(byteBuffer, mesh, vertexAttributes);
            VertexAttributeHandle handle = meshToHandleMap.get(mesh);
            handle.setup(vertexOffset, vertexAttributes);
        }
    }


    /**
     * Puts the given vertex attributes of a mesh into the buffer and returns the vertex index where it is stored.
     * All data is put at the buffer's current position, which must be at an even vertex-size boundary!
     * This index is given to openGL during the rendering process.
     * Given attribute data will be packed into vertex buffer
     *
     * @param byteBuffer       the byte buffer to put the data in.
     * @param mesh             the mesh to store
     * @param vertexAttributes the set of vertex attributes of the mesh to store
     * @return the vertex "index" where the data is stored.  When binding to this buffer, this is the effective number of vertices, into the buffer, to start rendering.
     */
    private int putMesh(ByteBuffer byteBuffer, Mesh mesh, VertexAttributes vertexAttributes)
    {
        int vertexByteSize = vertexAttributes.getVertexByteSize();
        int startByteOffset = byteBuffer.position();

        for (VertexAttribute vertexAttribute : vertexAttributes.getAttributes())
        {
            //Start putting data in the array at the given byteOffset AND the given attributes offset within a vertice
            byteBuffer.position(startByteOffset + vertexAttributes.getAttributeByteOffset(vertexAttribute));
            float[] data = mesh.getAttributeData(vertexAttribute);
            vertexAttribute.putDataInBuffer(byteBuffer, vertexByteSize, data);
        }
        return startByteOffset / vertexByteSize;
    }
}

