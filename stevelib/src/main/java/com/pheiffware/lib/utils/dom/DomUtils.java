package com.pheiffware.lib.utils.dom;

import com.pheiffware.lib.graphics.GColor;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;

/**
 * Created by Steve on 2/15/2016.
 */
public class DomUtils
{
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
        NodeList nodes = rootElement.getElementsByTagName(subTagName);
        for (int i = 0; i < nodes.getLength(); i++)
        {
            try
            {
                Element element = (Element) nodes.item(i);
                String id = element.getAttribute(keyAttribute);
                T elementObject = elementObjectFactory.createFromElement(element);
                if (elementObject != null)
                {
                    map.put(id, elementObject);
                }
            }
            catch (ClassCastException e)
            {
                throw new XMLParseException(rootElement.getTagName() + " had a sub-node which was not an element");
            }
        }
    }

    /**
     * Look for a sub-element with given name under given element.  If it doesn't exist throw an exception.
     *
     * @param element        Element to search in
     * @param subElementName name of sub-element to search for
     * @return found sub element
     * @throws XMLParseException Can't find sub-element
     */
    public static Element assertGetSingleSubElement(Element element, String subElementName) throws XMLParseException
    {
        NodeList subElementList = element.getElementsByTagName(subElementName);
        if (subElementList.getLength() == 0)
        {
            throw new XMLParseException(element.getTagName() + " did not contain " + subElementName);
        }
        Node subNode = subElementList.item(0);
        if (subNode.getNodeType() != Node.ELEMENT_NODE)
        {
            throw new XMLParseException(element.getTagName() + " had a sub-node which was not an element");
        }
        return (Element) subNode;
    }

    /**
     * Look for a sub-element with given name under given element.  If it doesn't exist, return null.
     *
     * @param element        Element to search in
     * @param subElementName name of sub-element to search for
     * @return found sub element
     * @throws XMLParseException If node is found, but it is not an Element
     */
    public static Element getSingleSubElement(Element element, String subElementName) throws XMLParseException
    {
        NodeList subElementList = element.getElementsByTagName(subElementName);
        if (subElementList.getLength() == 0)
        {
            return null;
        }
        Node subNode = subElementList.item(0);
        if (subNode.getNodeType() != Node.ELEMENT_NODE)
        {
            throw new XMLParseException(element.getTagName() + " had a sub-node which was not an element");
        }
        return (Element) subNode;
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
        Element colorElement = getSingleSubElement(element, "color");
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
        Element floatElement = getSingleSubElement(element, "float");
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
