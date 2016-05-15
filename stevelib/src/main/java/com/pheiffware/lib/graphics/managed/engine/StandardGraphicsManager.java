package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.program.Technique;

/**
 * This class manages storing data in index/vertex buffers and then conveniently/efficiently rendering that data.
 * <p/>
 * The core organizational structures are MeshHandles.  These contain a reference into the buffers where primitives are stored along with default rendering parameters such as color
 * and shininess.  An ObjectHandle is a reference to a collection of meshes, possibly rendered with different techniques, which share properties such as ModelMatrix.
 * <p/>
 * Typical usage:
 * <p/>
 * 1. Call addObject() and addMesh() over and over.
 * <p/>
 * 2. Call transfer()
 * <p/>
 * 3. Call setDefaultPropertyValues()
 * <p/>
 * TODO: Comment once we advance further
 * <p/>
 * 4. Call renderNow() over and over again
 * <p/>
 * <p/>
 * Created by Steve on 4/13/2016.
 */
public class StandardGraphicsManager extends BaseGraphicsManager
{
    public StandardGraphicsManager(Technique[] techniques, StaticVertexBuffer[] vertexBuffers)
    {
        super(techniques, vertexBuffers);
    }
}
