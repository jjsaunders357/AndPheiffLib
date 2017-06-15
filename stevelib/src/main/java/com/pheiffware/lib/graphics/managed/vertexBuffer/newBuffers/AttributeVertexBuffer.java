package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.program.VertexAttributes;

import java.nio.ByteBuffer;
import java.util.EnumSet;

/**
 * Created by Steve on 6/14/2017.
 */

public abstract class AttributeVertexBuffer extends VertexBuffer
{
    private final MeshVertexDataPacker dataPacker = new MeshVertexDataPacker();

    public VertexAttributeHandle addMesh(Mesh mesh)
    {
        return dataPacker.addMesh(mesh);
    }

    public VertexAttributeHandle addMesh(Mesh mesh, EnumSet<VertexAttribute> vertexAttributes)
    {
        return dataPacker.addMesh(mesh, vertexAttributes);
    }

    public void pack(ByteBuffer byteBuffer)
    {
        allocateSoftwareBuffer(dataPacker.calcRequiredSpace());
        dataPacker.pack(byteBuffer);
    }

    public void bind(Program program, VertexAttributeHandle handle)
    {
        bind();
        VertexAttributes vertexAttributes = handle.vertexAttributes;
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
                        handle.byteOffset + vertexAttributes.getAttributeByteOffset(vertexAttribute));
            }
        }
    }
}
