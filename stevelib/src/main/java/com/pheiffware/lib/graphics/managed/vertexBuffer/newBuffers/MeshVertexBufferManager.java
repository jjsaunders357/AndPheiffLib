package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;

import java.nio.ByteBuffer;
import java.util.EnumSet;

/**
 * Created by Steve on 6/14/2017.
 */

public class MeshVertexBufferManager
{
    private final MeshVertexIndexPacker indexPacker = new MeshVertexIndexPacker();
    private final MeshVertexDataPacker staticPacker = new MeshVertexDataPacker();
    private final MeshVertexDataPacker dynamicPacker = new MeshVertexDataPacker();
    //Buffers where vertex data it stored
    private final IndexBuffer indexBuffer = new IndexBuffer();
    private final StaticAttributeBuffer staticBuffer = new StaticAttributeBuffer();
    private final DynamicAttributeBuffer dynamicBuffer = new DynamicAttributeBuffer();

    /**
     * Adds static mesh to list which should be packed.  Returns a handle to data which is invalid until packBuffer() is called.
     *
     * @param mesh the mesh to add to the vertex buffer.
     * @return a handle to use for binding to a program/technique for rendering
     */
    public VertexDataHandle addStaticMesh(Mesh mesh)
    {
        VertexIndexHandle iHandle = indexPacker.addMesh(mesh);
        VertexAttributeHandle sHandle = staticPacker.addMesh(mesh, mesh.getAttributes());
        return new VertexDataHandle(iHandle, sHandle, null);
    }

    /**
     * Adds mesh to list which should be packed.  Returns a handle to data which is invalid until packBuffer() is called.
     *
     * @param mesh              the mesh to add to the vertex buffer.
     * @param dynamicAttributes attributes which should be loaded into dynamic vertex buffer
     * @return a handle to use for binding to a program/technique for rendering
     */
    public VertexDataHandle addMesh(Mesh mesh, EnumSet<VertexAttribute> dynamicAttributes)
    {
        VertexIndexHandle iHandle = indexPacker.addMesh(mesh);
        VertexAttributeHandle sHandle;
        VertexAttributeHandle dHandle;
        if (dynamicAttributes.size() > 0)
        {
            EnumSet<VertexAttribute> staticAttributes = EnumSet.copyOf(mesh.getAttributes());
            staticAttributes.removeAll(dynamicAttributes);

            sHandle = staticPacker.addMesh(mesh, staticAttributes);
            dHandle = dynamicPacker.addMesh(mesh, dynamicAttributes);
        }
        else
        {
            sHandle = staticPacker.addMesh(mesh);
            dHandle = null;
        }
        return new VertexDataHandle(iHandle, sHandle, dHandle);
    }

    /**
     * Packs and transfers all data into appropriate vertex buffers.
     */
    public void packAndTransfer()
    {
        indexPacker.packBuffer(indexBuffer);
        staticPacker.packBuffer(staticBuffer);
        dynamicPacker.packBuffer(dynamicBuffer);
        indexBuffer.transfer();
        staticBuffer.transfer();
        dynamicBuffer.transfer();
    }

    /**
     * If any dynamic data is changed, this must be called to transfer it to openGL before rendering.
     */
    public void transferDynamicData()
    {
        dynamicBuffer.transfer();
    }

    public final void drawTriangles(Program program, VertexDataHandle vertexDataHandle)
    {
        draw(program, vertexDataHandle, GLES20.GL_TRIANGLES);
    }

    public final void draw(Program program, VertexDataHandle vertexDataHandle, int primitiveType)
    {
        staticBuffer.drawSetup(program, vertexDataHandle.sHandle);
        if (vertexDataHandle.dHandle != null)
        {
            dynamicBuffer.drawSetup(program, vertexDataHandle.dHandle);
        }
        indexBuffer.draw(primitiveType, vertexDataHandle.iHandle);

    }

    public ByteBuffer edit(VertexDataHandle handle)
    {
        return dynamicBuffer.edit(handle.dHandle);
    }
}


