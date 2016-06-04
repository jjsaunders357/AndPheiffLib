package com.pheiffware.lib.graphics.managed.vertexBuffer;

import android.opengl.GLES20;

import com.pheiffware.lib.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Steve on 3/2/2016.
 */
public abstract class BaseBuffer
{
    //Handle to the gl buffer object
    protected final int bufferHandle;

    //The byte buffer which will be used to transfer data to the gl buffer.  This must be allocated by calling allocateBuffer() by the extending class
    protected ByteBuffer byteBuffer = null;

    public BaseBuffer()
    {
        int[] buffer = new int[1];
        GLES20.glGenBuffers(1, buffer, 0);
        bufferHandle = buffer[0];
    }

    /**
     * Allocates the byte buffer and sets the byte order correct.  This is done natively for efficient transfers.
     *
     * @param byteSize
     */
    protected final void allocateBuffer(int byteSize)
    {
        byteBuffer = ByteBuffer.allocateDirect(byteSize);
        byteBuffer.order(ByteOrder.nativeOrder());
    }

    /**
     * Make sure the byte buffer is deallocated if it hasn't been already.
     */
    protected void deallocateByteBuffer()
    {
        if (byteBuffer != null)
        {
            Utils.deallocateDirectByteBuffer(byteBuffer);
            byteBuffer = null;
        }
    }

    /**
     * Destroys this buffer resource with openGL
     */
    public void release()
    {
        GLES20.glDeleteBuffers(1, new int[]{bufferHandle}, 0);
        deallocateByteBuffer();
    }

    public final void putByte(byte b)
    {
        byteBuffer.put(b);
    }

    public final void putFloat(float value)
    {
        byteBuffer.putFloat(value);
    }

    public void putVec2(float x, float y)
    {
        byteBuffer.putFloat(x);
        byteBuffer.putFloat(y);
    }

    public final void putVec4(float x, float y, float z, float w)
    {
        byteBuffer.putFloat(x);
        byteBuffer.putFloat(y);
        byteBuffer.putFloat(z);
        byteBuffer.putFloat(w);
    }

    public final void putFloats(float[] floats)
    {
        for (int i = 0; i < floats.length; i++)
        {
            byteBuffer.putFloat(floats[i]);
        }
    }
}
