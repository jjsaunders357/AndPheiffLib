package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Technique;

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

    private final Technique[] techniques;
    private final int vertexBufferLengths[];

    private int indexBufferLength = 0;

    //List of added meshes, in order of addition
    private final List<Mesh> meshesForTransfer = new ArrayList<>();

    //Technique index each added mesh is associated with
    private final List<Integer> meshTechniqueIndices = new ArrayList<>();

    public GraphicsManagerTransferData(IndexBuffer indexBuffer, Technique[] techniques)
    {
        this.indexBuffer = indexBuffer;
        this.techniques = techniques;
        vertexBufferLengths = new int[techniques.length];
    }

    //TODO: Remove need to reference by index
    /**
     * Adds a mesh for transfer to the given technique's vertex buffer.
     *
     * @param mesh         mesh to render
     * @param techniqueIndex technique to render with
     * @return the location in the index buffer where this mesh will be (specified in terms of vertex offset, *2 will give byte offset)
     */
    public int addMesh(Mesh mesh, int techniqueIndex)
    {
        meshesForTransfer.add(mesh);
        meshTechniqueIndices.add(techniqueIndex);
        int meshIndexOffset = indexBufferLength;
        indexBufferLength += mesh.getNumIndices();
        vertexBufferLengths[techniqueIndex] += mesh.getNumVertices();
        return meshIndexOffset;
    }

    public void transfer()
    {
        indexBuffer.allocate(indexBufferLength);
        for (int i = 0; i < techniques.length; i++)
        {
            techniques[i].allocateBuffers(vertexBufferLengths[i]);
        }
        int indexWriteOffset = 0;
        int[] techniqueVertexOffsets = new int[techniques.length];

        for (int i = 0; i < meshesForTransfer.size(); i++)
        {
            Mesh transferMesh = meshesForTransfer.get(i);
            int techniqueIndex = meshTechniqueIndices.get(i);
            int vertexWriteOffset = techniqueVertexOffsets[techniqueIndex];
            Technique technique = techniques[techniqueIndex];
            indexBuffer.putIndicesWithOffset(transferMesh.vertexIndices, indexWriteOffset, (short) vertexWriteOffset);
            technique.putVertexAttributes(transferMesh, vertexWriteOffset);
            indexWriteOffset += transferMesh.getNumIndices();
            techniqueVertexOffsets[techniqueIndex] += transferMesh.getNumVertices();
        }
        indexBuffer.transfer();
        for (int i = 0; i < techniques.length; i++)
        {
            techniques[i].transferVertexData();
        }
    }
}
