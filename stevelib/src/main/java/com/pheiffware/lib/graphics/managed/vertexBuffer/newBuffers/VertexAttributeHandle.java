package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

import com.pheiffware.lib.graphics.managed.program.VertexAttributes;

/**
 * Created by Steve on 6/14/2017.
 */

public class VertexAttributeHandle
{
    //The vertex attributes associated with this handle
    VertexAttributes vertexAttributes;
    //Byte location in the vertex buffer where packed data associated with this handle starts
    int byteOffset;

    void setup(int byteOffset, VertexAttributes vertexAttributes)
    {
        this.vertexAttributes = vertexAttributes;
        this.byteOffset = byteOffset;
    }
}
