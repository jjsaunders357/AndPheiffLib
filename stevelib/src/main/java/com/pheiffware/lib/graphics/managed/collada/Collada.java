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
    private final Map<String, Material> effects = new HashMap<>();
    //Map from material ids to material data
    private final Map<String, Material> materials = new HashMap<>();

    /**
     * Look for a sub-element with given name under given element.  If it doesn't exist throw an exception.
     *
     * @param element        Element to search in
     * @param subElementName name of sub-element to search for
     * @return found sub element
     * @throws ColladaParseException Can't find sub-element
     */
    private static Element assertGetSingleSubElement(Element element, String subElementName) throws ColladaParseException
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
    private static Element getSingleSubElement(Element element, String subElementName) throws ColladaParseException
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
    private static GColor getColorSubElement(Element element) throws ColladaParseException
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
    private static float getFloatSubElement(Element element) throws ColladaParseException
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

    private <T> void loadMapFromElement(Map<String, T> map, Element rootElement, String subTagName, ElementObjectFactory<T> elementObjectFactory) throws ColladaParseException
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


    public void loadCollada(AssetManager assetManager, String assetFileName) throws FatalGraphicsException, ColladaParseException
    {
        Document doc = null;
        try
        {
            doc = loadColladaDocument(assetManager, assetFileName);
        }
        catch (SAXException e)
        {
            throw new ColladaParseException("XML Parse Exception", e);
        }
        Element libraryImagesElement = assertGetSingleSubElement(doc.getDocumentElement(), "library_images");
        loadMapFromElement(imageFileNames, libraryImagesElement, "image", new ColladaImageFileNameFactory());
        Element libraryEffectsElement = assertGetSingleSubElement(doc.getDocumentElement(), "library_effects");
        loadMapFromElement(effects, libraryEffectsElement, "effect", new ColladaEffectFactory());
        Element libraryMaterialsElement = assertGetSingleSubElement(doc.getDocumentElement(), "library_materials");
        loadMapFromElement(materials, libraryEffectsElement, "material", new ColladaMaterialFactory());
    }

    private Document loadColladaDocument(AssetManager assetManager, String assetFileName) throws FatalGraphicsException, SAXException
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document doc = docBuilder.parse(assetManager.open(assetFileName));
            //validator.validate(new DOMSource(doc));
            return doc;
        }
        catch (ParserConfigurationException e)
        {
            throw new FatalGraphicsException("Parser misconfigured", e);
        }
        catch (IOException e)
        {
            throw new FatalGraphicsException(e);
        }

    }


    private interface ElementObjectFactory<T>
    {
        T createFromElement(String id, Element element) throws ColladaParseException;
    }

    /**
     * Used to extract actual image file names from the library_images tag.
     */
    private class ColladaImageFileNameFactory implements ElementObjectFactory<String>
    {
        @Override
        public String createFromElement(String id, Element element) throws ColladaParseException
        {
            /*
            Example
            <image id="ID13">
                <init_from>test_sk/Steel.png</init_from>
            </image>
             */
            Element init_from = assertGetSingleSubElement(element, "init_from");
            String imageFileName = init_from.getFirstChild().getTextContent();
            return imageFileName;
        }
    }

    /**
     * Used to extract "effects", which are equivalent to materials for our purposes from library_effects
     */
    private class ColladaEffectFactory implements ElementObjectFactory<Material>
    {
        @Override
        public Material createFromElement(String id, Element element) throws ColladaParseException
        {
            /*
                Sketchup effect example
                <effect id="ID12">
                    <profile_COMMON>
                        <newparam sid="ID14">
                            <surface type="2D">
                                <init_from>ID13</init_from>   ////This id is an image reference
                            </surface>
                        </newparam>
                        <newparam sid="ID15">
                            <sampler2D>
                                <source>ID14</source>
                            </sampler2D>
                        </newparam>
                        <technique sid="COMMON">
                            <lambert>
                                <diffuse>
                                    <texture texture="ID15" texcoord="UVSET0" />   ////This is either a texture or a color.  If a color then the above "newparam" won't be present.
                                </diffuse>
                            </lambert>
                        </technique>
                    </profile_COMMON>
                </effect>

                Blender effect example
                <effect id="Steel-effect">
                  <profile_COMMON>
                    <newparam sid="steel_png-surface">
                      <surface type="2D">
                        <init_from>steel_png</init_from>
                      </surface>
                    </newparam>
                    <newparam sid="steel_png-sampler">
                      <sampler2D>
                        <source>steel_png-surface</source>
                      </sampler2D>
                    </newparam>
                    <technique sid="common">
                      <phong>
                        <emission>
                          <color sid="emission">0 0 0 1</color>
                        </emission>
                        <ambient>
                          <color sid="ambient">0 0 0 1</color>
                        </ambient>
                        <diffuse>
                          <texture texture="steel_png-sampler" texcoord="UVMap"/>
                        </diffuse>
                        <specular>
                          <color sid="specular">0.5 0.5 0.5 1</color>
                        </specular>
                        <shininess>
                          <float sid="shininess">50</float>
                        </shininess>
                        <index_of_refraction>
                          <float sid="index_of_refraction">1</float>
                        </index_of_refraction>
                      </phong>
                    </technique>
                  </profile_COMMON>
                </effect>
             */
            Element profileCommon = assertGetSingleSubElement(element, "profile_COMMON");
            String imageFileName = getImageFileName(profileCommon);


            Element technique = assertGetSingleSubElement(profileCommon, "technique");
            Element phong = getSingleSubElement(technique, "phong");
            GColor ambientColor;
            GColor diffuseColor;
            GColor specularColor;
            float shininess;

            if (phong != null)
            {
                ambientColor = getColorSubElement(assertGetSingleSubElement(phong, "ambient"));
                diffuseColor = getColorSubElement(assertGetSingleSubElement(phong, "diffuse"));
                specularColor = getColorSubElement(assertGetSingleSubElement(phong, "specular"));
                shininess = getFloatSubElement(assertGetSingleSubElement(phong, "shininess"));
            }
            else
            {
                Element lambert = assertGetSingleSubElement(technique, "lambert");
                ambientColor = new GColor(1.0f, 1.0f, 1.0f, 1.0f);
                diffuseColor = getColorSubElement(assertGetSingleSubElement(lambert, "diffuse"));
                specularColor = new GColor(1.0f, 1.0f, 1.0f, 1.0f);
                shininess = 1;
            }
            return new Material(imageFileName, ambientColor, diffuseColor, specularColor, shininess);
        }

        private String getImageFileName(Element technique) throws ColladaParseException
        {
            //Extract image reference or null if this technique does not contain an image
            NodeList newparamNodes = technique.getElementsByTagName("newparam");
            for (int i = 0; i < newparamNodes.getLength(); i++)
            {
                Element newparamElement = (Element) newparamNodes.item(i);
                Element surface = getSingleSubElement(newparamElement, "surface");
                if (surface != null)
                {
                    String type = surface.getAttribute("type");
                    if (type.equals("2D"))
                    {
                        Element init_from = getSingleSubElement(newparamElement, "init_from");
                        String imageReference = init_from.getFirstChild().getTextContent();
                        String imageFileName = imageFileNames.get(imageReference);
                        return imageFileName;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Used to extract material objects from within library_materials tag.
     */
    private class ColladaMaterialFactory implements ElementObjectFactory<Material>
    {
        @Override
        public Material createFromElement(String id, Element element) throws ColladaParseException
        {
            /*
            <material id="ID4" name="material">
                <instance_effect url="#ID3" />
            </material>
            */
            Element instance_effect = assertGetSingleSubElement(element, "instance_effect");
            String url = instance_effect.getAttribute("url");
            String effectKey = url.substring(1);
            return effects.get(effectKey);
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
