package com.pheiffware.lib.graphics.managed.collada;

import android.content.res.AssetManager;

import com.pheiffware.lib.graphics.GColor;
import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.MeshGroup;
import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Validator;

/**
 * Parses a Collada file
 * Created by Steve on 2/14/2016.
 */
public class Collada
{
    //Any material which does not define shininess gets this value
    public static final float DEFAULT_SHININESS = 0.5f;

    //Any material with a texture will have this as its default diffuse color
    public static final GColor DEFAULT_DIFFUSE_TEXTURE = new GColor(1f, 1f, 1f, 1f);
    public static final GColor DEFAULT_AMBIENT = new GColor(0f, 0f, 0f, 1f);
    public static final GColor DEFAULT_SPECULAR = new GColor(1f, 1f, 1f, 1f);

    public enum TOOL
    {
        BLENDER, SKETCHUP
    }

    private static final Validator validator = DomUtils.createValidator("meshes\\collada_schema_1_4_1.xsd");

    //Map from image ids to file names
    private final Map<String, String> imageFileNames = new HashMap<>();

    //Map from effect ids to effect data (effect data is identical to materials)
    private final Map<String, ColladaEffect> colladaEffects = new HashMap<>();

    //Map from material ids to material data
    private final Map<String, Material> materials = new HashMap<>();

    //Map from ids to ColladaGeometry
    private final Map<String, ColladaGeometry> geometries = new HashMap<>();

    //Map from node ids to completely defined meshGroups
    private final Map<String, MeshGroup> meshGroups = new HashMap<>();

    public void loadCollada(AssetManager assetManager, String assetFileName) throws XMLParseException
    {
        try
        {
            loadCollada(assetManager.open(assetFileName));
        }
        catch (IOException e)
        {
            throw new XMLParseException(e);
        }
    }

    private TOOL parseTool(Element element) throws XMLParseException
    {
        Element authoringToolElement = DomUtils.assertGetSubElementChain(element, "asset", "contributor", "authoring_tool");
        String toolString = DomUtils.getElementText(authoringToolElement);

        if (toolString.startsWith("SketchUp"))
        {
            return TOOL.SKETCHUP;
        }
        else if (toolString.startsWith("Blender"))
        {
            return TOOL.BLENDER;
        }
        else
        {
            throw new XMLParseException("Cannot parse files which are not made by Sketchup or Blender properly");
        }
    }

    public void loadCollada(InputStream input) throws XMLParseException
    {
        Document doc = loadColladaDocument(input);
        Element rootElement = doc.getDocumentElement();
        TOOL tool = parseTool(rootElement);
        Element libraryImagesElement = DomUtils.assertGetSingleSubElement(rootElement, "library_images");
        DomUtils.putSubElementsInMap(imageFileNames, libraryImagesElement, "image", "id", new ColladaLibraryImageFactory());
        Element libraryEffectsElement = DomUtils.assertGetSingleSubElement(rootElement, "library_effects");
        DomUtils.putSubElementsInMap(colladaEffects, libraryEffectsElement, "effect", "id", new ColladaEffectFactory());
        Element libraryMaterialsElement = DomUtils.assertGetSingleSubElement(rootElement, "library_materials");
        DomUtils.putSubElementsInMap(materials, libraryMaterialsElement, "material", "id", new ColladaMaterialFactory(imageFileNames, colladaEffects));
        Element libraryGeometriesElement = DomUtils.assertGetSingleSubElement(rootElement, "library_geometries");
        DomUtils.putSubElementsInMap(geometries, libraryGeometriesElement, "geometry", "id", new ColladaGeometryFactory());

        Element library_nodes = DomUtils.getSingleSubElement(rootElement, "library_nodes");
        if (library_nodes != null)
        {
            ColladaNodeProcessor colladaNodeProcessor = new ColladaNodeProcessor(library_nodes, materials, geometries, tool == TOOL.BLENDER);
            meshGroups.putAll(colladaNodeProcessor.getMeshGroupsMap());

            //Apply all top-level transforms to library objects.
            //It is unlikely there will be any top-level meshgroups with transforms,
            //but if these are referenced and further transformed, this would be a problem.
            for (MeshGroup meshGroup : meshGroups.values())
            {
                meshGroup.applyMatrixTransform(meshGroup.getInitialTransformMatrix());
            }
            //Should not be any top-level annonymous meshes in library and if there are, they can't be referenced, so ignore.
        }
//        Element visual_scene = DomUtils.assertGetSubElementChain(rootElement, "library_visual_scenes","visual_scene");
//        ColladaNodeProcessor colladaNodeProcessor = new ColladaNodeProcessor(visual_scene, materials, geometries, tool==TOOL.BLENDER);

    }

    private Document loadColladaDocument(InputStream input) throws XMLParseException
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document doc = docBuilder.parse(input);
            //validator.validate(new DOMSource(doc));
            return doc;
        }
        catch (ParserConfigurationException e)
        {
            throw new XMLParseException("Parser misconfigured", e);
        }
        catch (IOException e)
        {
            throw new XMLParseException(e);
        }
        catch (SAXException e)
        {
            throw new XMLParseException("XML Parse Exception", e);
        }

    }

    public Map<String, String> getImageFileNames()
    {
        return imageFileNames;
    }

    public Map<String, ColladaEffect> getColladaEffects()
    {
        return colladaEffects;
    }

    public Map<String, Material> getMaterialsByName()
    {
        //Map from material ids to material data
        Map<String, Material> materialsByNameMap = new HashMap<>();
        //Map material's by name
        for (Material material : materials.values())
        {
            materialsByNameMap.put(material.name, material);
        }
        return materialsByNameMap;
    }

    public Map<String, ColladaGeometry> getGeometries()
    {
        return geometries;
    }

    public Map<String, MeshGroup> getMeshGroups()
    {
        return meshGroups;
    }

}
