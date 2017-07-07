package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Turns instances of instance_geometry into MeshGroups.
 *
 * "url" references the geometry tag defined elsewhere in the file.
 * "target" references the ID of a material defined in the collada file.
 * "symbol" references the local material ID defined in the geometry this references.
 *
 * For each tag parsed, an individual map is built which maps local material IDs to globally (file) defined materials.
 * Created by Steve on 2/17/2016.
 */
class InstanceGeometryParser
{
    //
    private final Map<String, ColladaGeometry> geometriesByID;
    //All previously loaded materials and a default material under the id ""
    private final Map<String, ColladaMaterial> materialsByID;

    public InstanceGeometryParser(Map<String, ColladaMaterial> materialsByID, Map<String, ColladaGeometry> geometriesByID)
    {
        this.materialsByID = materialsByID;
        this.geometriesByID = geometriesByID;
    }

    /**
     * Turns an instance_geometry element into a useable MeshGroup
     *
     * @param element instance_geometry
     * @return new MeshGroup object with actual material object references
     * @throws XMLParseException
     */
    public MeshGroup parseInstanceGeometry(Element element) throws XMLParseException
    {
        String geometryID = element.getAttribute("url").substring(1);
        ColladaGeometry colladaGeometry = geometriesByID.get(geometryID);
        if (colladaGeometry == null)
        {
            throw new XMLParseException("Undefined geometry id referenced: " + geometryID);
        }
        Element technique_common = DomUtils.getSubElementChain(element, "bind_material", "technique_common");

        //Build a map from the geometry's local material naming scheme to the global naming scheme for the whole file
        Map<String, ColladaMaterial> localMaterialMap = new HashMap<>();
        localMaterialMap.put("", materialsByID.get(""));
        if (technique_common != null)
        {
            List<Element> instance_materials = DomUtils.getSubElements(technique_common, "instance_material");
            for (Element instance_material : instance_materials)
            {
                String realMaterialID = instance_material.getAttribute("target").substring(1);
                ColladaMaterial material = materialsByID.get(realMaterialID);
                String geoLocalMaterialID = instance_material.getAttribute("symbol");
                localMaterialMap.put(geoLocalMaterialID, material);
            }
        }

        return colladaGeometry.createMeshGroup(localMaterialMap);
    }

}
/*Example, with multiple bound materials:
<instance_geometry url="#CubeTexMesh-mesh" name="JoinedCubes">
  <bind_material>
    <technique_common>
      <instance_material symbol="Steel-material" target="#Steel-material">
        <bind_vertex_input semantic="UVMap" input_semantic="TEXCOORD" input_set="0"/>
      </instance_material>
      <instance_material symbol="Purple-material" target="#Purple-material">
        <bind_vertex_input semantic="UVMap" input_semantic="TEXCOORD" input_set="0"/>
      </instance_material>
    </technique_common>
  </bind_material>
</instance_geometry>
 */