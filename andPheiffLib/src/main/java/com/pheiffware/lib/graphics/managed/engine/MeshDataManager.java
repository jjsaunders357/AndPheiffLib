package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.vertexBuffer.DynamicAttributeBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.MeshVertexDataPacker;
import com.pheiffware.lib.graphics.managed.vertexBuffer.MeshVertexIndexPacker;
import com.pheiffware.lib.graphics.managed.vertexBuffer.StaticAttributeBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.VertexAttributeHandle;
import com.pheiffware.lib.graphics.managed.vertexBuffer.VertexIndexHandle;

import java.util.EnumSet;

/**
 * Manages packing mesh data into vertex buffers and then accessing/drawing that data.
 * Created by Steve on 6/14/2017.
 */

public class MeshDataManager
{
    //Packer objects for aiding in the storing process
    private final MeshVertexIndexPacker indexPacker = new MeshVertexIndexPacker();
    private final MeshVertexDataPacker staticPacker = new MeshVertexDataPacker();
    private final MeshVertexDataPacker dynamicPacker = new MeshVertexDataPacker();

    //Buffers where vertex data it stored
    private final IndexBuffer indexBuffer = new IndexBuffer();
    private final StaticAttributeBuffer staticBuffer = new StaticAttributeBuffer();
    private final DynamicAttributeBuffer dynamicBuffer = new DynamicAttributeBuffer();

    /**
     * Adds mesh to list which should be packed.  Returns a handle to data which is invalid until packBuffer() is called.
     *
     * @param mesh              the mesh to add to the vertex buffer.
     * @param dynamicAttributes attributes which should be loaded into dynamic vertex buffer
     * @param technique         default technique to use when rendering this mesh (unless overridden).
     * @param renderProperties  default property values with the technique when rendering
     * @return a handle to use for binding to a program/technique for rendering
     */
    public MeshHandle addMesh(Mesh mesh, EnumSet<VertexAttribute> dynamicAttributes, Technique technique, RenderPropertyValue[] renderProperties)
    {
        VertexIndexHandle iHandle = indexPacker.addMesh(mesh);
        VertexAttributeHandle sHandle;
        VertexAttributeHandle dHandle;
        if (dynamicAttributes.size() > 0)
        {
            EnumSet<VertexAttribute> staticAttributes = EnumSet.copyOf(mesh.getAttributes());
            staticAttributes.removeAll(dynamicAttributes);

            dHandle = dynamicPacker.addMesh(mesh, dynamicAttributes);
            if (staticAttributes.size() > 0)
            {
                sHandle = staticPacker.addMesh(mesh, staticAttributes);
            }
            else
            {
                sHandle = null;
            }
        }
        else
        {
            sHandle = staticPacker.addMesh(mesh);
            dHandle = null;
        }
        return new MeshHandle(iHandle, sHandle, dHandle, technique, renderProperties);
    }

    public MeshHandle addStaticMesh(Mesh mesh)
    {
        return addStaticMesh(mesh, null, new RenderPropertyValue[]{});
    }

    public MeshHandle addStaticMesh(Mesh mesh, Technique technique)
    {
        return addStaticMesh(mesh, technique, new RenderPropertyValue[]{});
    }

    public MeshHandle addStaticMesh(Mesh mesh, Technique technique, RenderPropertyValue[] renderProperties)
    {
        VertexIndexHandle iHandle = indexPacker.addMesh(mesh);
        VertexAttributeHandle sHandle = staticPacker.addMesh(mesh, mesh.getAttributes());
        return new MeshHandle(iHandle, sHandle, null, technique, renderProperties);
    }

    public MeshHandle addMesh(Mesh mesh, EnumSet<VertexAttribute> dynamicAttributes, Technique technique)
    {
        return addMesh(mesh, dynamicAttributes, technique, new RenderPropertyValue[]{});
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

}


