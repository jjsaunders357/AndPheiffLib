package com.pheiffware.lib.graphics.managed.mesh;

/**
 * Created by Steve on 2/15/2016.
 */
public class Object3D
{
    private float[] matrix;
    private final MeshGroup meshGroup;

    public Object3D(float[] matrix, MeshGroup meshGroup)
    {
        this.matrix = matrix;
        this.meshGroup = meshGroup;
    }

    public float[] getMatrix()
    {
        return matrix;
    }

    public MeshGroup getMeshGroup()
    {
        return meshGroup;
    }
}
