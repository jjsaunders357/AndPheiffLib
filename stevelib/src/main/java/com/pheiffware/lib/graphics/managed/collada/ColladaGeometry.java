package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.mesh.MeshGroup;
import com.pheiffware.lib.graphics.utils.MathUtils;

import java.util.ArrayList;
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
class ColladaGeometry
{
    //Parallel lists of materials and meshes.  This cannot be a map because in SketchUp the materials are meaningless and repeat (meaning we would lose meshes).  However, in blenders the materials matter and we don't want to lose them.
    public final List<String> materialIDs = new ArrayList<>();
    public final List<Mesh> meshes = new ArrayList<>();

    public void add(String materialID, Mesh mesh)
    {
        materialIDs.add(materialID);
        meshes.add(mesh);
    }

    public MeshGroup createMeshGroup(Map<String, Material> materials)
    {
        //No initial transform can be specified in geometry elements
        MeshGroup meshGroup = new MeshGroup(MathUtils.IDENTITY_MATRIX4);
        for (int i = 0; i < meshes.size(); i++)
        {
            Material material = materials.get(materialIDs.get(i));
            meshGroup.add(material, meshes.get(i));
        }
        return meshGroup;
    }

    public MeshGroup createMeshGroup(Material material)
    {
        //No initial transform can be specified in geometry elements
        MeshGroup meshGroup = new MeshGroup(MathUtils.IDENTITY_MATRIX4);
        for (int i = 0; i < meshes.size(); i++)
        {
            meshGroup.add(material, meshes.get(i));
        }
        return meshGroup;
    }
}
