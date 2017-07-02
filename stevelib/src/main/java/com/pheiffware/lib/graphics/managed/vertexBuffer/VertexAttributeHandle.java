package com.pheiffware.lib.graphics.managed.vertexBuffer;

import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.managed.program.VertexAttributes;

import java.nio.ByteBuffer;

/**
 * Created by Steve on 6/14/2017.
 */

public class VertexAttributeHandle
{
    //Byte location in the vertex buffer where packed data associated with this handle starts
    private int byteOffset;

    //The upper limit of the data (used to set buffer limit when editing)
    private int byteLimit;

    //The vertex attributes associated with this handle
    private VertexAttributes vertexAttributes;

    //The buffer containing the data
    private AttributeVertexBuffer vertexBuffer;

    void setup(int byteOffset, int numVertices, VertexAttributes vertexAttributes, AttributeVertexBuffer vertexBuffer)
    {
        this.byteOffset = byteOffset;
        this.byteLimit = byteOffset + numVertices * vertexAttributes.getVertexByteSize();
        this.vertexAttributes = vertexAttributes;
        this.vertexBuffer = vertexBuffer;
    }

    public final void bindToProgram(Program program)
    {
        vertexBuffer.bindToProgram(program, vertexAttributes, byteOffset);
    }

    public final ByteBuffer edit()
    {
        return vertexBuffer.editBuffer(byteOffset, byteLimit);
    }

}
