package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.mesh.Material;

/**
 * TODO: Comment me!
 * <p/>
 * Created by Steve on 4/10/2016.
 */
public class StaticMeshHandle
{
    final Material material;
    final int vertexOffset;
    final int numVertices;

    public StaticMeshHandle(Material material, int vertexOffset, int numVertices)
    {
        this.material = material;
        this.vertexOffset = vertexOffset;
        this.numVertices = numVertices;
    }
}
