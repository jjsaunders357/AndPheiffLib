package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.graphics.Matrix4;

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
    public Matrix4 getTransform()
    {
        return Matrix4.newIdentity();
    }

    @Override
    public MeshGroup retrieveMeshGroup(boolean flatten)
    {
        return meshGroup;
    }
}