package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

import android.opengl.GLES20;

import java.nio.ByteBuffer;

/**
 * A VertexBuffer which holds vertex attribute data and whose contents will be regularly changed.
 * <p/>
 * Usage should look like:
 * <p>
 * Setup:
 * <p>
 * buffer.allocateSoftwareBuffer(size)
 * <p/>
 * Per frame (or update period):
 * <p/>
 * buffer.put*
 * <p/>
 * ...
 * <p/>
 * buffer.transfer();
 * <p/>
 * buffer.bind();
 * Created by Steve on 6/14/2017.
 */

public class DynamicAttributeBuffer extends AttributeVertexBuffer
{
    @Override
    protected void bind(int glHandle)
    {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, glHandle);
    }

    @Override
    protected void transferData(int bytesToTransfer, ByteBuffer byteBuffer)
    {
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bytesToTransfer, byteBuffer, GLES20.GL_DYNAMIC_DRAW);
    }
}
