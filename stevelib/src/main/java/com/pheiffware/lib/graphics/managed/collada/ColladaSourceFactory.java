package com.pheiffware.lib.graphics.managed.collada;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Created by Steve on 2/15/2016.
 */
public class ColladaSourceFactory implements ElementObjectFactory<ColladaSource>
{
    @Override
    public ColladaSource createFromElement(Element element) throws ColladaParseException
    {
        Element techniqueCommon = Collada.getSingleSubElement(element, "technique_common");
        if (techniqueCommon == null)
        {
            return null;
        }


        Element floatsElement = Collada.assertGetSingleSubElement(element, "float_array");
        String floatsID = floatsElement.getAttribute("id");
        float[] rawFloats = Collada.getFloatsFromElement(floatsElement);

        Element accessorElement = Collada.assertGetSingleSubElement(element, "accessor");
        ColladaAccessor accessor = new ColladaAccessor(accessorElement);


        return new ColladaSource(accessor.count, accessor.stride, accessor.collateData(rawFloats));
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