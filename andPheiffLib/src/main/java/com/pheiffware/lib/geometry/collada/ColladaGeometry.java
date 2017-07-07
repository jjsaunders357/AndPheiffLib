package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A group of meshes each mapped to some local generated material ID.  This ID means nothing except when referenced later when actual geometry instances are created and this local
 * generated ID is mapped to a real Collada material ID.  Any geometry without a material defined is assigned the default material ID "".
 * <p/>
 * Created by Steve on 2/16/2016.
 */
class ColladaGeometry
{
    //Parallel lists of local material IDs and meshes.  This is not a map, because localMaterialIDs repeat.
    public final List<String> localMaterialIDs = new ArrayList<>();
    public final List<Mesh> meshes = new ArrayList<>();

    public void add(String materialID, Mesh mesh)
    {
        localMaterialIDs.add(materialID);
        meshes.add(mesh);
    }

    /**
     * Creates a mesh group object referencing real ColladaMaterials.  The supplied map, must map this geometry's fake local material IDs to actual Collada materials. "" should map
     * to the default material.
     *
     * @param materialMap a map from this geometry's local material naming convention to actual materials.
     * @return
     */
    public MeshGroup createMeshGroup(Map<String, ColladaMaterial> materialMap)
    {
        //No initial transform can be specified in geometry elements
        MeshGroup meshGroup = new MeshGroup(Matrix4.newIdentity());
        for (int i = 0; i < meshes.size(); i++)
        {
            ColladaMaterial colladaMaterial = materialMap.get(localMaterialIDs.get(i));
            meshGroup.add(colladaMaterial, meshes.get(i));
        }
        return meshGroup;
    }
}
