package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.graphics.managed.mesh.MeshGroup;
import com.pheiffware.lib.graphics.utils.MathUtils;

/**
 * Created by Steve on 2/20/2016.
 */
class DirectMeshGroupProxy implements MeshGroupProxy
{
    private final MeshGroup meshGroup;

    public DirectMeshGroupProxy(MeshGroup meshGroup)
    {
        this.meshGroup = meshGroup;
    }

    @Override
    public String getID()
    {
        return null;
    }

    @Override
    public String getName()
    {
        return null;
    }

    @Override
    public float[] getTransform()
    {
        return MathUtils.IDENTITY_MATRIX4;
    }

    @Override
    public MeshGroup retrieveMeshGroup(boolean flatten)
    {
        return meshGroup;
    }
}