package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A map from materials to a list of meshes which should be rendered by them.  This is used while loading to efficiently group meshes with the same material.  When finished each
 * list of meshes is collapsed into one mesh.
 * <p/>
 * Created by Steve on 2/16/2016.
 */
class MeshGroup
{
    //The initial transform this object had when loaded.  If not identity, this will NOT have been applied to the loaded mesh.
    private final Matrix4 initialTransformMatrix;

    //A map from each material to a list of meshes with that material
    private final Map<ColladaMaterial, List<Mesh>> meshes = new HashMap<>();

    public MeshGroup(Matrix4 initialTransformMatrix)
    {
        this.initialTransformMatrix = initialTransformMatrix;
    }

    private List<Mesh> getMeshList(ColladaMaterial colladaMaterial)
    {
        List<Mesh> meshList = meshes.get(colladaMaterial);
        if (meshList == null)
        {
            meshList = new LinkedList<>();
            meshes.put(colladaMaterial, meshList);
        }
        return meshList;
    }

    public void add(ColladaMaterial colladaMaterial, Mesh mesh)
    {
        List<Mesh> meshList = getMeshList(colladaMaterial);
        meshList.add(mesh);
    }

    public void add(MeshGroup meshGroup)
    {
        for (Map.Entry<ColladaMaterial, List<Mesh>> entry : meshGroup.meshes.entrySet())
        {
            List<Mesh> destMeshList = getMeshList(entry.getKey());
            List<Mesh> sourceMeshList = meshGroup.getMeshList(entry.getKey());
            destMeshList.addAll(sourceMeshList);
        }
    }


    public MeshGroup newTransformedMeshGroup(Matrix4 transformMatrix)
    {
        MeshGroup transformedMeshGroup = new MeshGroup(Matrix4.newIdentity());
        for (Map.Entry<ColladaMaterial, List<Mesh>> entry : meshes.entrySet())
        {
            ColladaMaterial colladaMaterial = entry.getKey();
            List<Mesh> meshList = entry.getValue();
            for (Mesh mesh : meshList)
            {
                Mesh transformedMesh = mesh.newTransformedMesh(transformMatrix);
                transformedMeshGroup.add(colladaMaterial, transformedMesh);
            }
        }
        return transformedMeshGroup;
    }

    /**
     * Goes through all materials and collapses each list of meshes to a single mesh object.
     *
     * @return a map from materials to single, collapsed, meshes
     */
    public Map<ColladaMaterial, Mesh> collapseMeshLists()
    {
        Map<ColladaMaterial, Mesh> materialToMeshMap = new HashMap<>();

        for (Map.Entry<ColladaMaterial, List<Mesh>> entry : meshes.entrySet())
        {
            ColladaMaterial colladaMaterial = entry.getKey();
            List<Mesh> meshList = entry.getValue();
            Mesh collapsedMesh;
            if (meshList.size() == 1)
            {
                collapsedMesh = meshList.get(0);
            }
            else
            {
                collapsedMesh = new Mesh(meshList);
            }
            materialToMeshMap.put(colladaMaterial, collapsedMesh);
        }
        return materialToMeshMap;
    }

    public List<Mesh> getMeshes(ColladaMaterial colladaMaterial)
    {
        return meshes.get(colladaMaterial);
    }

    public Map<ColladaMaterial, List<Mesh>> getMeshMap()
    {
        return meshes;
    }

    public Matrix4 getInitialTransformMatrix()
    {
        return initialTransformMatrix;
    }

}
