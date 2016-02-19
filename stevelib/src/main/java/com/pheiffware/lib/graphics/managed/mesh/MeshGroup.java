package com.pheiffware.lib.graphics.managed.mesh;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A map from materials to the mesh which should be rendered by them.
 * Created by Steve on 2/16/2016.
 */
public class MeshGroup
{
    //The initial transform this object had when loaded.  This has NOT been applied to the loaded mesh.
    private final float[] initialTransformMatrix;
    private final Map<Material, List<Mesh>> meshes = new HashMap<>();

    public MeshGroup(float[] initialTransformMatrix)
    {
        this.initialTransformMatrix = initialTransformMatrix;
    }

    private List<Mesh> getMeshList(Material material)
    {
        List<Mesh> meshList = meshes.get(material);
        if (meshList == null)
        {
            meshList = new ArrayList<>();
            meshes.put(material, meshList);
        }
        return meshList;
    }

    public void add(Material material, Mesh mesh)
    {
        List<Mesh> meshList = getMeshList(material);
        meshList.add(mesh);
    }

    public void add(MeshGroup meshGroup)
    {
        for (Map.Entry<Material, List<Mesh>> entry : meshGroup.meshes.entrySet())
        {
            List<Mesh> destMeshList = getMeshList(entry.getKey());
            List<Mesh> sourceMeshList = meshGroup.getMeshList(entry.getKey());
            destMeshList.addAll(sourceMeshList);
        }
    }


    public void applyMatrixTransform(float[] transformMatrix)
    {
        //TODO: Apply matrix transformation to child meshgroup
    }

    public List<Mesh> getMesh(Material material)
    {
        return meshes.get(material);
    }

    public float[] getInitialTransformMatrix()
    {
        return initialTransformMatrix;
    }
}
