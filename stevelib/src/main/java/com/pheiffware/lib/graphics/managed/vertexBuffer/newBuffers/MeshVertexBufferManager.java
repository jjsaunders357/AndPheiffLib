package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;

import java.util.EnumSet;

/**
 * Created by Steve on 6/14/2017.
 */

public class MeshVertexBufferManager
{
    //Buffers where vertex data it stored
    private final StaticAttributeBuffer staticBuffer = new StaticAttributeBuffer();
    private final DynamicAttributeBuffer dynamicBuffer = new DynamicAttributeBuffer();
    private final IndexBuffer indexBuffer = new IndexBuffer();

    /**
     * Adds mesh to list which should be packed.  Returns a handle to data which is invalid until pack() is called.
     * <p>
     * Handle is invalid until pack() is called.
     *
     * @param mesh              the mesh to add to the vertex buffer.
     * @param dynamicAttributes attributes which should be loaded into dynamic vertex buffer
     * @return a handle to use for binding to a program/technique for rendering
     */
    public VertexDataHandle addMesh(Mesh mesh, EnumSet<VertexAttribute> dynamicAttributes)
    {
        VertexAttributeHandle dHandle;
        VertexAttributeHandle sHandle;
        VertexIndexHandle iHandle;
        if (dynamicAttributes.size() > 0)
        {
            EnumSet<VertexAttribute> staticAttributes = EnumSet.copyOf(mesh.getAttributes());
            staticAttributes.removeAll(dynamicAttributes);
            dHandle = dynamicBuffer.addMesh(mesh, dynamicAttributes);
            sHandle = staticBuffer.addMesh(mesh, staticAttributes);
        }
        else
        {
            dHandle = null;
            sHandle = staticBuffer.addMesh(mesh);
        }
        iHandle = indexBuffer.addMesh(mesh);
        return new VertexDataHandle(dHandle, sHandle, iHandle);
    }

    /**
     * Packs and transfers all data into appropriate vertex buffers.
     */
    public void packAndTransfer()
    {
        dynamicBuffer.packAndTransfer();
        staticBuffer.packAndTransfer();
        indexBuffer.packAndTransfer();
    }

    /**
     * If any dynamic data is changed, this must be called to transfer it to openGL before rendering.
     */
    public void transferDynamicData()
    {
        dynamicBuffer.transfer();
    }
}


