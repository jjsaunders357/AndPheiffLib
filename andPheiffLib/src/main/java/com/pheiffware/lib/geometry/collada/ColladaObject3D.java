package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;

import java.util.Map;

/**
 * Created by Steve on 2/15/2016.
 */
public class ColladaObject3D
{
    private Matrix4 initialMatrix;
    private final ColladaMaterial[] materials;
    private final Mesh[] meshes;

    public ColladaObject3D(Matrix4 initialMatrix, Map<ColladaMaterial, Mesh> meshMap)
    {
        this.initialMatrix = initialMatrix;
        materials = new ColladaMaterial[meshMap.size()];
        meshes = new Mesh[meshMap.size()];
        int index = 0;
        for (Map.Entry<ColladaMaterial, Mesh> entry : meshMap.entrySet())
        {
            materials[index] = entry.getKey();
            meshes[index] = entry.getValue();
            index++;
        }
    }

    public final Matrix4 getInitialMatrix()
    {
        return initialMatrix;
    }

    public final int getNumMeshes()
    {
        return meshes.length;
    }

    public final Mesh getMesh(int meshIndex)
    {
        return meshes[meshIndex];
    }

    public final Mesh[] getMeshes()
    {
        return meshes;
    }
    public final ColladaMaterial getMaterial(int meshIndex)
    {
        return materials[meshIndex];
    }



    /**
     * Only for use in testing.  For a given material, this looks up the corresponding mesh.  Not particularly efficient nor an operation for external code to use.
     *
     * @param material
     * @return
     */
    Mesh matMeshTO(ColladaMaterial material)
    {
        for (int i = 0; i < materials.length; i++)
        {
            if (materials[i] == material)
            {
                return meshes[i];
            }
        }
        return null;
    }
}
