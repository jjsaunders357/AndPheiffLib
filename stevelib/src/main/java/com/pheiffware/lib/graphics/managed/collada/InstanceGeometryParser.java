package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.graphics.managed.collada.ColladaGeometry;
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
    private final Map<String, ColladaGeometry> geometries;
    private final boolean ignoreMaterialAssignments;
    private final Map<String, Material> materials;

    public InstanceGeometryParser(Map<String, Material> materials, Map<String, ColladaGeometry> geometries, boolean ignoreMaterialAssignments)
    {
        this.materials = materials;
        this.geometries = geometries;
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
        ColladaGeometry colladaGeometry = geometries.get(geometryID);
        if (colladaGeometry == null)
        {
            throw new XMLParseException("Undefined geometry id referenced: " + geometryID);
        }
        if (ignoreMaterialAssignments)
        {
            return colladaGeometry.createMeshGroup(materials);
        }
        else
        {
            Element instance_material = DomUtils.assertGetSubElementChain(element, "bind_material", "technique_common", "instance_material");
            String materialID = instance_material.getAttribute("target").substring(1);
            Material material = materials.get(materialID);
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