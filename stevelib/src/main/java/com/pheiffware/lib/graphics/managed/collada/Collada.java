package com.pheiffware.lib.graphics.managed.collada;

import android.content.res.AssetManager;

import com.pheiffware.lib.graphics.FatalGraphicsException;
import com.pheiffware.lib.graphics.GColor;
import com.pheiffware.lib.graphics.managed.mesh.Material;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
    private final Map<String, Material> materials = new HashMap<>();


    public void loadCollada(AssetManager assetManager, String assetFileName) throws ColladaParseException
    {
        InputStream input = null;
        try
        {
            input = assetManager.open(assetFileName);
        }
        catch (IOException e)
        {
            throw new ColladaParseException(e);
        }
        loadCollada(input);
    }

    public void loadCollada(InputStream input) throws ColladaParseException
    {
        Document doc = loadColladaDocument(input);
        Element libraryImagesElement = assertGetSingleSubElement(doc.getDocumentElement(), "library_images");
        loadMapFromElement(imageFileNames, libraryImagesElement, "image", new ColladaLibraryImageFactory());
        Element libraryEffectsElement = assertGetSingleSubElement(doc.getDocumentElement(), "library_effects");
        loadMapFromElement(effects, libraryEffectsElement, "effect", new ColladaEffectFactory());
        Element libraryMaterialsElement = assertGetSingleSubElement(doc.getDocumentElement(), "library_materials");
        loadMapFromElement(materials, libraryMaterialsElement, "material", new ColladaMaterialFactory(imageFileNames, effects));
    }

    private Document loadColladaDocument(InputStream input) throws ColladaParseException
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
            throw new ColladaParseException("Parser misconfigured", e);
        }
        catch (IOException e)
        {
            throw new ColladaParseException(e);
        }
        catch (SAXException e)
        {
            throw new ColladaParseException("XML Parse Exception", e);
        }

    }


    /**
     * Go through an Element which contains sub-elements of a known type with "id" attributes.  For each sub-element, turn it into an object with given factory and put it in a map using id as the key.
     *
     * @param map                  Where to store resulting id --> T pairs
     * @param rootElement          The element in which to search for sub-elements
     * @param subTagName           The name of sub-element tags
     * @param elementObjectFactory Given a sub-element, this produces an object of type T
     * @param <T>                  The value type to store in the map.
     * @throws ColladaParseException
     */
    public static <T> void loadMapFromElement(Map<String, T> map, Element rootElement, String subTagName, ElementObjectFactory<T> elementObjectFactory) throws ColladaParseException
    {
        NodeList nodes = rootElement.getElementsByTagName(subTagName);
        for (int i = 0; i < nodes.getLength(); i++)
        {
            try
            {
                Element element = (Element) nodes.item(i);
                String id = element.getAttribute("id");
                T elementObject = elementObjectFactory.createFromElement(id, element);
                map.put(id, elementObject);
            }
            catch (ClassCastException e)
            {
                throw new ColladaParseException(rootElement.getTagName() + " had a sub-node which was not an element");
            }
        }
    }

    /**
     * Look for a sub-element with given name under given element.  If it doesn't exist throw an exception.
     *
     * @param element        Element to search in
     * @param subElementName name of sub-element to search for
     * @return found sub element
     * @throws ColladaParseException Can't find sub-element
     */
    public static Element assertGetSingleSubElement(Element element, String subElementName) throws ColladaParseException
    {
        NodeList subElementList = element.getElementsByTagName(subElementName);
        if (subElementList.getLength() == 0)
        {
            throw new ColladaParseException(element.getTagName() + " did not contain " + subElementName);
        }
        Node subNode = subElementList.item(0);
        if (subNode.getNodeType() != Node.ELEMENT_NODE)
        {
            throw new ColladaParseException(element.getTagName() + " had a sub-node which was not an element");
        }
        return (Element) subNode;
    }

    /**
     * Look for a sub-element with given name under given element.  If it doesn't exist, return null.
     *
     * @param element        Element to search in
     * @param subElementName name of sub-element to search for
     * @return found sub element
     * @throws ColladaParseException If node is found, but it is not an Element
     */
    public static Element getSingleSubElement(Element element, String subElementName) throws ColladaParseException
    {
        NodeList subElementList = element.getElementsByTagName(subElementName);
        if (subElementList.getLength() == 0)
        {
            return null;
        }
        Node subNode = subElementList.item(0);
        if (subNode.getNodeType() != Node.ELEMENT_NODE)
        {
            throw new ColladaParseException(element.getTagName() + " had a sub-node which was not an element");
        }
        return (Element) subNode;
    }

    /**
     * Look inside element for a sub element named "color" and extract its value.  If no sub-element exists, return null.
     *
     * @param element
     * @return float[4]
     * @throws ColladaParseException
     */
    public static GColor getColorSubElement(Element element) throws ColladaParseException
    {
        Element colorElement = getSingleSubElement(element, "color");
        if (colorElement == null)
        {
            return null;
        }
        NodeList childNodes = colorElement.getChildNodes();
        if (childNodes.getLength() != 1)
        {
            throw new ColladaParseException("Color node did not have text node child");
        }
        String colorString = childNodes.item(0).getNodeValue();
        String[] rgbaString = colorString.split(" ");
        float[] color = new float[4];
        color[0] = Float.valueOf(rgbaString[0]);
        color[1] = Float.valueOf(rgbaString[1]);
        color[2] = Float.valueOf(rgbaString[2]);
        color[3] = Float.valueOf(rgbaString[3]);
        return new GColor(color);
    }

    /**
     * Look inside element for a sub element named "color" and extract its value.  If no sub-element exists, return null.
     *
     * @param element
     * @return float
     * @throws ColladaParseException
     */
    public static float getFloatSubElement(Element element) throws ColladaParseException
    {
        Element floatElement = getSingleSubElement(element, "float");
        if (floatElement == null)
        {
            throw new ColladaParseException("Float node did not have sub-element");
        }
        NodeList childNodes = floatElement.getChildNodes();
        if (childNodes.getLength() != 1)
        {
            throw new ColladaParseException("Float node did not have text node child");
        }
        String floatString = childNodes.item(0).getNodeValue();
        return Float.valueOf(floatString);
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
