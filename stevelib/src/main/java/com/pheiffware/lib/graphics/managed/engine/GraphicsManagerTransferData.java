package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.techniques.ShadConst;

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

    private final Program[] programs;
    private final StaticVertexBuffer[] staticVertexBuffers;
    private final int vertexBufferLengths[];

    private int indexBufferLength = 0;

    //List of added meshes, in order of addition
    private final List<Mesh> meshesForTransfer = new ArrayList<>();

    //Program index each added mesh is associated with
    private final List<Integer> meshProgramIndices = new ArrayList<>();

    public GraphicsManagerTransferData(IndexBuffer indexBuffer, Program[] programs, StaticVertexBuffer[] staticVertexBuffers)
    {
        this.indexBuffer = indexBuffer;
        this.programs = programs;
        this.staticVertexBuffers = staticVertexBuffers;
        vertexBufferLengths = new int[programs.length];
    }

    /**
     * Adds a mesh for transfer to the given program's vertex buffer.
     *
     * @param mesh         mesh to render
     * @param programIndex program to render with
     * @return the location in the index buffer where this mesh will be (specified in terms of vertex offset, *2 will give byte offset)
     */
    public int addMesh(Mesh mesh, int programIndex)
    {
        meshesForTransfer.add(mesh);
        meshProgramIndices.add(programIndex);
        int meshIndexOffset = indexBufferLength;
        indexBufferLength += mesh.getNumIndices();
        vertexBufferLengths[programIndex] += mesh.getNumVertices();
        return meshIndexOffset;
    }

    public void transfer()
    {
        indexBuffer.allocate(indexBufferLength);
        for (int i = 0; i < programs.length; i++)
        {
            staticVertexBuffers[i].allocate(vertexBufferLengths[i]);
        }
        int indexWriteOffset = 0;
        int[] programVertexOffsets = new int[programs.length];

        for (int i = 0; i < meshesForTransfer.size(); i++)
        {
            Mesh transferMesh = meshesForTransfer.get(i);
            int programIndex = meshProgramIndices.get(i);
            int vertexWriteOffset = programVertexOffsets[programIndex];
            Program program = programs[programIndex];
            StaticVertexBuffer staticVertexBuffer = staticVertexBuffers[programIndex];
            indexBuffer.putIndicesWithOffset(transferMesh.vertexIndices, indexWriteOffset, (short) vertexWriteOffset);
            transferMeshAttributes(transferMesh, program, staticVertexBuffer, vertexWriteOffset);
            indexWriteOffset += transferMesh.getNumIndices();
            programVertexOffsets[programIndex] += transferMesh.getNumVertices();
        }
        indexBuffer.transfer();
        for (int i = 0; i < programs.length; i++)
        {
            staticVertexBuffers[i].transfer();
        }
    }

    protected void transferMeshAttributes(Mesh transferMesh, Program program, StaticVertexBuffer staticVertexBuffer, int vertexWriteOffset)
    {
        if (program.getAttributeNames().contains(ShadConst.VERTEX_POSITION_ATTRIBUTE))
        {
            staticVertexBuffer.putAttributeFloats(ShadConst.VERTEX_POSITION_ATTRIBUTE, transferMesh.getPositionData(), vertexWriteOffset);
        }
        if (program.getAttributeNames().contains(ShadConst.VERTEX_NORMAL_ATTRIBUTE))
        {
            staticVertexBuffer.putAttributeFloats(ShadConst.VERTEX_NORMAL_ATTRIBUTE, transferMesh.getNormalData(), vertexWriteOffset);
        }
        if (program.getAttributeNames().contains(ShadConst.VERTEX_TEXCOORD_ATTRIBUTE))
        {
            staticVertexBuffer.putAttributeFloats(ShadConst.VERTEX_TEXCOORD_ATTRIBUTE, transferMesh.getTexCoordData(), vertexWriteOffset);
        }
    }

}
