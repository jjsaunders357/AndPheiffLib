package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

import android.opengl.GLES20;

/**
 * Created by Steve on 6/14/2017.
 */

public class VertexIndexHandle
{
    private int numIndices;
    private int byteOffset;
    private IndexBuffer indexBuffer;

    public void setup(int numIndices, int byteOffset, IndexBuffer indexBuffer)
    {
        this.numIndices = numIndices;
        this.byteOffset = byteOffset;
        this.indexBuffer = indexBuffer;
    }


    /**
     * Draws triangles, using the currently bound program.
     */
    public final void drawTriangles()
    {
        draw(GLES20.GL_TRIANGLES);
    }

    /**
     * Draws triangles, using the currently bound program.
     *
     * @param primitiveType the type of primitive (example: GL_TRIANGLES)
     */
    public final void draw(int primitiveType)
    {
        indexBuffer.draw(primitiveType, numIndices, byteOffset);
    }
}
