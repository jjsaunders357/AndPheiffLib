package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.graphics.Matrix4;

/**
 * Created by Steve on 2/15/2016.
 */
public class ColladaObject3D
{
    private Matrix4 matrix;
    private final MeshGroup meshGroup;

    public ColladaObject3D(Matrix4 matrix, MeshGroup meshGroup)
    {
        this.matrix = matrix;
        this.meshGroup = meshGroup;
    }

    public Matrix4 getMatrix()
    {
        return matrix;
    }

    public MeshGroup getMeshGroup()
    {
        return meshGroup;
    }
}
