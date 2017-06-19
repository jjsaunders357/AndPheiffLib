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
    public final void drawSetup(Program program, VertexAttributeHandle handle)
    {
        drawSetup(program, handle.vertexAttributes, handle.byteOffset);
    }

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
