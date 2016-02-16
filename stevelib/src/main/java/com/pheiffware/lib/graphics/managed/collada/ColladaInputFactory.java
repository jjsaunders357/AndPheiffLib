package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.utils.dom.ElementObjectFactory;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Steve on 2/15/2016.
 */
public class ColladaInputFactory implements ElementObjectFactory<ColladaInput>
{
    private final Map<String, ColladaSource> sources;

    public ColladaInputFactory(Map<String, ColladaSource> sources, Integer offset)
    {
        this.sources = sources;
    }

    @Override
    public ColladaInput createFromElement(Element element) throws XMLParseException
    {
        String semantic = element.getAttribute("semantic");
        //Remove '#'
        String sourceKey = element.getAttribute("source").substring(1);
        String offsetString = element.getAttribute("offset");
        int offset;
        if (offsetString.equals(""))
        {
            offset = 0;
        }
        else
        {
            offset = Integer.valueOf(offsetString);
        }
        return new ColladaInput(semantic, sources.get(sourceKey), offset);
    }
}
//Example: <input semantic="NORMAL" source="#Plane-mesh-normals" offset="1"/>