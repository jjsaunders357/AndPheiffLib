package com.pheiffware.lib.graphics.managed.vertexBuffer;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;

/**
 * Created by Steve on 6/14/2017.
 */

public abstract class AttributeVertexBuffer extends VertexBuffer
{
    /**
     * Binds the attributes of the buffer, specified in vertexAttributeGroup to the given program (assumed to already be bound).
     * Any attribute not used by the program is ignored.
     * ASSUMES ALL ATTRIBUTES PACKED IN ORDER DICTATED BY VertexAttribute Enum!!!s
     *
     * @param program          program to use (assumed to bound already)
     * @param vertexAttributeGroup a description of the vertex data packed in the buffer at this point
     * @param byteOffset       the offset in the buffer where the data is located
     */
    public final void bindToProgram(Program program, VertexAttributeGroup vertexAttributeGroup, int byteOffset)
    {
        bind();
        for (VertexAttribute vertexAttribute : program.getAttributes())
        {
            if (vertexAttributeGroup.contains(vertexAttribute))
            {
                int location = program.getAttributeLocation(vertexAttribute);
                GLES20.glEnableVertexAttribArray(location);
                GLES20.glVertexAttribPointer(
                        location,
                        vertexAttribute.getNumBaseTypeElements(),
                        vertexAttribute.getBaseType(),
                        false,
                        vertexAttributeGroup.getVertexByteSize(),
                        byteOffset + vertexAttributeGroup.getAttributeByteOffset(vertexAttribute));
            }
        }
    }

}
