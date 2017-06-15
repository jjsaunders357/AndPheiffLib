package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

/**
 * Created by Steve on 6/14/2017.
 */

public class VertexIndexHandle
{
    int numVertices;
    int byteOffset;

    public void setup(int numVertices, int byteOffset)
    {
        this.numVertices = numVertices;
        this.byteOffset = byteOffset;
    }
}
