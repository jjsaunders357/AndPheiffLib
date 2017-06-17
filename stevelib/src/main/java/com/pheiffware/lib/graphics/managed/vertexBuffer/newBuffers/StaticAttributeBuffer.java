package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

import android.opengl.GLES20;

import java.nio.ByteBuffer;

/**
 * A VertexBuffer which holds vertex attribute data and whose contents will be set once and never changed.
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
 * buffer.transfer(gl);
 * <p/>
 * Per frame (or update period):
 * <p/>
 * buffer.bind(gl);
 * <p/>
 * YOU CANNOT put more data in once transfer is called!
 * Created by Steve on 6/14/2017.
 */

public class StaticAttributeBuffer extends AttributeVertexBuffer
{
    private boolean isTransferred = false;

    @Override
    protected void bind(int glHandle)
    {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, glHandle);
    }

    @Override
    protected void transfer()
    {
        if (isTransferred)
        {
            throw new RuntimeException("Static buffer already transferred");
        }
        super.transfer();
    }

    @Override
    protected void transferData(int bytesToTransfer, ByteBuffer byteBuffer)
    {
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bytesToTransfer, byteBuffer, GLES20.GL_STATIC_DRAW);
        deallocateSoftwareBuffer();
        isTransferred = true;
    }

    protected ByteBuffer editBuffer(int byteOffset)
    {
        throw new UnsupportedOperationException("Illegal to edit a static buffer!");
    }
}
