package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.MeshGroup;
import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * Turns instances of instance_geometry into MeshGroups.  This involves looking up ColladaGeometry and possibly assigning materials.
 * SketchUp files should use the single material specified and apply it to shallow copy of the entire ColladaGeometry which is referred to.
 * Blender files may specify multiple materials and should completely ignore this information.
 * Created by Steve on 2/17/2016.
 */
class InstanceGeometryParser
{
    //
    private final Map<String, ColladaGeometry> geometriesByID;
    //Blender should ignore material assignments here as they will have already been defined and may be ambiguous here.
    private final boolean ignoreMaterialAssignments;
    //All previously loaded materials and a default material under the id ""
    private final Map<String, Material> materialsByID;

    public InstanceGeometryParser(Map<String, Material> materialsByID, Map<String, ColladaGeometry> geometriesByID, boolean ignoreMaterialAssignments)
    {
        this.materialsByID = materialsByID;
        this.geometriesByID = geometriesByID;
        this.ignoreMaterialAssignments = ignoreMaterialAssignments;
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
        if (ignoreMaterialAssignments)
        {
            return colladaGeometry.createMeshGroup(materialsByID);
        }
        else
        {
            Element bind_material = DomUtils.getSubElement(element, "bind_material");
            String materialID;
            if (bind_material != null)
            {
                Element instance_material = DomUtils.assertGetSubElementChain(bind_material, "technique_common", "instance_material");
                materialID = instance_material.getAttribute("target").substring(1);
            }
            else
            {
                materialID = "";
            }
            Material material = materialsByID.get(materialID);
            return colladaGeometry.createMeshGroup(material);
        }
    }

}
/*Example:
    <instance_geometry url="#ID76">
        <bind_material>
            <technique_common>
                <instance_material symbol="Material2" target="#ID6">
                    <bind_vertex_input semantic="UVSET0" input_semantic="TEXCOORD" input_set="0" />
                </instance_material>
            </technique_common>
        </bind_material>
    </instance_geometry>
 */