package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

import com.pheiffware.lib.graphics.managed.program.VertexAttributes;

/**
 * Created by Steve on 6/14/2017.
 */

public class VertexAttributeHandle
{
    VertexAttributes vertexAttributes;
    int byteOffset;

    void setup(int vertexOffset, VertexAttributes vertexAttributes)
    {
        this.vertexAttributes = vertexAttributes;
        this.byteOffset = vertexOffset * vertexAttributes.getVertexByteSize();
    }
}
