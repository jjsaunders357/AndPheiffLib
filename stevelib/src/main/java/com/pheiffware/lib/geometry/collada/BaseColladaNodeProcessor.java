package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.MeshGroup;
import com.pheiffware.lib.graphics.utils.MathUtils;
import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Used to process all nodes under library_nodes and visual_scene elements.  Note: in SketchUp a top-level "SketchUp" node is what is parsed on visual_scene_elements.
 * Created by Steve on 2/16/2016.
 */
abstract class BaseColladaNodeProcessor
{
    //Used to parse any instance geometry elements encountered
    private final InstanceGeometryParser instanceGeometryParser;

    //Any nodes encountered are mapped by id here for access later.  Additionally, library MeshGroups may be added to this for reference
    protected final Map<String, MeshGroupProxy> meshGroupProxies = new HashMap<>();

    /**
     * @param materialsByID             library of materials, mapped by id which may be looked up.
     * @param geometriesByID                a map from ids to ColladaGeometries
     * @param ignoreMaterialAssignments if parsing blender, materials will already have been assigned inside ColladaGeometries and what is encountered in this node structure is ambiguous and should be ignored.
     * @throws XMLParseException
     */
    public BaseColladaNodeProcessor(Map<String, Material> materialsByID, Map<String, ColladaGeometry> geometriesByID, boolean ignoreMaterialAssignments) throws XMLParseException
    {
        instanceGeometryParser = new InstanceGeometryParser(materialsByID, geometriesByID, ignoreMaterialAssignments);
    }

