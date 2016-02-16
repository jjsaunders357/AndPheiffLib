package com.pheiffware.lib.graphics.managed.collada;

import org.w3c.dom.Element;

/**
 * Used to extract image file name from an <image></image> tag.
 * Created by Steve on 2/15/2016.
 */
public class ColladaLibraryImageFactory implements ElementObjectFactory<String>
{
    @Override
    public String createFromElement(Element element) throws ColladaParseException
    {
        Element init_from = Collada.assertGetSingleSubElement(element, "init_from");
        String imageFileName = init_from.getFirstChild().getTextContent();
        return imageFileName;
    }
}
/*Example:
    <image id="ID13">
        <init_from>test_sk/Steel.png</init_from>
    </image>
 */
