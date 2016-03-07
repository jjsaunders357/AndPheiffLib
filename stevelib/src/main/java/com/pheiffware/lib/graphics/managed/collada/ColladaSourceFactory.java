package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.ElementObjectFactory;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

/**
 * Created by Steve on 2/15/2016.
 */
class ColladaSourceFactory implements ElementObjectFactory<ColladaSource>
{
    @Override
    public ColladaSource createFromElement(Element element) throws XMLParseException
    {
        Element techniqueCommon = DomUtils.getSubElement(element, "technique_common");
        if (techniqueCommon == null)
        {
            return null;
        }


        Element floatsElement = DomUtils.assertGetSubElement(element, "float_array");
        String floatsID = floatsElement.getAttribute("id");
        float[] rawFloats = DomUtils.getFloatsFromElement(floatsElement);

        Element accessorElement = DomUtils.assertGetSubElement(techniqueCommon, "accessor");
        ColladaAccessor accessor = new ColladaAccessor(accessorElement);


        return new ColladaSource(accessor.count, accessor.stride, accessor.removeUnusedData(rawFloats));
    }

}

/*Example:
<source id="CubeTexMesh-mesh-positions">
  <float_array id="CubeTexMesh-mesh-positions-array" count="24">-1 -1 -1 -1 -1 1 -1 1 -1 -1 1 1 1 -1 -1 1 -1 1 1 1 -1 1 1 1</float_array>
  <technique_common>
    <accessor source="#CubeTexMesh-mesh-positions-array" count="8" stride="3">
      <param name="X" semantic="float"/>
      <param name="Y" semantic="float"/>
      <param name="Z" semantic="float"/>
    </accessor>
  </technique_common>
</source>
 */