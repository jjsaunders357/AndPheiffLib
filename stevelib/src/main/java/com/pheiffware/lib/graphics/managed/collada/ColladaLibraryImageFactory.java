package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.ElementObjectFactory;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

/**
 * Used to extract image file name from an image element.
 * Created by Steve on 2/15/2016.
 */
class ColladaLibraryImageFactory implements ElementObjectFactory<String>
{
    @Override
    public String createFromElement(Element element) throws XMLParseException
    {
        Element init_from = DomUtils.assertGetSingleSubElement(element, "init_from");
        String imageFileName = init_from.getFirstChild().getTextContent();
        return imageFileName;
    }
}
/*Example:
    <image id="ID13">
        <init_from>test_sk/Steel.png</init_from>
    </image>
 */
