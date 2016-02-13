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
 * Sets up a packed vertex buffer designed to be filled ONCE and then displayed over and over.
 * 
 * This does not have to include all attributes of a given program as others can be handled by dynamic buffers OR by constant values.
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
 */
public class StaticVertexBuffer
{
	private final int bufferHandle;
	private final ByteBuffer byteBuffer;

	private int[] attributeIndices;
	private int[] attributeDims;
	private int[] attributeTypes;
	private int[] attributeByteOffsets;
	private int vertexByteSize;

	public StaticVertexBuffer(int programHandle, int maxVertices, String[] attributes, int[] dims, int[] types)
	{
		attributeIndices = new int[dims.length];
		attributeDims = new int[dims.length];
		attributeTypes = new int[dims.length];
		attributeByteOffsets = new int[dims.length];

		int attributeByteOffset = 0;
		for (int i = 0; i < dims.length; i++)
		{
			attributeIndices[i] = GLES20.glGetAttribLocation(programHandle, attributes[i]);
			attributeDims[i] = dims[i];
			attributeTypes[i] = types[i];
			attributeByteOffsets[i] = attributeByteOffset;
			int attributeByteSize = dims[i] * PheiffGLUtils.GLTypeToSize(types[i]);
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

	public final void bind()
	{
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferHandle);
		for (int i = 0; i < attributeDims.length; i++)
		{
			GLES20.glEnableVertexAttribArray(attributeIndices[i]);
			GLES20.glVertexAttribPointer(attributeIndices[i], attributeDims[i], attributeTypes[i], false, vertexByteSize, attributeByteOffsets[i]);
		}
	}

	/**
	 * Transfer contents loaded by putAttribute* calls into graphics library. Also frees client side memory after transfer if possible.
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

	public void release()
	{
		GLES20.glDeleteBuffers(1, new int[] { bufferHandle }, 0);
	}

	public int getHandle()
	{
		return bufferHandle;
	}

}
