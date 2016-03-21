package com.pheiffware.lib.utils.dom;

import com.pheiffware.lib.R;
import com.pheiffware.lib.graphics.GColor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Validator;

/**
 * Created by Steve on 2/15/2016.
 */
public class DomUtils
{
    /**
     * Loads an DOM XML Document from the given stream and validates it.
     * @param input
     * @return
     * @throws XMLParseException
     */
    public static Document loadDocumentFromStream(InputStream input) throws XMLParseException
    {
        return loadDocumentFromStream(input, null);
    }

    /**
     * Loads an DOM XML Document from the given stream and validates it.
     *
     * @param input
     * @param validator Used to perform validation, if null, no validation happens
     * @return
     * @throws XMLParseException
     */
    public static Document loadDocumentFromStream(InputStream input, Validator validator) throws XMLParseException
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document doc = docBuilder.parse(input);
            if (validator != null)
            {
                validator.validate(new DOMSource(doc));
            }
            return doc;
        }
        catch (ParserConfigurationException e)
        {
            throw new XMLParseException(e);
        }
        catch (IOException e)
        {
            throw new XMLParseException(e);
        }
        catch (SAXException e)
        {
            throw new XMLParseException(e);
        }
    }

    public static Validator createValidator(String assetName)
    {
        //TODO: Fix validator
//         Due to broken, F**KED up, reasons, cannot get Schema factory!
        return null;
//        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//
//        Validator validator;
//        try
//        {
//            Schema schema = schemaFactory.newSchema(Utils.getAssetURL(assetName));
//            return schema.newValidator();
//        }
//        catch (SAXException e)
//        {
//            throw new XMLParseException("Cannot parse schema",e);
//        }
//        catch (MalformedURLException e)
//        {
//            throw new XMLParseException("Collada schema file cannot be found at URL",e);
//        }
    }

    /**
     * Get the text contained within a leaf element's tag (get value from child text node).
     *
     * @param element
     * @return
     */
    public static String getElementText(Element element)
    {
        return element.getFirstChild().getTextContent();
    }

    /**
     * For a given element, this retrieves the child elements with the given tag name. THIS IS NOT RECURSIVE!
     *
     * @param element    The element to search under
     * @param subTagName The tag name to search for
     * @return A list of sub-elements
     */
    public static List<Element> getSubElements(Element element, String subTagName)
    {
        List<Element> subElements = new ArrayList<>();

        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling())
        {
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                Element childElement = (Element) child;
                if (childElement.getTagName().equals(subTagName))
                {
                    subElements.add(childElement);
                }
            }
        }
        return subElements;
    }

    /**
     * Go through an Element which contains sub-elements of a known semantic with "id" attributes.  For each sub-element, turn it into an object with given factory and put it in a map using id as the key.
     *
     * @param map                  Where to store resulting id --> T pairs
     * @param rootElement          The element in which to search for sub-elements
     * @param subTagName           The name of sub-element tags
     * @param keyAttribute         Each subtag should have this attribute which is used as the key to intersert into map.
     * @param elementObjectFactory Given a sub-element, this produces an object of semantic T
     * @param <T>                  The value semantic to store in the map.
     * @throws XMLParseException
     */
    public static <T> void putSubElementsInMap(Map<String, T> map, Element rootElement, String subTagName, String keyAttribute, ElementObjectFactory<T> elementObjectFactory) throws XMLParseException
    {
        for (Node child = rootElement.getFirstChild(); child != null; child = child.getNextSibling())
        {
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                Element childElement = (Element) child;
                if (childElement.getTagName().equals(subTagName))
                {
                    String id = childElement.getAttribute(keyAttribute);
                    T elementObject = elementObjectFactory.createFromElement(childElement);
                    if (elementObject != null)
                    {
                        map.put(id, elementObject);
                    }
                }
            }
        }
    }

    public static Element assertGetSubElementChain(Element element, String... subTagNames) throws XMLParseException
    {
        for (String subTagName : subTagNames)
        {
            element = assertGetSubElement(element, subTagName);
        }
        return element;
    }

    /**
     * Look for a sub-element with given name under given element.  If it doesn't exist throw an exception.
     *
     * @param element        Element to search in
     * @param subTagName name of sub-element to search for
     * @return found sub element
     * @throws XMLParseException Can't find sub-element
     */
    public static Element assertGetSubElement(Element element, String subTagName) throws XMLParseException
    {
        Element childElement = getSubElement(element, subTagName);
        if (childElement == null)
        {
            throw new XMLParseException(element.getTagName() + " did not contain " + subTagName);
        }
        else
        {
            return childElement;
        }
    }

    /**
     * Look for a sub-element with given name under given element.  If it doesn't exist, return null.
     *
     * @param element        Element to search in
     * @param subTagName name of sub-element to search for
     * @return found sub element
     */
    public static Element getSubElement(Element element, String subTagName)
    {
        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling())
        {
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                Element childElement = (Element) child;
                if (childElement.getTagName().equals(subTagName))
                {
                    return childElement;
                }
            }
        }
        return null;
    }

    /**
     * Look inside element for a sub element named "color" and extract its value.  If no sub-element exists, return null.
     *
     * @param element
     * @return float[4]
     * @throws XMLParseException
     */
    public static GColor getColorSubElement(Element element) throws XMLParseException
    {
        Element colorElement = getSubElement(element, "color");
        if (colorElement == null)
        {
            return null;
        }
        float[] components = getFloatsFromElement(colorElement);
        return new GColor(components);
    }

    public static int[] getIntsFromElement(Element element) throws XMLParseException
    {
        NodeList childNodes = element.getChildNodes();
        if (childNodes.getLength() != 1)
        {
            throw new XMLParseException("Node did not have text node child");
        }
        String intsString = childNodes.item(0).getNodeValue();
        String[] arrayOfFloatStrings = intsString.split(" ");
        int[] ints = new int[arrayOfFloatStrings.length];
        for (int i = 0; i < arrayOfFloatStrings.length; i++)
        {
            ints[i] = Integer.valueOf(arrayOfFloatStrings[i]);
        }
        return ints;
    }

    public static short[] getShortsFromElement(Element element) throws XMLParseException
    {
        NodeList childNodes = element.getChildNodes();
        if (childNodes.getLength() != 1)
        {
            throw new XMLParseException("Node did not have text node child");
        }
        String shortsString = childNodes.item(0).getNodeValue();
        String[] arrayOfFloatStrings = shortsString.split(" ");
        short[] shorts = new short[arrayOfFloatStrings.length];
        for (short i = 0; i < arrayOfFloatStrings.length; i++)
        {
            shorts[i] = Short.valueOf(arrayOfFloatStrings[i]);
        }
        return shorts;
    }

    /**
     * Get an array of floats from the text node of an element
     *
     * @param element Element to extract from
     * @return float[]
     * @throws XMLParseException
     */
    public static float[] getFloatsFromElement(Element element) throws XMLParseException
    {
        NodeList childNodes = element.getChildNodes();
        if (childNodes.getLength() != 1)
        {
            throw new XMLParseException("Node did not have text node child");
        }
        String floatsString = childNodes.item(0).getNodeValue();
        String[] arrayOfFloatStrings = floatsString.split(" ");
        float[] floats = new float[arrayOfFloatStrings.length];
        for (int i = 0; i < arrayOfFloatStrings.length; i++)
        {
            floats[i] = Float.valueOf(arrayOfFloatStrings[i]);
        }
        return floats;
    }

    /**
     * Look inside element for a sub element named "color" and extract its value.  If no sub-element exists, return null.
     *
     * @param element
     * @return float
     * @throws XMLParseException
     */
    public static float getFloatSubElement(Element element) throws XMLParseException
    {
        Element floatElement = getSubElement(element, "float");
        if (floatElement == null)
        {
            throw new XMLParseException("Float node did not have sub-element");
        }
        NodeList childNodes = floatElement.getChildNodes();
        if (childNodes.getLength() != 1)
        {
            throw new XMLParseException("Float node did not have text node child");
        }
        String floatString = childNodes.item(0).getNodeValue();
        return Float.valueOf(floatString);
    }
}
