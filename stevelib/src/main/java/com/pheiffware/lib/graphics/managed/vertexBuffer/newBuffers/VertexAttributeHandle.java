package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

import com.pheiffware.lib.graphics.managed.program.VertexAttributes;

/**
 * Created by Steve on 6/14/2017.
 */

public class VertexAttributeHandle
{
    //Byte location in the vertex buffer where packed data associated with this handle starts
    int byteOffset;

    //The upper limit of the data (used to set buffer limit when editting)
    int byteLimit;

    //The vertex attributes associated with this handle
    VertexAttributes vertexAttributes;

    void setup(int byteOffset, int numVertices, VertexAttributes vertexAttributes)
    {
        this.byteOffset = byteOffset;
        this.byteLimit = byteOffset + numVertices * vertexAttributes.getVertexByteSize();
        this.vertexAttributes = vertexAttributes;
    }
}
