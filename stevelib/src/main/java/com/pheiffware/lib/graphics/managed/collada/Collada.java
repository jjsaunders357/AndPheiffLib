package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.Object3D;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: Deal with meshes that have no material (ie blender) Will simply not have a material listed in either geometry or object definition
//TODO: Load camera positions or main view position (sketchup)
/**
 * Holds key data from a parsed Collada file.  Unlike the factory, which keep most intermediate data, this only keeps the essential stuff.
 * Additionally, this maps all information to a "name" rather than an "id".
 * id a a guaranteed to be unique and is generated by the authoring software for internally referencing data in one part of the file from another.
 * name is a human readable string which was entered by the person using the authoring software.
 *
 * If name collisions occur:
 * materials - only one material is kept in the map, the others are discarded
 * objects - additional objects, past the 1st one, with the same name are put in the anonymousObjects list
 *
 * Created by Steve on 2/14/2016.
 */
public class Collada
{
    //A list of all images used in textures in the Collada file
    public final Collection<String> imageFileNames;

    //Map from material names to material data
    public final Map<String, Material> materialsByName;

    //Map from node names defined in the visual scene to collapsed collections of meshes, keyed by which material is used to render them
    public final Map<String, Object3D> objects;

    //List of all unnamed objects, either: node didn't have a name attribute, multiple nodes with the same name attribute (all but 1st put in this bin) or top-level geometry instance in a SketchUp node.
    public final List<Object3D> anonymousObjects;

    public Collada(Collection<String> imageFileNames, Map<String, Material> materialsByID, Map<String, Object3D> objects, List<Object3D> anonymousObjects)
    {
        this.imageFileNames = imageFileNames;
        materialsByName = remapMaterialsByName(materialsByID);
        this.anonymousObjects = anonymousObjects;
        this.objects = objects;
    }

    private Map<String, Material> remapMaterialsByName(Map<String, Material> materialsByID)
    {
        //Map from material ids to material data
        Map<String, Material> materialsByNameMap = new HashMap<>();
        //Map material's by name
        for (Material material : materialsByID.values())
        {
            materialsByNameMap.put(material.name, material);
        }
        return materialsByNameMap;
    }


}
