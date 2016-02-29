/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.graphics.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.managed.Attribute;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.utils.Utils;

/**
 * Sets up a packed vertex buffer designed to be filled ONCE and then displayed over and over with a given program.
 * <p/>
 * This does not have to include all attributes of the given program as some attributes may dynamically change and be handled in dynamic buffers.
 * <p/>
 * Usage should look like:
 * <p/>
 * One time setup:
 * <p/>
 * buffer.put*
 * <p/>
 * ...
 * <p/>
 * buffer.transfer(gl);
 * <p/>
 * Per frame (or update period)
 * <p/>
 * buffer.bind(gl);
 * <p/>
 * YOU CANNOT put more data in once transfer is called!
 */
public class StaticVertexBuffer
{
    //Program object this buffer was setup for
    private final Program program;

    //GL handle to the buffer object
    private final int bufferHandle;

    //Java ByteBuffer used to fill static buffer.
    private final ByteBuffer byteBuffer;

    //Total size of each vertex in this buffer
    private int vertexByteSize;

    //Byte offset, within each vertex of each attribute
    private Map<String, Integer> attributeByteOffsets;

    //The names of the attributes being managed by this buffer
    private String[] attributeNames;

    public StaticVertexBuffer(Program program, int maxVertices, String[] attributeNames)
    {
        this.attributeNames = attributeNames;
        this.program = program;
        attributeByteOffsets = new HashMap<>(attributeNames.length * 2);

        int attributeByteOffset = 0;
        for (String attributeName : attributeNames)
        {
            attributeByteOffsets.put(attributeName, attributeByteOffset);
            attributeByteOffset += program.getAttribute(attributeName).byteSize;
        }
        vertexByteSize = attributeByteOffset;

        byteBuffer = ByteBuffer.allocateDirect(maxVertices * vertexByteSize);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] buffer = new int[1];
        GLES20.glGenBuffers(1, buffer, 0);
        bufferHandle = buffer[0];
    }

    public final void putByte(byte b)
    {
        byteBuffer.put(b);
    }

    public final void putFloat(float value)
    {
        byteBuffer.putFloat(value);
    }

    public void putVec2(int x, int y)
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

    /**
     * For a given attributeIndex (defined by order in constructor) put an array of floats in the appropriate buffer location.
     * Note, this is very inefficient, but is fine for one time setup.
     *
     * @param attributeName
     * @param values
     */
    public final void putAttributeFloats(String attributeName, float[] values)
    {
        putAttributeFloats(attributeByteOffsets.get(attributeName), program.getAttribute(attributeName).dims, values, 0, values.length);
    }

    public final void putAttributeFloats(int attributeByteOffset, int attributeDims, float[] values, int floatOffset, int numFloats)
    {
        int putPosition = attributeByteOffset;
        int dims = attributeDims;
        int end = floatOffset + numFloats;
        for (int i = floatOffset; i < end; i += dims)
        {
            byteBuffer.position(putPosition);
            for (int j = 0; j < dims; j++)
            {
                byteBuffer.putFloat(values[i + j]);
            }
            putPosition += vertexByteSize;
        }
    }

    /**
     * Binds this buffer with all specified attributes, such that it will work with the given program.
     */
    public final void bind()
    {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferHandle);
        for (String attributeName : attributeNames)
        {
            Attribute attribute = program.getAttribute(attributeName);
            GLES20.glEnableVertexAttribArray(attribute.location);
            GLES20.glVertexAttribPointer(attribute.location, attribute.dims, attribute.baseType, false, vertexByteSize, attributeByteOffsets.get(attributeName));
        }
    }

    /**
     * Transfer contents loaded by putAttribute* calls into graphics library.
     * CAN ONLY BE CALLED ONCE!  After this method is called, no more put/transfer operations should occur.
     */
    public void transfer()
    {
        // Bind to the buffer. Future commands will affect this buffer specifically.
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferHandle);

        // Transfer data from client memory to the buffer.
        int transferSize = byteBuffer.position();

        // MUST RESET POSITION TO 0!
        byteBuffer.position(0);

        // Transfer data
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, transferSize, byteBuffer, GLES20.GL_STATIC_DRAW);

        // IMPORTANT: Unbind from the buffer when we're done with it.
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        // Destroy bytebuffer (immediately)
        Utils.deallocateDirectByteBuffer(byteBuffer);
    }

    /**
     * Destroys this buffer resource with openGL
     */
    public void release()
    {
        GLES20.glDeleteBuffers(1, new int[]{bufferHandle}, 0);
    }

    /**
     * @return GL handle of buffer object.
     */
    public int getHandle()
    {
        return bufferHandle;
    }

}
