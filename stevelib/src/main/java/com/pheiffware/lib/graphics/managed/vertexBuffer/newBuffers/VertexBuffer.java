package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

import android.opengl.GLES20;

import com.pheiffware.lib.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Core vertex buffer class which is backed by a ByteBuffer.
 * Created by Steve on 6/14/2017.
 */

public abstract class VertexBuffer
{
    //Handle to the gl buffer object
    private final int glHandle;

    //This is created during the allocation step
    private ByteBuffer byteBuffer = null;

    VertexBuffer()
    {
        int[] buffer = new int[1];
        GLES20.glGenBuffers(1, buffer, 0);
        glHandle = buffer[0];
    }

    /**
     * Allocates the byte buffer and sets the byte order correct.  This is done natively for efficient transfers.
     *
     * @param byteSize
     */
    public final void allocateSoftwareBuffer(int byteSize)
    {
        byteBuffer = ByteBuffer.allocateDirect(byteSize);
        byteBuffer.order(ByteOrder.nativeOrder());
    }

    /**
     * Transfer all data from byteBuffer to openGL.
     */
    public void transfer()
    {
        transfer(byteBuffer.capacity());
    }

    /**
     * Transfer data from byteBuffer to openGL.
     *
     * @param transferSize
     */
    protected void transfer(int transferSize)
    {
        bind();

        // Reset position to 0 for this transfer and future puts
        byteBuffer.position(0);

        //Reset limit to capacity
        byteBuffer.limit(byteBuffer.capacity());

        transferData(transferSize, byteBuffer);

        // IMPORTANT: Unbind from the buffer when we're done with it.
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    /**
     * Destroys this buffer resource with openGL
     */
    public void release()
    {
        GLES20.glDeleteBuffers(1, new int[]{glHandle}, 0);
        deallocateSoftwareBuffer();
    }

    /**
     * Provides access to the entire byte buffer backing this vertex buffer for editing.
     */
    public final ByteBuffer editBuffer()
    {
        return editBuffer(0, byteBuffer.capacity());
    }

    /**
     * Provides access to the byte buffer backing this vertex buffer for editing.
     *
     * @param byteOffset set the buffer to point to given byte offset
     * @param limit      the limit to set on the buffer
     * @return the buffer, set to given position
     */
    public final ByteBuffer editBuffer(int byteOffset, int limit)
    {
        byteBuffer.position(byteOffset);
        byteBuffer.limit(limit);
        return byteBuffer;
    }

    /**
     * Make sure the byte buffer is deallocated if it hasn't been already.
     */
    protected void deallocateSoftwareBuffer()
    {
        if (byteBuffer != null)
        {
            Utils.deallocateDirectByteBuffer(byteBuffer);
            byteBuffer = null;
        }
    }


    /**
     * Perform GL bind operation.
     */
    public void bind()
    {
        bind(glHandle);
    }


    /**
     * Implementing class should perform the appropriate openGL bind operation.
     *
     * @param glHandle openGL buffer handle
     */
    protected abstract void bind(int glHandle);

    /**
     * Implementing class should transfer the specified number of bytes from the buffer's current position, to openGL
     *
     * @param bytesToTransfer the number of bytes to transfer from the buffer's current position
     * @param byteBuffer      buffer to transfer data from
     */
    protected abstract void transferData(int bytesToTransfer, ByteBuffer byteBuffer);

}
