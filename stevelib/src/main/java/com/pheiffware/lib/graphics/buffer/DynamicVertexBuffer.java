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
 * Sets up a vertex buffer for holding an array of a single attribute. This is generally more efficient for attributes which will change regularly as
 * they can be quickly copied into buffers and re-transfered.
 * 
 * Usage should look like:
 * 
 * Per frame (or update period)
 * 
 * buffer.put*
 * 
 * ...
 * 
 * buffer.transfer(gl);
 * 
 * buffer.bind(gl);
 * 
 */
public class DynamicVertexBuffer
{
	private final int bufferHandle;
	private final ByteBuffer byteBuffer;

	private int attributeIndex;
	private int dim;
	private int type;
	private int vertexByteSize;

	public DynamicVertexBuffer(int programHandle, int maxVertices, String attribute, int dim, int type)
	{
		attributeIndex = GLES20.glGetAttribLocation(programHandle, attribute);
		this.dim = dim;
		this.type = type;
		vertexByteSize = dim * PheiffGLUtils.GLTypeToSize(type);

		byteBuffer = ByteBuffer.allocateDirect(maxVertices * vertexByteSize);
		byteBuffer.order(ByteOrder.nativeOrder());

		int[] buffer = new int[1];
		GLES20.glGenBuffers(1, buffer, 0);
		bufferHandle = buffer[0];

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferHandle);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, 96, null, GLES20.GL_DYNAMIC_DRAW);
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

	public final void bind()
	{
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferHandle);
		GLES20.glEnableVertexAttribArray(attributeIndex);
		GLES20.glVertexAttribPointer(attributeIndex, dim, type, false, vertexByteSize, 0);
	}

	/**
	 * Transfer contents loaded by putAttribute* calls into graphics library. Also frees client side memory after transfer (using low-level buffer
	 * hack).
	 * 
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
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, transferSize, byteBuffer, GLES20.GL_DYNAMIC_DRAW);

		// TODO: Remove in final version (just let state linger)
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}

	public void release()
	{
		GLES20.glDeleteBuffers(1, new int[] { bufferHandle }, 0);
		// Destroy bytebuffer (immediately)
		Utils.deallocateDirectByteBuffer(byteBuffer);
	}

	public int getHandle()
	{
		return bufferHandle;
	}

}
