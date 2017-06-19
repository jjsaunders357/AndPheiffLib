package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

import android.opengl.GLES20;

import java.nio.ByteBuffer;

/**
 * A VertexBuffer which holds index data and whose contents will be set once and never changed.
 * <p/>
 * Usage should look like:
 * <p/>
 * Setup:
 * <p/>
 * buffer.allocateSoftwareBuffer(size)
 * <p>
 * buffer.put*
 * <p/>
 * ...
 * <p/>
 * buffer.transfer();
 * <p/>
 * Per frame (or update period):
 * <p/>
 * buffer.bind();
 * <p/>
 * YOU CANNOT put more data in once transfer is called!
 * Created by Steve on 6/14/2017.
 */

public class IndexBuffer extends VertexBuffer
{
    private boolean isTransferred;

    @Override
    public void transfer()
    {
        if (isTransferred)
        {
            throw new RuntimeException("Static buffer already transferred");
        }
        super.transfer();
    }

    /**
     * Draws triangles, referenced by the given handle, using the currently bound vertex attributes/program.
     *
     * @param handle handle to the index data
     */
    public final void drawTriangles(VertexIndexHandle handle)
    {
        draw(GLES20.GL_TRIANGLES, handle);
    }

    /**
     * Draws the given primitive type , referenced by the given handle, using the currently bound vertex attributes/program.
     *
     * @param primitiveType the type of primite (example: GL_TRIANGLES)
     * @param handle        handle to the index data
     */
    public final void draw(int primitiveType, VertexIndexHandle handle)
    {
        draw(primitiveType, handle.numVertices, handle.byteOffset);
    }

    /**
     * Draws the given primitive type using the currently bound vertex attributes/program.
     *
     * @param primitiveType the type of primite (example: GL_TRIANGLES)
     * @param numVertices   the number of vertices to render
     * @param byteOffset    offset to where the data begins in the buffer
     */
    public final void draw(int primitiveType, int numVertices, int byteOffset)
    {
        bind();
        GLES20.glDrawElements(primitiveType, numVertices, GLES20.GL_UNSIGNED_SHORT, byteOffset);
    }

    @Override
    protected void bind(int glHandle)
    {
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, glHandle);
    }

    @Override
    protected void transferData(int bytesToTransfer, ByteBuffer byteBuffer)
    {
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, bytesToTransfer, byteBuffer, GLES20.GL_STATIC_DRAW);
        deallocateSoftwareBuffer();
        isTransferred = true;
    }
}
