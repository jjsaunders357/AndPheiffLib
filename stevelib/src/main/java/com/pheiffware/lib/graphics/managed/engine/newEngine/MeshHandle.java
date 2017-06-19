package com.pheiffware.lib.graphics.managed.engine.newEngine;

import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.VertexAttributeHandle;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.VertexIndexHandle;

import java.nio.ByteBuffer;

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

    public final void drawTriangles()
    {
        drawTriangles(technique);
    }

    public final void drawTriangles(RenderPropertyValue[] renderProperties)
    {
        drawTriangles(technique, renderProperties);
    }

    public final void drawTriangles(Technique technique)
    {
        technique.setProperties(renderProperties);
        technique.applyProperties();

        if (sHandle != null)
        {
            technique.bindToVertexBuffer(sHandle);
        }
        if (dHandle != null)
        {
            technique.bindToVertexBuffer(dHandle);
        }
        iHandle.drawTriangles();
    }

    public final void drawTriangles(Technique technique, RenderPropertyValue[] renderProperties)
    {
        technique.setProperties(renderProperties);
        technique.setProperties(renderProperties);
        technique.applyProperties();
        if (sHandle != null)
        {
            technique.bindToVertexBuffer(sHandle);
        }
        if (dHandle != null)
        {
            technique.bindToVertexBuffer(dHandle);
        }
        iHandle.drawTriangles();
    }

    public ByteBuffer edit()
    {
        return dHandle.edit();
    }
}
