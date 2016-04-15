package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;

import java.util.Map;

/**
 * Created by Steve on 2/15/2016.
 */
public class ColladaObject3D
{
    private Matrix4 initialMatrix;
    private final Map<ColladaMaterial, Mesh> meshes;

    public ColladaObject3D(Matrix4 initialMatrix, Map<ColladaMaterial, Mesh> meshes)
    {
        this.initialMatrix = initialMatrix;
        this.meshes = meshes;
    }

    public Matrix4 getInitialMatrix()
    {
        return initialMatrix;
    }

    public Map<ColladaMaterial, Mesh> getMeshMap()
    {
        return meshes;
    }
}
