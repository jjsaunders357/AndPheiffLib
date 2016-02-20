package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

/**
 * What tool was used to create a Collada file?  There a few peculiar differences between BLENDER and SKETCHUP
 * Created by Steve on 2/20/2016.
 */
enum ColladaAuthoringSoftware
{
    BLENDER, SKETCHUP;

    static ColladaAuthoringSoftware parse(Element element) throws XMLParseException
    {
        Element authoringToolElement = DomUtils.assertGetSubElementChain(element, "asset", "contributor", "authoring_tool");
        String toolString = DomUtils.getElementText(authoringToolElement);

        if (toolString.startsWith("SketchUp"))
        {
            return ColladaAuthoringSoftware.SKETCHUP;
        }
        else if (toolString.startsWith("Blender"))
        {
            return ColladaAuthoringSoftware.BLENDER;
        }
        else
        {
            throw new XMLParseException("Cannot parse files which are not made by Sketchup or Blender properly");
        }

    }
}
