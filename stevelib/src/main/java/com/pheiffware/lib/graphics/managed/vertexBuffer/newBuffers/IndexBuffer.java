package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.Mesh;

import java.nio.ByteBuffer;

/**
 * A VertexBuffer which holds index data and whose contents will be set once and never changed.
 * <p/>
 * Usage should look like:
 * <p/>
 * Setup:
 * <p/>
 * buffer.allocateSoftwareBuffer(size)
 * <p>
 * buffer.put*
 * <p/>
 * ...
 * <p/>
 * buffer.transfer(gl);
 * <p/>
 * Per frame (or update period):
 * <p/>
 * buffer.bind(gl);
 * <p/>
 * YOU CANNOT put more data in once transfer is called!
 * Created by Steve on 6/14/2017.
 */

public class IndexBuffer extends VertexBuffer
{
    private final MeshVertexIndexPacker dataPacker = new MeshVertexIndexPacker();
    private boolean isTransferred;

    public VertexIndexHandle addMesh(Mesh mesh)
    {
        return dataPacker.addMesh(mesh);
    }

    public final void drawTriangles(VertexIndexHandle handle)
    {
        draw(handle, GLES20.GL_TRIANGLES);
    }

    public final void draw(VertexIndexHandle handle, int primitiveType)
    {
        bind();
        GLES20.glDrawElements(primitiveType, handle.numVertices, GLES20.GL_UNSIGNED_SHORT, handle.byteOffset);
    }

    @Override
    protected int calcPackedSize()
    {
        return dataPacker.calcRequiredSpace();
    }

    protected void pack(ByteBuffer byteBuffer)
    {
        dataPacker.pack(byteBuffer);
    }

    @Override
    protected void bind(int glHandle)
    {
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, glHandle);
    }

    @Override
    public void transfer()
    {
        if (isTransferred)
        {
            throw new RuntimeException("Static buffer already transferred");
        }
        super.transfer();
    }

    @Override
    protected void transferData(int bytesToTransfer, ByteBuffer byteBuffer)
    {
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, bytesToTransfer, byteBuffer, GLES20.GL_STATIC_DRAW);
        deallocateSoftwareBuffer();
        isTransferred = true;
    }

}
