package com.pheiffware.lib.geometry.collada;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO 0.5 = 1/2: Load camera positions or main view position (sketchup)

/**
 * Holds key data from a parsed Collada file.  Unlike the factory, which keep most intermediate data, this only keeps the essential stuff.
 * Additionally, this maps all information to a "name" rather than an "id".
 * id a a guaranteed to be unique and is generated by the authoring software for internally referencing data in one part of the file from another.
 * name is a human readable string which was entered by the person using the authoring software.
 * <p>
 * If name collisions occur:
 * materials - only one material is kept in the map, the others are discarded
 * objects - additional objects, past the 1st one, with the same name are put in the anonymousObjects list
 * <p>
 * Created by Steve on 2/14/2016.
 */
public class Collada
{
    public static final String COLLADA_VERTEX_POSITION = "POSITION";
    public static final String COLLADA_VERTEX_NORMAL = "NORMAL";
    public static final String COLLADA_VERTEX_TEXCOORD = "TEXCOORD";
    public static final String COLLADA_VERTEX_COLOR = "COLOR";

    //A list of all images used in textures in the Collada file
    public final Collection<String> imageFileNames;

    //Map from material names to material data
    public final Map<String, ColladaMaterial> materialsByName;

    //Map from node names defined in the visual scene to collapsed collections of meshes, keyed by which material is used to render them
    public final Map<String, ColladaObject3D> objects;

    //List of all unnamed objects, either: node didn't have a name attribute, multiple nodes with the same name attribute (all but 1st put in this bin) or top-level geometry instance in a SketchUp node.
    public final List<ColladaObject3D> anonymousObjects;

    public Collada(Collection<String> imageFileNames, Map<String, ColladaMaterial> materialsByID, Map<String, ColladaObject3D> objects, List<ColladaObject3D> anonymousObjects)
    {
        this.imageFileNames = imageFileNames;
        materialsByName = remapMaterialsByName(materialsByID);
        this.anonymousObjects = anonymousObjects;
        this.objects = objects;
    }

    private Map<String, ColladaMaterial> remapMaterialsByName(Map<String, ColladaMaterial> materialsByID)
    {
        //Map from material ids to material data
        Map<String, ColladaMaterial> materialsByNameMap = new HashMap<>();
        //Map material's by name
        for (ColladaMaterial colladaMaterial : materialsByID.values())
        {
            materialsByNameMap.put(colladaMaterial.name, colladaMaterial);
        }
        return materialsByNameMap;
    }


}
