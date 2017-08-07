package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.utils.dom.XMLParseException;

import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;


/**
 * Tests Collada loading
 * <p>
 * Created by Steve on 2/20/2016.
 */
public class ColladaTests
{
    @Test
    public void doesntCrash() throws XMLParseException, IOException, ParserConfigurationException, SAXException
    {
        com.pheiffware.lib.geometry.collada.TestCollada.doesntCrash();
    }

    @Test
    public void testCompleteLoadBlender() throws XMLParseException, IOException, ParserConfigurationException, SAXException
    {
        com.pheiffware.lib.geometry.collada.TestCollada.testCompleteLoadBlender();
    }

    @Test
    public void testCompleteLoadSketchup() throws XMLParseException, IOException, ParserConfigurationException, SAXException
    {
        com.pheiffware.lib.geometry.collada.TestCollada.testCompleteLoadSketchup();
    }

    @Test
    public void testColladaSource()
    {
        com.pheiffware.lib.geometry.collada.TestCollada.testColladaSource();
    }

    @Test
    public void testColladaMeshNormalizer()
    {
        com.pheiffware.lib.geometry.collada.TestCollada.testColladaMeshNormalizer();
    }
}
