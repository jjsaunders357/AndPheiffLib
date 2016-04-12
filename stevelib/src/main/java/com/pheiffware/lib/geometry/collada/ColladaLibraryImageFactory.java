package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.ElementObjectFactory;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

/**
 * Used to extract image file name from an image element.  This always removes any leading path, as the assumption is that all files will be loaded from a single directory.
 * <p/>
 * Created by Steve on 2/15/2016.
 */
class ColladaLibraryImageFactory implements ElementObjectFactory<String>
{
    @Override
    public String createFromElement(Element element) throws XMLParseException
    {
        Element init_from = DomUtils.assertGetSubElement(element, "init_from");
        String imageFileName = init_from.getFirstChild().getTextContent();
        String[] split = imageFileName.split("/");
        imageFileName = split[split.length - 1];
        return imageFileName;
    }
}
/*Example:
    <image id="ID13">
        <init_from>ignore/this/path/Steel.png</init_from>
    </image>
 */
