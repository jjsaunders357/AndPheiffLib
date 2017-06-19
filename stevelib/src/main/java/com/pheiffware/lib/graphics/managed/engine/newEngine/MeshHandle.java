package com.pheiffware.lib.graphics.managed.engine.newEngine;

import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.VertexAttributeHandle;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.VertexIndexHandle;

/**
 * Created by Steve on 6/14/2017.
 */

public class MeshHandle
{
    final VertexIndexHandle iHandle;
    final VertexAttributeHandle sHandle;
    final VertexAttributeHandle dHandle;
    final Technique technique;
    final RenderPropertyValue[] renderProperties;

    public MeshHandle(VertexIndexHandle iHandle, VertexAttributeHandle sHandle, VertexAttributeHandle dHandle, Technique technique, RenderPropertyValue[] renderProperties)
    {
        this.iHandle = iHandle;
        this.sHandle = sHandle;
        this.dHandle = dHandle;
        this.technique = technique;
        this.renderProperties = renderProperties;
    }
}
