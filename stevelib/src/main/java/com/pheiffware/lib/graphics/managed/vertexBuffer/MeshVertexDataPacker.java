package com.pheiffware.lib.graphics.managed.vertexBuffer;

import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.program.VertexAttributes;
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
 * Created by Steve on 6/12/2017.
 */

public class MeshVertexDataPacker
{
    //For each "Type" of mesh encountered, store a corresponding list of meshes of that type. Type is determined by the set of VertexAttributes it contains.
    private final MapLinkedList<EnumSet<VertexAttribute>, Mesh> meshTypeLists = new MapLinkedList<>();

    //For each mesh, keep track of the handle given to the user of the class.  This handle must be setup when the meshes are packed so that data can be accessed.
    private final Map<Mesh, VertexAttributeHandle> meshToHandleMap = new HashMap<>();

    /**
     * Adds mesh to list which should be packed.  Returns a handle to data which is invalid until packBuffer() is called.
     * <p>
     * NOTE: Adding the same mesh twice is illegal!
     *
     * @param mesh the mesh to add to the vertex buffer.  All vertex attributes will be stored.
     * @return a handle to use for binding to a program/technique for rendering
     */
    public final VertexAttributeHandle addMesh(Mesh mesh)
    {
        return addMesh(mesh, mesh.getAttributes());
    }

    /**
     * Adds specified vertex attributes of a mesh to list which should be packed.  Returns a handle to data which is invalid until packBuffer() is called.
     * <p>
     * NOTE: Adding the same mesh twice is illegal!
     *
     * @param mesh             the mesh to add to the vertex buffer
     * @param vertexAttributes the set of vertex attributes, from the mesh, to be stored.
     * @return a handle to use for binding to a program/technique for rendering
     */
    public final VertexAttributeHandle addMesh(Mesh mesh, EnumSet<VertexAttribute> vertexAttributes)
    {
        meshTypeLists.append(vertexAttributes, mesh);
        VertexAttributeHandle handle = new VertexAttributeHandle();
        meshToHandleMap.put(mesh, handle);
        return handle;
    }

    /**
     * Allocates the given vertex buffer, then packs all data into the buffer.
     * All meshHandles are updated to contain references to the corresponding packed data.
     * All internal references to mesh data are destroyed and class is prepared to wrap another mesh.
     */
    public final void packBuffer(AttributeVertexBuffer vertexBuffer)
    {
        int spaceRequired = calcRequiredSpace();
        vertexBuffer.allocateSoftwareBuffer(spaceRequired);
        ByteBuffer byteBuffer = vertexBuffer.editBuffer(0, spaceRequired);
        for (Map.Entry<EnumSet<VertexAttribute>, List<Mesh>> entry : meshTypeLists.entrySet())
        {
            VertexAttributes vertexAttributes = new VertexAttributes(entry.getKey());
            putAllMeshesOfType(vertexBuffer, byteBuffer, vertexAttributes, entry.getValue());
        }
        meshTypeLists.clear();
        meshToHandleMap.clear();
    }

    /**
     * Calculate the total size of the buffer required to hold all mesh data, in bytes.
     *
     * @return
     */
    private int calcRequiredSpace()
    {
        int size = 0;
        for (Map.Entry<EnumSet<VertexAttribute>, List<Mesh>> entry : meshTypeLists.entrySet())
        {
            VertexAttributes vertexAttributes = new VertexAttributes(entry.getKey());

            List<Mesh> meshList = entry.getValue();
            for (Mesh mesh : meshList)
            {
                size += mesh.getNumVertices() * vertexAttributes.getVertexByteSize();
            }
        }
        return size;
    }


    /**
     * Put all meshes of the given "Type" in the buffer at its current position.
     *
     * @param vertexBuffer     the vertex buffer containing the data
     * @param byteBuffer       the byte buffer to put the data in.
     * @param vertexAttributes the type of the meshes
     * @param meshList         the list of meshes, of this type, to transfer
     */
    private void putAllMeshesOfType(AttributeVertexBuffer vertexBuffer, ByteBuffer byteBuffer, VertexAttributes vertexAttributes, List<Mesh> meshList)
    {
        for (Mesh mesh : meshList)
        {
            int byteOffset = byteBuffer.position();
            putMesh(byteBuffer, mesh, vertexAttributes);
            VertexAttributeHandle handle = meshToHandleMap.get(mesh);
            handle.setup(byteOffset, mesh.getNumVertices(), vertexAttributes, vertexBuffer);
        }
    }


    /**
     * Packs the given vertex attributes of a mesh into the buffer.
     *
     * @param byteBuffer       the byte buffer to put the data in.
     * @param mesh             the mesh to store
     * @param vertexAttributes the set of vertex attributes of the mesh to store
     */
    private void putMesh(ByteBuffer byteBuffer, Mesh mesh, VertexAttributes vertexAttributes)
    {
        int vertexByteSize = vertexAttributes.getVertexByteSize();
        int startByteOffset = byteBuffer.position();

        for (VertexAttribute vertexAttribute : vertexAttributes.getAttributes())
        {
            //Start putting data in the array at the given byteOffset AND the given attributes offset within a vertex
            byteBuffer.position(startByteOffset + vertexAttributes.getAttributeByteOffset(vertexAttribute));
            float[] data = mesh.getAttributeData(vertexAttribute);
            vertexAttribute.putDataInBuffer(byteBuffer, vertexByteSize, data);
        }
    }
}

