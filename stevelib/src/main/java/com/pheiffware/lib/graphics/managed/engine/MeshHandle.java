package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.vertexBuffer.VertexAttributeHandle;
import com.pheiffware.lib.graphics.managed.vertexBuffer.VertexIndexHandle;

import java.nio.ByteBuffer;
import java.util.EnumMap;

/**
 * Created by Steve on 6/14/2017.
 */

public class MeshHandle
{
    private final VertexIndexHandle iHandle;
    private final VertexAttributeHandle sHandle;
    private final VertexAttributeHandle dHandle;
    private final Technique technique;
    private final EnumMap<RenderProperty, Object> renderProperties;

    public MeshHandle(VertexIndexHandle iHandle, VertexAttributeHandle sHandle, VertexAttributeHandle dHandle, Technique technique, RenderPropertyValue[] renderProperties)
    {
        this.iHandle = iHandle;
        this.sHandle = sHandle;
        this.dHandle = dHandle;
        this.technique = technique;
        this.renderProperties = new EnumMap<>(RenderProperty.class);
        for (RenderPropertyValue renderPropertyValue : renderProperties)
        {
            this.renderProperties.put(renderPropertyValue.property, renderPropertyValue.value);
        }
    }

    public MeshHandle(VertexIndexHandle iHandle, VertexAttributeHandle sHandle, VertexAttributeHandle dHandle, Technique technique, EnumMap<RenderProperty, Object> renderProperties)
    {
        this.iHandle = iHandle;
        this.sHandle = sHandle;
        this.dHandle = dHandle;
        this.technique = technique;
        this.renderProperties = new EnumMap<>(renderProperties);
    }

    public final void drawTriangles()
    {
        drawTriangles(technique);
    }

    public final void drawTriangles(Technique technique)
    {
        technique.bind();
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

    public final void drawTriangles(RenderPropertyValue[] renderProperties)
    {
        drawTriangles(technique, renderProperties);
    }

    public final void drawTriangles(Technique technique, RenderPropertyValue[] renderProperties)
    {
        technique.bind();
        technique.setProperties(this.renderProperties);
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

    public void setProperty(RenderProperty renderProperty, Object value)
    {
        renderProperties.put(renderProperty, value);
    }

    public MeshHandle copy()
    {
        return new MeshHandle(iHandle, sHandle, dHandle, technique, renderProperties);
    }
}
