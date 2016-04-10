package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.mesh.Mesh;

/**
 * TODO: Comment me!
 * <p/>
 * Created by Steve on 4/10/2016.
 */
public class TransferObject
{
    final Mesh mesh;
    final int indexInsertIndex;
    final int vertexInsertIndex;

    public TransferObject(Mesh mesh, int indexInsertIndex, int vertexInsertIndex)
    {
        this.mesh = mesh;
        this.indexInsertIndex = indexInsertIndex;
        this.vertexInsertIndex = vertexInsertIndex;
    }
}
