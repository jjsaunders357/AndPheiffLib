package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.Color4F;
import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Main class which parses a Collada file to produce a Collada object.  This holds lots of additional intermediate information which can be used during testing or for other
 * purposes. Created by Steve on 2/19/2016.
 */
public class ColladaFactory
{
    //Any material without a texture will have this as its default diffuse color
    static final Color4F DEFAULT_AMBIENT = new Color4F(0.1f, 0.1f, 0.1f, 1f);
    static final Color4F DEFAULT_DIFFUSE = new Color4F(0.6f, 0.6f, 0.6f, 1f);
    static final Color4F DEFAULT_SPECULAR = new Color4F(1f, 1f, 1f, 1f);

    //Any material which does not define shininess gets this value
    static final float DEFAULT_SHININESS = 2f;

    //If no default material is defined, this this is assigned automatically
    private static final ColladaMaterial DEFAULT_DEFAULT_COLLADA_MATERIAL = new ColladaMaterial("", null, DEFAULT_AMBIENT, DEFAULT_DIFFUSE, DEFAULT_SPECULAR, DEFAULT_SHININESS);

    //Map from image ids to file names
    private final Map<String, String> imageFileNames = new HashMap<>();

    //Map from effect ids to effect data (effect data is identical to materials)
    private final Map<String, ColladaEffect> colladaEffects = new HashMap<>();

    //Map from material ids to material data
    private final Map<String, ColladaMaterial> materialsByID = new HashMap<>();

    //Map from ids to ColladaGeometry
    private final Map<String, ColladaGeometry> geometries = new HashMap<>();

    //Map from library node ids to MeshGroups (completely parsed/usable geometry objects).
    private final Map<String, MeshGroup> libraryMeshGroups = new HashMap<>();

    //Map from node names defined in the visual scene to collapsed collections of meshes, keyed by which material is used to render them
    private final Map<String, ColladaObject3D> objects = new HashMap<>();

    //List of all unnamed objects, either: node didn't have a name attribute, multiple nodes with the same name attribute (all but 1st put in this bin) or top-level geometry instance in a SketchUp node.
    private final List<ColladaObject3D> anonymousObjects = new LinkedList<>();

    //When position are loaded, a 1 is appended to the end of the loaded data to create a homogeneous coordinate
    private boolean homogenizePositions = true;

    //When position are loaded, a 0 is appended to the end of the loaded data to create a homogeneous vector
    private boolean homogenizeNormals = false;

    //Default material for meshes with unassigned material
    private ColladaMaterial defaultColladaMaterial = DEFAULT_DEFAULT_COLLADA_MATERIAL;


    public ColladaFactory()
    {
        clear();
    }

    public Collada loadCollada(AssetLoader al, String assetPath) throws XMLParseException, IOException
    {
        clear();
        return loadCollada(al.getInputStream(assetPath));
    }

    private void clear()
    {
        imageFileNames.clear();
        colladaEffects.clear();
        materialsByID.clear();
        geometries.clear();
        libraryMeshGroups.clear();
        objects.clear();
        anonymousObjects.clear();

        //Store default material
        materialsByID.put("", defaultColladaMaterial);
    }

    public Collada loadCollada(InputStream input) throws XMLParseException
    {
        //Note: Collada schema downloaded, but it is corrupt/incompatible with Android.  Schema fails to parse...
        Document doc = DomUtils.loadDocumentFromStream(input, null);
        Element rootElement = doc.getDocumentElement();
        ColladaAuthoringSoftware colladaAuthoringSoftware = ColladaAuthoringSoftware.parse(rootElement);


        Element libraryImagesElement = DomUtils.getSubElement(rootElement, "library_images");
        if (libraryImagesElement != null)
        {
            DomUtils.putSubElementsInMap(imageFileNames, libraryImagesElement, "image", "id", new ColladaLibraryImageFactory());
        }

        Element libraryEffectsElement = DomUtils.assertGetSubElement(rootElement, "library_effects");
        DomUtils.putSubElementsInMap(colladaEffects, libraryEffectsElement, "effect", "id", new ColladaEffectFactory());
        Element libraryMaterialsElement = DomUtils.assertGetSubElement(rootElement, "library_materials");
        DomUtils.putSubElementsInMap(materialsByID, libraryMaterialsElement, "material", "id", new ColladaMaterialFactory(imageFileNames, colladaEffects));
        Element libraryGeometriesElement = DomUtils.assertGetSubElement(rootElement, "library_geometries");
        DomUtils.putSubElementsInMap(geometries, libraryGeometriesElement, "geometry", "id", new ColladaGeometryFactory(homogenizePositions, homogenizeNormals));

        Element libraryNodesElement = DomUtils.getSubElement(rootElement, "library_nodes");
        if (libraryNodesElement != null)
        {
            LibraryColladaNodeProcessor colladaNodeProcessor = new LibraryColladaNodeProcessor(libraryNodesElement, materialsByID, geometries);
            libraryMeshGroups.putAll(colladaNodeProcessor.getLibraryMeshGroups());
        }
        Element sceneElement = DomUtils.assertGetSubElementChain(rootElement, "library_visual_scenes", "visual_scene");
        if (colladaAuthoringSoftware == ColladaAuthoringSoftware.SKETCHUP)
        {
            //SketchUp wraps everything in a "SketchUp" node.
            sceneElement = DomUtils.assertGetSubElement(sceneElement, "node");
            if (!sceneElement.getAttribute("name").equals("SketchUp"))
            {
                throw new XMLParseException("SketchUp Collada file missing root \"SketchUp\" node in scene");
            }
        }
        SceneColladaNodeProcessor colladaNodeProcessor = new SceneColladaNodeProcessor(sceneElement, materialsByID, geometries, libraryMeshGroups);
        objects.putAll(colladaNodeProcessor.getObjects());
        anonymousObjects.addAll(colladaNodeProcessor.getAnonymousObjects());
        return new Collada(new HashSet<>(imageFileNames.values()), new HashMap<>(materialsByID), new HashMap<>(objects), new LinkedList<>(anonymousObjects));
    }


    Map<String, String> getImageFileNames()
    {
        return imageFileNames;
    }

    Map<String, ColladaEffect> getColladaEffects()
    {
        return colladaEffects;
    }

    Map<String, ColladaGeometry> getGeometries()
    {
        return geometries;
    }

    Map<String, MeshGroup> getLibraryMeshGroups()
    {
        return libraryMeshGroups;
    }

    Map<String, ColladaObject3D> getObjects()
    {
        return objects;
    }

    List<ColladaObject3D> getAnonymousObjects()
    {
        return anonymousObjects;
    }

    public void setHomogenizePositions(boolean homogenizePositions)
    {
        this.homogenizePositions = homogenizePositions;
    }

    public void setHomogenizeNormals(boolean homogenizeNormals)
    {
        this.homogenizeNormals = homogenizeNormals;
    }

    public void setDefaultColladaMaterial(ColladaMaterial defaultColladaMaterial)
    {
        this.defaultColladaMaterial = defaultColladaMaterial;
    }
}
