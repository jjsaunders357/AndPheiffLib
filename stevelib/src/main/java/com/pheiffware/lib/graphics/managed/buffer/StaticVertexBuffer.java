/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.graphics.managed.buffer;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Attribute;
import com.pheiffware.lib.graphics.managed.program.Program;

/**
 * Sets up a packed vertex buffer designed to be filled ONCE and then displayed over and over.
 * <p/>
 * This does not have to include all attributes of a program will use as some attributes may dynamically change and be handled in dynamic buffers.
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
public class StaticVertexBuffer extends BaseBuffer
{
    //TODO: Must be an even multiple of machine word size.  Check OpenGL ES spec.
    //Total size of each vertex in this buffer
    private int vertexByteSize;

    //Maps standard attributes to their corresponding byte offsets within each vertex data block (EnumSets not supported by Android yet).
    private int[] attributeByteOffsets = new int[Attribute.values().length];

    //The attributes being managed by this buffer.  This is the order they will appear within each vertex data block
    private final Attribute[] attributes;

    //Has the buffer been transferred?  Its illegal to transfer multiple times.
    private boolean isTransferred = false;

    /**
     * Create buffer which holds a specific set of standard vertex attributes
     */
    public StaticVertexBuffer(Attribute[] attributes)
    {
        this.attributes = attributes;

        int attributeByteOffset = 0;
        for (Attribute attribute : attributes)
        {
            setAttributeByteOffset(attribute, attributeByteOffset);
            attributeByteOffset += attribute.getByteSize();
        }
        vertexByteSize = attributeByteOffset;
    }

    public void allocate(int numVertices)
    {
        allocateBuffer(numVertices * vertexByteSize);
    }

    /**
     * For a given attribute put an array of floats in the appropriate buffer location, starting at the given vertex offset. Note, this is very inefficient, but is fine for one
     * time setup.
     *
     * @param attribute
     * @param values
     * @param vertexOffset
     */
    public final void putAttributeFloats(Attribute attribute, float[] values, int vertexOffset)
    {
        putAttributeFloats(getAttributeByteOffset(attribute), attribute.getNumBaseTypeElements(), values, vertexOffset);
    }

    public final void putAttributeFloats(int attributeByteOffset, int numBaseTypeElements, float[] values, int vertexOffset)
    {
        int putPosition = attributeByteOffset + vertexByteSize * vertexOffset;
        for (int i = 0; i < values.length; i += numBaseTypeElements)
        {
            byteBuffer.position(putPosition);
            for (int j = 0; j < numBaseTypeElements; j++)
            {
                byteBuffer.putFloat(values[i + j]);
            }
            putPosition += vertexByteSize;
        }
    }

    /**
     * Binds this buffer with all specified attributes, such that it will work with the given program.
     */
    public final void bind(Program program)
    {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferHandle);
        for (Attribute attribute : attributes)
        {
            int location = program.getAttributeLocation(attribute);
            //TODO: Should we just auto-enable this once when the buffer is created?
            GLES20.glEnableVertexAttribArray(location);
            GLES20.glVertexAttribPointer(location, attribute.getNumBaseTypeElements(), attribute.getBaseType(), false, vertexByteSize, getAttributeByteOffset(attribute));
        }
    }

    /**
     * Transfer contents loaded by putAttribute* calls into graphics library. CAN ONLY BE CALLED ONCE!  After this method is called, no more put/transfer operations should occur.
     */
    public void transfer()
    {
        if (isTransferred)
        {
            throw new RuntimeException("Static buffer already transferred");
        }
        isTransferred = true;
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
        deallocateByteBuffer();
    }

    /**
     * Has this buffer been transferred to the GL already?
     *
     * @return
     */
    public boolean isTransferred()
    {
        return isTransferred;
    }


    public void putVertexAttributes(Mesh mesh, int vertexOffset)
    {
        for (Attribute attribute : attributes)
        {
            //TODO: Collada mesh output should be made to follow standard.  This can then be simple loop
            if (mesh.getPositionData() != null && attribute == Attribute.POSITION)
            {
                putAttributeFloats(attribute, mesh.getPositionData(), vertexOffset);
            }
            if (mesh.getNormalData() != null && attribute == Attribute.NORMAL)
            {
                putAttributeFloats(attribute, mesh.getNormalData(), vertexOffset);
            }
            if (mesh.getTexCoordData() != null && attribute == Attribute.TEXCOORD)
            {
                putAttributeFloats(attribute, mesh.getTexCoordData(), vertexOffset);
            }
        }
    }

    private void setAttributeByteOffset(Attribute attribute, int byteOffset)
    {
        attributeByteOffsets[attribute.ordinal()] = byteOffset;
    }

    private int getAttributeByteOffset(Attribute attribute)
    {
        return attributeByteOffsets[attribute.ordinal()];
    }
}
