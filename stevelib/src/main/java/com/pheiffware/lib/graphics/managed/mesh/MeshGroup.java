package com.pheiffware.lib.graphics.managed.mesh;

import com.pheiffware.lib.graphics.Matrix4;

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
    //The initial transform this object had when loaded.  If not identity, this will NOT have been applied to the loaded mesh.
    private final Matrix4 initialTransformMatrix;

    //A map from each material to a list of meshes with that material
    private final Map<Material, List<Mesh>> meshes = new HashMap<>();

    public MeshGroup(Matrix4 initialTransformMatrix)
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


    public void applyMatrixTransform(Matrix4 transformMatrix)
    {
        //TODO: Apply matrix transformation to child meshgroup
    }

    public List<Mesh> getMeshes(Material material)
    {
        return meshes.get(material);
    }

    public Matrix4 getInitialTransformMatrix()
    {
        return initialTransformMatrix;
    }
}
