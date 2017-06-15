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
    final void allocateSoftwareBuffer(int byteSize)
    {
        byteBuffer = ByteBuffer.allocateDirect(byteSize);
        byteBuffer.order(ByteOrder.nativeOrder());
    }

    void pack()
    {
        pack(byteBuffer);
    }


    public void transfer()
    {
        bind();

        // Transfer data from client memory to the buffer.
        int transferSize = byteBuffer.position();

        // Reset position to 0 for this transfer and future puts
        byteBuffer.position(0);

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
     * Make sure the byte buffer is deallocated if it hasn't been already.
     */
    void deallocateSoftwareBuffer()
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
    protected void bind()
    {
        bind(glHandle);
    }

    /**
     * Perform appropriate pack operation as related to meshes.
     *
     * @param byteBuffer buffer to pack data into
     */
    protected abstract void pack(ByteBuffer byteBuffer);

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
