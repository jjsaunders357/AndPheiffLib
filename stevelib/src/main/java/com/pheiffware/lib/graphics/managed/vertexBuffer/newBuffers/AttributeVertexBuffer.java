package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.program.VertexAttributes;

/**
 * Created by Steve on 6/14/2017.
 */

public abstract class AttributeVertexBuffer extends VertexBuffer
{
    /**
     * Binds the attributes of the buffer, specified in vertexAttributes to the given program.  Any attribute not used by the program is ignored.
     *
     * @param program program to use (assumed to bound already)
     * @param handle  the handle to vertex data
     */
    public final void drawSetup(Program program, VertexAttributeHandle handle)
    {
        drawSetup(program, handle.vertexAttributes, handle.byteOffset);
    }

    /**
     * Binds the attributes of the buffer, specified in vertexAttributes to the given program.  Any attribute not used by the program is ignored.
     * ASSUMES ALL ATTRIBUTES PACKED IN ORDER DICTATED BY VertexAttribute Enum!!!s
     *
     * @param program          program to use (assumed to bound already)
     * @param vertexAttributes a description of the vertex data packed in the buffer at this point
     * @param byteOffset       the offset in the buffer where the data is located
     */
    public final void drawSetup(Program program, VertexAttributes vertexAttributes, int byteOffset)
    {
        bind();
        for (VertexAttribute vertexAttribute : program.getAttributes())
        {
            if (vertexAttributes.contains(vertexAttribute))
            {
                int location = program.getAttributeLocation(vertexAttribute);
                GLES20.glEnableVertexAttribArray(location);
                GLES20.glVertexAttribPointer(
                        location,
                        vertexAttribute.getNumBaseTypeElements(),
                        vertexAttribute.getBaseType(),
                        false,
                        vertexAttributes.getVertexByteSize(),
                        byteOffset + vertexAttributes.getAttributeByteOffset(vertexAttribute));
            }
        }
    }

}
