package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.geometry.collada.ColladaMaterial;

/**
 * Holds information about a single mesh which should be rendered with a specific Program and specific values for uniforms in that Program.  The set of uniform values may be
 * incomplete as some values may be specified more globally.
 * <p/>
 * Created by Steve on 4/10/2016.
 */
public class MeshRenderHandle
{
    final ColladaMaterial colladaMaterial;
    final int vertexOffset;
    final int numVertices;

    public MeshRenderHandle(ColladaMaterial colladaMaterial, int vertexOffset, int numVertices)
    {
        this.colladaMaterial = colladaMaterial;
        this.vertexOffset = vertexOffset;
        this.numVertices = numVertices;
    }
}
