package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.mesh.MeshGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A group of meshes, nominally mapped by material.
 * In Blender files: the material is meaningful
 * In Sketchup files: the material is meaningless and instead materials have to be determined in geometry instances elsewhere.
 * <p/>
 * For sketchup, this information is later combined with other material/instance information to actually determine material.
 * Created by Steve on 2/16/2016.
 */
public class ColladaGeometry
{
    public final List<String> materialIDs = new ArrayList<>();
    public final List<Mesh> meshes = new ArrayList<>();

    public void add(String materialID, Mesh mesh)
    {
        materialIDs.add(materialID);
        meshes.add(mesh);
    }

    public MeshGroup createMeshGroup(Map<String, Material> materials)
    {
        MeshGroup meshGroup = new MeshGroup();
        for (int i = 0; i < meshes.size(); i++)
        {
            Material material = materials.get(materialIDs.get(i));
            meshGroup.add(material, meshes.get(i));
        }
        return meshGroup;
    }

    public MeshGroup createMeshGroup(Map<String, Material> materials, String materialID)
    {
        Material material = materials.get(materialID);
        MeshGroup meshGroup = new MeshGroup();
        for (int i = 0; i < meshes.size(); i++)
        {
            meshGroup.add(material, meshes.get(i));
        }
        return meshGroup;
    }
}
