/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.graphics.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.utils.PheiffGLUtils;
import com.pheiffware.lib.Utils;

/**
 * Sets up a packed vertex buffer designed to be filled ONCE and then displayed over and over with a given program.
 *
 * This does not have to include all attributes of the given program as some attributes may dynamically change and be handled in dynamic buffers.
 *
 * Usage should look like:
 * 
 * One time setup:
 * 
 * buffer.put*
 * 
 * ...
 * 
 * buffer.transfer(gl);
 * 
 * Per frame (or update period)
 * 
 * buffer.bind(gl);
 *
 * YOU CANNOT put more data in once transfer is called!
 */
public class StaticVertexBuffer
{
    //GL handle to the buffer object
    private final int bufferHandle;

    //Java ByteBuffer used to fill static buffer.
    private final ByteBuffer byteBuffer;

    private int[] attributePositions;
    private int[] attributeDims;
	private int[] attributeTypes;
	private int[] attributeByteOffsets;
	private int vertexByteSize;

	public StaticVertexBuffer(int programHandle, int maxVertices, String[] attributes, int[] dims, int[] types)
	{
        attributePositions = new int[dims.length];
        attributeDims = new int[dims.length];
		attributeTypes = new int[dims.length];
		attributeByteOffsets = new int[dims.length];

		int attributeByteOffset = 0;
		for (int i = 0; i < dims.length; i++)
		{
            attributePositions[i] = GLES20.glGetAttribLocation(programHandle, attributes[i]);
            attributeDims[i] = dims[i];
			attributeTypes[i] = types[i];
			attributeByteOffsets[i] = attributeByteOffset;
            int attributeByteSize = dims[i] * PheiffGLUtils.getGLTypeSize(types[i]);
            attributeByteOffset += attributeByteSize;
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
	 * @param attributeIndex
	 * @param values
	 */
	public final void putFloats(int attributeIndex, float[] values)
	{
		putFloats(attributeIndex, values, 0, values.length);
	}

	public final void putFloats(int attributeIndex, float[] values, int offset, int length)
	{
		int putPosition = attributeByteOffsets[attributeIndex];
		int dims = attributeDims[attributeIndex];
		int end = offset + length;
		for (int i = offset; i < end; i += dims)
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
        System.out.println("bind:\n");
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferHandle);
		for (int i = 0; i < attributeDims.length; i++)
		{
            GLES20.glEnableVertexAttribArray(attributePositions[i]);
            GLES20.glVertexAttribPointer(attributePositions[i], attributeDims[i], attributeTypes[i], false, vertexByteSize, attributeByteOffsets[i]);
            System.out.println("dim:" + attributeDims[i]);
            System.out.println("type:" + attributeTypes[i]);
            System.out.println("offset:" + attributeByteOffsets[i]);
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
		GLES20.glDeleteBuffers(1, new int[] { bufferHandle }, 0);
	}

    /**
     * @return GL handle of buffer object.
     */
    public int getHandle()
	{
		return bufferHandle;
	}

}
