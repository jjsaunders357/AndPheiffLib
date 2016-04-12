/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.graphics.managed.buffer;

import com.pheiffware.lib.graphics.managed.Program;

/**
 * A combination of a packed vertex buffer and one or more single attribute vertex buffers. All static attributes should be put in the packed buffer while the dynamic ones are put
 * in the dynamic buffers.
 */
public class CombinedVertexBuffer
{
    private StaticVertexBuffer staticVertexBuffer;
    private DynamicVertexBuffer[] dynamicVertexBuffers;

    public CombinedVertexBuffer(Program program, String[] staticAttributeNames, String[] dynamicAttributeNames)
    {
        staticVertexBuffer = new StaticVertexBuffer(program, staticAttributeNames);
        dynamicVertexBuffers = new DynamicVertexBuffer[dynamicAttributeNames.length];
        for (int i = 0; i < dynamicAttributeNames.length; i++)
        {
            dynamicVertexBuffers[i] = new DynamicVertexBuffer(program, dynamicAttributeNames[i]);
        }
    }

    public void allocate(int numVertices)
    {
        staticVertexBuffer.allocate(numVertices);
        for (DynamicVertexBuffer dynamicVertexBuffer : dynamicVertexBuffers)
        {
            dynamicVertexBuffer.allocate(numVertices);
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

    public void putStaticVec2(float x, float y)
    {
        staticVertexBuffer.putVec2(x, y);
    }

    public final void putStaticVec4(float x, float y, float z, float w)
    {
        staticVertexBuffer.putVec4(x, y, z, w);
    }

    public final void putStaticFloats(float[] floats)
    {
        staticVertexBuffer.putFloats(floats);
    }

    public final void putDynamicByte(int bufferIndex, byte value)
    {
        dynamicVertexBuffers[bufferIndex].putByte(value);
    }

    public final void putDynamicFloat(int bufferIndex, float value)
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

    public final void putDynamicFloats(int bufferIndex, float[] floats)
    {
        dynamicVertexBuffers[bufferIndex].putFloats(floats);
    }

}
