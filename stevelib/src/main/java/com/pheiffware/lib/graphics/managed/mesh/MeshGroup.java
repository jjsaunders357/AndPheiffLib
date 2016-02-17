package com.pheiffware.lib.graphics.managed.mesh;

import java.util.HashMap;

/**
 * A map from materials to the mesh which should be rendered by them.
 * Created by Steve on 2/16/2016.
 */
public class MeshGroup
{
    private final HashMap<Material, Mesh> meshes = new HashMap<>();

    public void add(Material material, Mesh mesh)
    {
        meshes.put(material, mesh);
    }

}
