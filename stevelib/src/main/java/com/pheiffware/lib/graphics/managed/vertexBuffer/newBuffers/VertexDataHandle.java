package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

/**
 * Created by Steve on 6/14/2017.
 */

public class VertexDataHandle
{
    final VertexAttributeHandle dHandle;
    final VertexAttributeHandle sHandle;
    final VertexIndexHandle iHandle;

    VertexDataHandle(VertexAttributeHandle dHandle, VertexAttributeHandle sHandle, VertexIndexHandle iHandle)
    {
        this.dHandle = dHandle;
        this.sHandle = sHandle;
        this.iHandle = iHandle;
    }
}
