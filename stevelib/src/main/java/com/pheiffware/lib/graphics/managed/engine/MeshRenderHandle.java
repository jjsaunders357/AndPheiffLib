package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.mesh.Material;

/**
 * Holds information about a single mesh which should be rendered with a specific Program and specific values for uniforms in that Program.  The set of uniform values may be
 * incomplete as some values may be specified more globally.
 * <p/>
 * Created by Steve on 4/10/2016.
 */
public class MeshRenderHandle
{
//    final Program program;
//    final int[] uniformLocations;
//    final Object[] uniformValues;

    final Material material;
    final int vertexOffset;
    final int numVertices;

    public MeshRenderHandle(Material material, int vertexOffset, int numVertices)
    {
        this.material = material;
        this.vertexOffset = vertexOffset;
        this.numVertices = numVertices;
    }
}
