/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.graphics.managed.vertexBuffer;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;

/**
 * Sets up a vertex buffer for holding an array of a single vertexAttribute. This is generally more efficient for attributes which will change regularly as
 * they can be quickly copied into buffers and re-transfered.
 * <p/>
 * Usage should look like:
 * <p/>
 * Per frame (or update period)
 * <p/>
 * buffer.put*
 * <p/>
 * ...
 * <p/>
 * buffer.transfer(gl);
 * <p/>
 * buffer.bind(gl);
 */

public class DynamicVertexBuffer extends BaseBuffer
{
    private final VertexAttribute vertexAttribute;

    public DynamicVertexBuffer(VertexAttribute vertexAttribute)
    {
        super();
        this.vertexAttribute = vertexAttribute;
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferHandle);
    }

    public void allocate(int numVertices)
    {
        allocateBuffer(numVertices * vertexAttribute.getByteSize());
    }

    public final void bind(Program program)
    {
        int location = program.getAttributeLocation(vertexAttribute);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferHandle);
        GLES20.glEnableVertexAttribArray(location);
        GLES20.glVertexAttribPointer(location, vertexAttribute.getNumBaseTypeElements(), vertexAttribute.getBaseType(), false, vertexAttribute.getByteSize(), 0);
    }

    /**
     * Transfer contents loaded by putAttribute* calls into graphics library. Also frees client side memory after transfer (using low-level buffer
     * hack).
     */
    public void transfer()
    {
        // Bind to the buffer. Future commands will affect this buffer specifically.
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferHandle);

        // Transfer data from client memory to the buffer.
        int transferSize = byteBuffer.position();

        // Reset position to 0 for this transfer and future puts
        byteBuffer.position(0);

        // Transfer data
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, transferSize, byteBuffer, GLES20.GL_DYNAMIC_DRAW);
    }

    public void release()
    {
        GLES20.glDeleteBuffers(1, new int[]{bufferHandle}, 0);
        deallocateByteBuffer();
    }
}
