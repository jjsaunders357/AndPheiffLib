/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.graphics.buffer;

/**
 * A combination of a packed buffer vertex and one or more single attribute vertex buffers. All static attributes should be put in the packed buffer
 * while the dynamic ones are put in the dynamic buffers.
 */
// TODO: Add bulk copy operations. Note that when putting into a wrapping float buffer, this must manually advance the backing byte buffer.
public class CombinedVertexBuffer
{
	private StaticVertexBuffer staticVertexBuffer;
	private DynamicVertexBuffer[] dynamicVertexBuffers;

	public CombinedVertexBuffer(int programHandle, int maxVertices, String[] staticAttributes, int[] staticDims, int[] staticTypes,
			String[] dynamicAttributes, int[] dynamicDims, int[] dynamicTypes)
	{
		if (staticAttributes.length != staticDims.length || staticDims.length != staticTypes.length)
		{
			throw new RuntimeException("Size mismatch in buffer attribute arrays");
		}

		if (dynamicAttributes.length != dynamicDims.length || dynamicDims.length != dynamicTypes.length)
		{
			throw new RuntimeException("Size mismatch in buffer attribute arrays");
		}

		staticVertexBuffer = new StaticVertexBuffer(programHandle, maxVertices, staticAttributes, staticDims, staticTypes);
		dynamicVertexBuffers = new DynamicVertexBuffer[dynamicAttributes.length];
		for (int i = 0; i < dynamicAttributes.length; i++)
		{
			dynamicVertexBuffers[i] = new DynamicVertexBuffer(programHandle, maxVertices, dynamicAttributes[i], dynamicDims[i], dynamicTypes[i]);
		}
	}

	public final void bind()
	{
		staticVertexBuffer.bind();
		for (int i = 0; i < dynamicVertexBuffers.length; i++)
		{
			dynamicVertexBuffers[i].bind();
		}
	}

	public final void transferStatic()
	{
		staticVertexBuffer.transfer();
	}

	public final void transferDynamic()
	{
		for (int i = 0; i < dynamicVertexBuffers.length; i++)
		{
			dynamicVertexBuffers[i].transfer();
		}
	}

	public final void putStaticByte(byte value)
	{
		staticVertexBuffer.putByte(value);
	}

	public final void putStaticFloat(float value)
	{
		staticVertexBuffer.putFloat(value);
	}

	public void putStaticVec2(int x, int y)
	{
		staticVertexBuffer.putVec2(x, y);
	}

	public final void putStaticVec4(float x, float y, float z, float w)
	{
		staticVertexBuffer.putVec4(x, y, z, w);
	}

	public final void putDynamicByte(int bufferIndex, byte value)
	{
		dynamicVertexBuffers[bufferIndex].putByte(value);
	}

	public final void putFloatDynamic(int bufferIndex, float value)
	{
		dynamicVertexBuffers[bufferIndex].putFloat(value);
	}

	public void putDynamicVec2(int bufferIndex, int x, int y)
	{
		dynamicVertexBuffers[bufferIndex].putVec2(x, y);
	}

	public final void putDynamicVec4(int bufferIndex, float x, float y, float z, float w)
	{
		dynamicVertexBuffers[bufferIndex].putVec4(x, y, z, w);
	}

}
