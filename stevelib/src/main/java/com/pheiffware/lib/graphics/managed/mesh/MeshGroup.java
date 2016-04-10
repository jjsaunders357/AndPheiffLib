package com.pheiffware.lib.graphics.managed.mesh;

import com.pheiffware.lib.graphics.Matrix4;

import java.util.HashMap;
import java.util.LinkedList;
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
            meshList = new LinkedList<>();
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


    public MeshGroup newTransformedMeshGroup(Matrix4 transformMatrix)
    {
        MeshGroup transformedMeshGroup = new MeshGroup(Matrix4.newIdentity());
        for (Map.Entry<Material, List<Mesh>> entry : meshes.entrySet())
        {
            Material material = entry.getKey();
            List<Mesh> meshLists = entry.getValue();
            for (Mesh mesh : meshLists)
            {
                Mesh transformedMesh = mesh.newTransformedMesh(transformMatrix);
                transformedMeshGroup.add(material, transformedMesh);
            }
        }
        return transformedMeshGroup;
    }

    public List<Mesh> getMeshes(Material material)
    {
        return meshes.get(material);
    }

    public Map<Material, List<Mesh>> getMeshMap()
    {
        return meshes;
    }

    public Matrix4 getInitialTransformMatrix()
    {
        return initialTransformMatrix;
    }
}
