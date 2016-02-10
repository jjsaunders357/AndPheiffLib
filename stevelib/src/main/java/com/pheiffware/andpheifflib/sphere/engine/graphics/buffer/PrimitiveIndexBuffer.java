/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.graphics.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

import com.pheiffware.andpheifflib.sphere.Utils;

/**
 * Simple element buffer for triangles.
 */
public class PrimitiveIndexBuffer
{
	private final int bufferHandle;
	private final ByteBuffer byteBuffer;
	private final ShortBuffer shortBuffer;
	private int numVerticesTransfered;

	public PrimitiveIndexBuffer(int maxVertices)
	{
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
		putIndices(primitiveIndices, 0, primitiveIndices.length);
	}

	public void putIndices(short[] primitiveIndices, int offset, int length)
	{
		shortBuffer.position(0);
		shortBuffer.put(primitiveIndices, offset, length);
		// Must manually move the byteBuffer to the correct location
		byteBuffer.position(length * 2);
	}

	public final void drawAll(int GLPrimitiveType)
	{
		draw(numVerticesTransfered, GLPrimitiveType);
	}

	public final void draw(int numVertices, int GLPrimitiveType)
	{
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferHandle);
		GLES20.glDrawElements(GLPrimitiveType, numVertices, GLES20.GL_UNSIGNED_SHORT, 0);
	}

	/**
	 * Transfer contents loaded by putAttribute* calls into graphics library. Also frees client side memory after transfer (using low-level buffer
	 * hack).
	 * 
	 */
	public void transfer()
	{
		// Bind to the buffer. Future commands will affect this buffer specifically.
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferHandle);

		// Transfer data from client memory to the buffer.
		int transferSize = byteBuffer.position();

		// MUST RESET POSITION TO 0!
		byteBuffer.position(0);

		// Transfer data
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, transferSize, byteBuffer, GLES20.GL_STATIC_DRAW);

		// IMPORTANT: Unbind from the buffer when we're done with it.
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

		// Destroy bytebuffer (immediately)
		Utils.deallocateDirectByteBuffer(byteBuffer);

		// Record the number of vertices in the buffer
		numVerticesTransfered = transferSize / 2;
	}

	public void release()
	{
		GLES20.glDeleteBuffers(1, new int[] { bufferHandle }, 0);
	}

}