    /**
     * This recursively descends through a node hierarchy composed of 3 types of nodes:
     * 1. nodes: Possibly include initial matrix transformation information and will contain other nodes
     * 2. geometry-instances: Reference to previously parsed geometry data.  These are always leaves.
     * 3. node-instances: References to other nodes.  These only occur under library_nodes elements.  These are always leaves.
     *
     * All nodes are added to the meshGroupProxies map by id as these may be referenced when meshGroupProxies are accessed later to build meshes from other nodes on the fly.
     * @param element the element to parse
     * @return list of mesh group proxies, these are actually backed by multiple types of of objects described above
     * @throws XMLParseException
     */
    protected List<MeshGroupProxy> getMeshGroupProxies(Element element) throws XMLParseException
    {
        List<MeshGroupProxy> meshGroupProxies = new LinkedList<>();
        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling())
        {
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                Element childElement = (Element) child;
                if (childElement.getTagName().equals("node"))
                {
                    MeshGroupProxy meshGroupProxy = processNodeElementAndRegister(childElement);
                    meshGroupProxies.add(meshGroupProxy);

                }
                else if (childElement.getTagName().equals("instance_node"))
                {
                    String referenceNodeID = childElement.getAttribute("url").substring(1);
                    MeshGroupProxy meshGroupProxy = new ColladaNodeInstance(referenceNodeID);
                    meshGroupProxies.add(meshGroupProxy);
                }
                else if (childElement.getTagName().equals("instance_geometry"))
                {
                    MeshGroup meshGroup = instanceGeometryParser.parseInstanceGeometry(childElement);
                    meshGroupProxies.add(new DirectMeshGroupProxy(meshGroup));
                }
            }
        }
        return meshGroupProxies;
    }

    /**
     * Handles parsing/processing of any node elements encountered.
     * @param element
     * @return
     * @throws XMLParseException
     */
    private ColladaNode processNodeElementAndRegister(Element element) throws XMLParseException
    {
        Element matrixElement = DomUtils.getSubElement(element, "matrix");
        float[] transformMatrix;
        if (matrixElement != null)
        {
            transformMatrix = MathUtils.createTransposeMatrix(DomUtils.getFloatsFromElement(matrixElement));
        }
        else
        {
            transformMatrix = MathUtils.IDENTITY_MATRIX4;
        }
        String id = element.getAttribute("id");
        String name = element.getAttribute("name");
        if (name.equals(""))
        {
            name = null;
        }
        ColladaNode colladaNode = new ColladaNode(id, name, transformMatrix);
        List<MeshGroupProxy> meshGroupProxies = getMeshGroupProxies(element);
        colladaNode.addMeshRetrievables(meshGroupProxies);
        this.meshGroupProxies.put(id, colladaNode);
        return colladaNode;
    }

    /**
     * Created to handle any instanceNodes encountered.  These are what make the whole process difficult.
     * They reference other nodes in the hierarchy, which you may not even have parsed yet.
     * When retrieveMeshGroup is called, these do the lookup then inside meshgroupproxies.
     */
    private class ColladaNodeInstance implements MeshGroupProxy
    {
        private final String referenceNodeID;

        public ColladaNodeInstance(String referenceNodeID)
        {
            this.referenceNodeID = referenceNodeID;
        }

        @Override
        public String getID()
        {
            return null;
        }

        @Override
        public String getName()
        {
            return null;
        }

        @Override
        public float[] getTransform()
        {
            return MathUtils.IDENTITY_MATRIX4;
        }

        @Override
        public MeshGroup retrieveMeshGroup(boolean flatten)
        {
            MeshGroupProxy meshGroupProxy = meshGroupProxies.get(referenceNodeID);
            return meshGroupProxy.retrieveMeshGroup(true);
        }
    }
}
/*
SketchUp example which uses weird reference nodes:
    <library_nodes>
        <node id="ID20" name="Copy_First_Cube_Component">
            <instance_geometry url="#ID21">
                <bind_material>
                    <technique_common>
                        <instance_material symbol="Material2" target="#ID6">
                            <bind_vertex_input semantic="UVSET0" input_semantic="TEXCOORD" input_set="0" />
                        </instance_material>
                    </technique_common>
                </bind_material>
            </instance_geometry>
        </node>
        <node id="ID28" name="TestMultiComponent">
            <instance_geometry url="#ID29">
                <bind_material>
                    <technique_common>
                        <instance_material symbol="Material2" target="#ID30">
                            <bind_vertex_input semantic="UVSET0" input_semantic="TEXCOORD" input_set="0" />
                        </instance_material>
                    </technique_common>
                </bind_material>
            </instance_geometry>
            <instance_geometry url="#ID42">
                <bind_material>
                    <technique_common>
                        <instance_material symbol="Material2" target="#ID43">
                            <bind_vertex_input semantic="UVSET0" input_semantic="TEXCOORD" input_set="0" />
                        </instance_material>
                    </technique_common>
                </bind_material>
            </instance_geometry>
        </node>
        <node id="ID52" name="CubeGroupComponent">
            <node id="ID53" name="BlahCube">
                <matrix>1 0 0 0 0 1 0 36.64123 0 0 1 0 0 0 0 1</matrix>
                <instance_node url="#ID54" />
            </node>
            <node id="ID61" name="SteelCube">
                <matrix>1 0 0 289.768 0 1 0 0 0 0 1 0 0 0 0 1</matrix>
                <instance_node url="#ID62" />
            </node>
        </node>
        <node id="ID54" name="BlahCube">
            <instance_geometry url="#ID55">
                <bind_material>
                    <technique_common>
                        <instance_material symbol="Material2" target="#ID43">
                            <bind_vertex_input semantic="UVSET0" input_semantic="TEXCOORD" input_set="0" />
                        </instance_material>
                    </technique_common>
                </bind_material>
            </instance_geometry>
        </node>
        <node id="ID62" name="SteelCube">
            <instance_geometry url="#ID63">
                <bind_material>
                    <technique_common>
                        <instance_material symbol="Material2" target="#ID64">
                            <bind_vertex_input semantic="UVSET0" input_semantic="TEXCOORD" input_set="0" />
                        </instance_material>
                    </technique_common>
                </bind_material>
            </instance_geometry>
        </node>
    </library_nodes>
 */