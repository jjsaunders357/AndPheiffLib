package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.techniques.RenderPropertyValue;

/**
 * Holds information about a single mesh which should be rendered with a specific Program and specific values for properties in that Program.  The set of uniform values may be
 * incomplete as some values (such as ViewMatrix) may be specified more globally.
 * <p/>
 * Created by Steve on 4/10/2016.
 */
public class MeshRenderHandle<M>
{
    //The offset in the index buffer to render at
    private final int vertexOffset;
    //The number of vertices to render
    private final int numVertices;
    //The vertex buffer where mesh data is stored
    final StaticVertexBuffer vertexBuffer;
    //GraphicsManager specific material information (such as a technique to render with)
    final M material;

    //Generic property/values to be used when rendering
    final RenderPropertyValue[] renderPropertyValues;

    public MeshRenderHandle(StaticVertexBuffer vertexBuffer, int vertexOffset, int numVertices, M material, RenderPropertyValue[] renderPropertyValues)
    {
        this.vertexBuffer = vertexBuffer;
        this.vertexOffset = vertexOffset;
        this.numVertices = numVertices;
        this.material = material;
        this.renderPropertyValues = renderPropertyValues;
    }

    void drawTriangles(IndexBuffer indexBuffer)
    {
        indexBuffer.drawTriangles(vertexOffset, numVertices);
    }
}
