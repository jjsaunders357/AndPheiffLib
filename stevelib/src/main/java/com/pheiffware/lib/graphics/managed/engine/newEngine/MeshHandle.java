package com.pheiffware.lib.graphics.managed.engine.newEngine;

import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.VertexAttributeHandle;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.VertexIndexHandle;

import java.nio.ByteBuffer;
import java.util.EnumMap;

/**
 * Created by Steve on 6/14/2017.
 */

public class MeshHandle
{
    final VertexIndexHandle iHandle;
    final VertexAttributeHandle sHandle;
    final VertexAttributeHandle dHandle;
    final Technique technique;
    final EnumMap<RenderProperty, Object> renderProperties = new EnumMap(RenderProperty.class);

    public MeshHandle(VertexIndexHandle iHandle, VertexAttributeHandle sHandle, VertexAttributeHandle dHandle, Technique technique, RenderPropertyValue[] renderProperties)
    {
        this.iHandle = iHandle;
        this.sHandle = sHandle;
        this.dHandle = dHandle;
        this.technique = technique;
        for (RenderPropertyValue renderPropertyValue : renderProperties)
        {
            this.renderProperties.put(renderPropertyValue.property, renderPropertyValue.value);
        }
    }

    public final void drawTriangles()
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

    public final void drawTriangles(RenderPropertyValue[] overrideRenderProperties)
    {
        drawTriangles(technique, overrideRenderProperties);
    }

    public final void drawTriangles(Technique overrideTechnique, RenderPropertyValue[] overrideRenderProperties)
    {
        overrideTechnique.bind();
        overrideTechnique.setProperties(this.renderProperties);
        overrideTechnique.setProperties(overrideRenderProperties);
        overrideTechnique.applyProperties();
        if (sHandle != null)
        {
            overrideTechnique.bindToVertexBuffer(sHandle);
        }
        if (dHandle != null)
        {
            overrideTechnique.bindToVertexBuffer(dHandle);
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
}
