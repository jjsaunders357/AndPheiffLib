/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.graphics.managed.buffer;

import android.opengl.GLES20;

import com.pheiffware.lib.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * Simple element buffer for triangles or other primitives.  Holds index references to other buffers.
 */
public class IndexBuffer
{
    private final int bufferHandle;
    private final ByteBuffer byteBuffer;
    private final ShortBuffer shortBuffer;
    private final boolean dynamic;
    private int numVerticesTransfered;

    public IndexBuffer(int maxVertices, boolean dynamic)
    {
        this.dynamic = dynamic;
        byteBuffer = ByteBuffer.allocateDirect(maxVertices * 2);
        byteBuffer.order(ByteOrder.nativeOrder());
        shortBuffer = byteBuffer.asShortBuffer();
        int[] buffer = new int[1];
        GLES20.glGenBuffers(1, buffer, 0);
        bufferHandle = buffer[0];
    }

    public final void putIndex(short index)
    {
        byteBuffer.putShort(index);
    }

    public final void putIndex(int index)
    {
        byteBuffer.putShort((short) index);
    }

    /**
     * Overwrites indices in the index buffer with the given indices.
     *
     * @param primitiveIndices
     */
    public void putIndices(short[] primitiveIndices)
    {
        putIndices(primitiveIndices, 0);
    }

    public void putIndices(short[] primitiveIndices, int writeOffset)
    {
        shortBuffer.position(writeOffset);
        shortBuffer.put(primitiveIndices);
        // Must manually move the byteBuffer to the correct location
        byteBuffer.position(shortBuffer.position() * 2);
    }

    /**
     * Write primitive indices to the index buffer, but apply an offset to each index.  This allows you to reference geometry inserted into a vertex buffer at a starting position
     * other than 0.
     *
     * @param primitiveIndices base, 0 indexed, indices
     * @param writeOffset      the location in this buffer to write data
     * @param perIndexOffset   an offset to apply to each index written
     */
    public void putIndicesWithOffset(short[] primitiveIndices, int writeOffset, short perIndexOffset)
    {
        shortBuffer.position(writeOffset);
        for (short primitiveIndex : primitiveIndices)
        {
            shortBuffer.put((short) (primitiveIndex + perIndexOffset));
        }

        // Must manually move the byteBuffer to the correct location
        byteBuffer.position(shortBuffer.position() * 2);
    }


    public final void drawAll(int GLPrimitiveType)
    {
        draw(GLPrimitiveType, 0, numVerticesTransfered);
    }

    public final void draw(int GLPrimitiveType, int offset, int numVertices)
    {
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferHandle);
        GLES20.glDrawElements(GLPrimitiveType, numVertices, GLES20.GL_UNSIGNED_SHORT, offset * 2);
    }

    public final void drawTriangles(int offset, int numVertices)
    {
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numVertices, GLES20.GL_UNSIGNED_SHORT, offset * 2);
    }

    /**
     * Transfer contents loaded by putAttribute* calls into graphics library. Also frees client side memory after transfer (using low-level buffer hack).
     */
    public void transfer()
    {
        // Bind to the buffer. Future commands will affect this buffer specifically.
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferHandle);

        // Transfer data from client memory to the buffer.
        int transferSize = byteBuffer.position();

        // Reset position to 0 for this transfer and future puts
        byteBuffer.position(0);

        // Transfer data
        if (dynamic)
        {
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, transferSize, byteBuffer, GLES20.GL_DYNAMIC_DRAW);
        }
        else
        {
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, transferSize, byteBuffer, GLES20.GL_STATIC_DRAW);
        }
        // IMPORTANT: Unbind from the buffer when we're done with it.
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        if (!dynamic)
        {
            // Destroy bytebuffer (immediately)
            Utils.deallocateDirectByteBuffer(byteBuffer);
        }
        // Record the number of vertices in the buffer
        numVerticesTransfered = transferSize / 2;
    }

    public void deallocate()
    {
        GLES20.glDeleteBuffers(1, new int[]{bufferHandle}, 0);
        if (dynamic)
        {
            // Destroy bytebuffer (immediately)
            Utils.deallocateDirectByteBuffer(byteBuffer);
        }
    }

}
