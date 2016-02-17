package com.pheiffware.lib.graphics.managed.collada;

import android.content.res.AssetManager;

import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.MeshGroup;
import com.pheiffware.lib.graphics.managed.mesh.Object3D;
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
    private static final Validator validator = createValidator();

    //Map from image ids to file names
    private final Map<String, String> imageFileNames = new HashMap<>();
    //Map from effect ids to effect data (effect data is identical to materials)
    private final Map<String, ColladaEffect> effects = new HashMap<>();

    //Map from material ids to material data
    private final Map<String, Material> materialsByIDMap = new HashMap<>();

    //Map from material ids to material data
    private final Map<String, Material> materialsByNameMap = new HashMap<>();

    //Map from mesh ids to meshes
    private Map<String, MeshGroup> meshCollection3Ds = new HashMap<>();

    //Holds actual objects
    private Map<String, Object3D> objects = new HashMap<>();

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

    public void loadCollada(InputStream input) throws XMLParseException
    {
        Document doc = loadColladaDocument(input);
        Element libraryImagesElement = DomUtils.assertGetSingleSubElement(doc.getDocumentElement(), "library_images");
        DomUtils.putSubElementsInMap(imageFileNames, libraryImagesElement, "image", "id", new ColladaLibraryImageFactory());
        Element libraryEffectsElement = DomUtils.assertGetSingleSubElement(doc.getDocumentElement(), "library_effects");
        DomUtils.putSubElementsInMap(effects, libraryEffectsElement, "effect", "id", new ColladaEffectFactory());
        Element libraryMaterialsElement = DomUtils.assertGetSingleSubElement(doc.getDocumentElement(), "library_materials");
        DomUtils.putSubElementsInMap(materialsByIDMap, libraryMaterialsElement, "material", "id", new ColladaMaterialFactory(imageFileNames, effects));
        Element libraryGeometriesElement = DomUtils.assertGetSingleSubElement(doc.getDocumentElement(), "library_geometries");
        DomUtils.putSubElementsInMap(meshCollection3Ds, libraryGeometriesElement, "geometry", "id", new ColladaMeshGroupFactory(materialsByIDMap));

        Element library_visual_scenes = DomUtils.assertGetSingleSubElement(doc.getDocumentElement(), "library_visual_scenes");
        Element visual_scene = DomUtils.assertGetSingleSubElement(library_visual_scenes, "visual_scene");
        ColladaNodeProcessor colladaNodeProcessor = new ColladaNodeProcessor(visual_scene);

        //Map material's by name
        for (Material material : materialsByIDMap.values())
        {
            materialsByNameMap.put(material.name, material);
        }
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


    private static Validator createValidator()
    {
        return null;
        // Due to broken, F**KED up, reasons, cannot get Schema factory!
//        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//
//        Validator validator;
//        try
//        {
//            Schema schema = schemaFactory.newSchema(Utils.getAssetURL("meshes\\collada_schema_1_4_1.xsd"));
//            return schema.newValidator();
//        }
//        catch (SAXException e)
//        {
//            throw new FatalGraphicsException("Cannot parse schema for Collada files",e);
//        }
//        catch (MalformedURLException e)
//        {
//            throw new FatalGraphicsException("Collada schema file cannot be found at URL",e);
//        }
    }


}
