package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

/**
 * Created by Steve on 6/14/2017.
 */

public class VertexDataHandle
{
    final VertexIndexHandle iHandle;
    final VertexAttributeHandle sHandle;
    final VertexAttributeHandle dHandle;

    public VertexDataHandle(VertexIndexHandle iHandle, VertexAttributeHandle sHandle, VertexAttributeHandle dHandle)
    {
        this.iHandle = iHandle;
        this.sHandle = sHandle;
        this.dHandle = dHandle;
    }
}
