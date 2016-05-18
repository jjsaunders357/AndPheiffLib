/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.graphics.managed.buffer;

import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;

/**
 * A combination of a packed vertex buffer and one or more single attribute vertex buffers. All static attributes should be put in the packed buffer while the dynamic ones are put
 * in the dynamic buffers.
 */
public class CombinedVertexBuffer
{
    private StaticVertexBuffer staticVertexBuffer;
    private DynamicVertexBuffer[] dynamicVertexBuffers;

    public CombinedVertexBuffer(VertexAttribute[] staticVertexAttributes, VertexAttribute[] dynamicVertexAttributes)
    {
        staticVertexBuffer = new StaticVertexBuffer(staticVertexAttributes);
        dynamicVertexBuffers = new DynamicVertexBuffer[dynamicVertexAttributes.length];
        for (int i = 0; i < dynamicVertexAttributes.length; i++)
        {
            dynamicVertexBuffers[i] = new DynamicVertexBuffer(dynamicVertexAttributes[i]);
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

    public final void bind(Program program)
    {
        staticVertexBuffer.bind(program);
        for (int i = 0; i < dynamicVertexBuffers.length; i++)
        {
            dynamicVertexBuffers[i].bind(program);
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
