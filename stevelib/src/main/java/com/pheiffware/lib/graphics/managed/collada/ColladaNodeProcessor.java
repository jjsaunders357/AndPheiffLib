package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.MeshGroup;
import com.pheiffware.lib.graphics.utils.MathUtils;
import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Given an element which contains nodes (and possibly geometry instances) process all sub-nodes recursively to
 * create a map from ids to complete MeshGroups, with all materials properly assigned.  This completely flattens
 * the hierarchy at the top level.  Sub-nodes/geometry instances are just rolled into parent MeshGroups.
 * Created by Steve on 2/16/2016.
 */
public class ColladaNodeProcessor
{
    private final Map<String, Material> materials;
    private final InstanceGeometryParser instanceGeometryParser;
    private final Map<String, IncompleteNode> incompleteNodeMap = new HashMap<>();
    private final Map<String, MeshGroup> meshGroupsMap = new HashMap<>();
    private final List<MeshGroup> annonymousMeshGroups = new ArrayList<>();

    public ColladaNodeProcessor(Element element, Map<String, Material> materials, Map<String, ColladaGeometry> geometries, boolean ignoreMaterialAssignments) throws XMLParseException
    {
        this.materials = materials;
        instanceGeometryParser = new InstanceGeometryParser(materials, geometries, ignoreMaterialAssignments);
        List<MeshGroupRetrievable> topLevelMeshGroupRetrievables = getMeshGroupRetrievables(element, true);
        for (MeshGroupRetrievable topLevelMeshGroupRetrievable : topLevelMeshGroupRetrievables)
        {
            if (!topLevelMeshGroupRetrievable.hasId())
            {
                annonymousMeshGroups.add(topLevelMeshGroupRetrievable.retrieveMeshGroup());
            }
        }
        registerAllNamedMeshGroups();
    }

    private void registerAllNamedMeshGroups()
    {
        for (Map.Entry<String, IncompleteNode> entry : incompleteNodeMap.entrySet())
        {
            IncompleteNode incompleteNode = entry.getValue();
            if (incompleteNode.topLevel)
            {
                MeshGroup meshGroup = incompleteNode.retrieveMeshGroup();
                meshGroupsMap.put(entry.getKey(), meshGroup);
            }
        }
    }

    private List<MeshGroupRetrievable> getMeshGroupRetrievables(Element element, boolean topLevel) throws XMLParseException
    {
        List<MeshGroupRetrievable> meshGroupRetrievables = new LinkedList<>();
        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling())
        {
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                Element childElement = (Element) child;
                if (childElement.getTagName().equals("node"))
                {
                    MeshGroupRetrievable meshGroupRetrievable = processNodeElementAndRegister(childElement, topLevel);
                    meshGroupRetrievables.add(meshGroupRetrievable);

                }
                else if (childElement.getTagName().equals("instance_node"))
                {
                    String referenceNodeID = childElement.getAttribute("url").substring(1);
                    MeshGroupRetrievable meshGroupRetrievable = new NodeInstance(referenceNodeID);
                    meshGroupRetrievables.add(meshGroupRetrievable);
                }
                else if (childElement.getTagName().equals("instance_geometry"))
                {
                    MeshGroup meshGroup = instanceGeometryParser.parseInstanceGeometry(childElement);
                    meshGroupRetrievables.add(new SimpleMeshGroupRetrievable(meshGroup));
                }
            }
        }
        return meshGroupRetrievables;
    }

    private IncompleteNode processNodeElementAndRegister(Element element, boolean topLevel) throws XMLParseException
    {
        Element matrixElement = DomUtils.getSingleSubElement(element, "matrix");
        float[] transformMatrix;
        if (matrixElement != null)
        {
            transformMatrix = DomUtils.getFloatsFromElement(matrixElement);
        }
        else
        {
            transformMatrix = MathUtils.IDENTITY_MATRIX4;
        }
        IncompleteNode incompleteNode = new IncompleteNode(transformMatrix, topLevel);
        List<MeshGroupRetrievable> meshGroupRetrievables = getMeshGroupRetrievables(element, false);
        incompleteNode.addMeshRetrievables(meshGroupRetrievables);
        String id = element.getAttribute("id");
        incompleteNodeMap.put(id, incompleteNode);
        return incompleteNode;
    }

    private class NodeInstance implements MeshGroupRetrievable
    {
        private final String referenceNodeID;

        public NodeInstance(String referenceNodeID)
        {
            this.referenceNodeID = referenceNodeID;
        }

        @Override
        public MeshGroup retrieveMeshGroup()
        {
            MeshGroupRetrievable meshGroupRetrievable = incompleteNodeMap.get(referenceNodeID);
            return meshGroupRetrievable.retrieveMeshGroup();
        }

        @Override
        public boolean hasId()
        {
            return false;
        }
    }

    private class IncompleteNode implements MeshGroupRetrievable
    {
        private final float[] transformMatrix;
        private final boolean topLevel;
        private final List<MeshGroupRetrievable> children = new LinkedList<>();
        private MeshGroup meshGroup = null;

        IncompleteNode(float[] transformMatrix, boolean topLevel)
        {
            this.transformMatrix = transformMatrix;
            this.topLevel = topLevel;
        }

        void addMeshRetrievable(MeshGroupRetrievable meshGroupRetrievable)
        {
            children.add(meshGroupRetrievable);
        }

        public void addMeshRetrievables(List<MeshGroupRetrievable> meshGroupRetrievables)
        {
            children.addAll(meshGroupRetrievables);
        }

        @Override
        public MeshGroup retrieveMeshGroup()
        {
            if (meshGroup == null)
            {
                //For nodes which are not top-level collapse all child nodes by transforming them and amalgamating them into one mesh.
                //Only top-level nodes are ever seen and they have their initial transform specified, but unapplied.
                meshGroup = new MeshGroup(transformMatrix);
                for (MeshGroupRetrievable meshGroupRetrievable : children)
                {
                    MeshGroup childMeshGroup = meshGroupRetrievable.retrieveMeshGroup();
                    if (!topLevel)
                    {
                        childMeshGroup.applyMatrixTransform(transformMatrix);
                    }
                    meshGroup.add(childMeshGroup);
                }
            }
            return meshGroup;
        }

        @Override
        public boolean hasId()
        {
            return true;
        }

    }

    private static class SimpleMeshGroupRetrievable implements MeshGroupRetrievable
    {
        private final MeshGroup meshGroup;

        public SimpleMeshGroupRetrievable(MeshGroup meshGroup)
        {
            this.meshGroup = meshGroup;
        }

        @Override
        public MeshGroup retrieveMeshGroup()
        {
            return meshGroup;
        }

        @Override
        public boolean hasId()
        {
            return false;
        }
    }

    private interface MeshGroupRetrievable
    {
        MeshGroup retrieveMeshGroup();

        boolean hasId();
    }

    public Map<String, MeshGroup> getMeshGroupsMap()
    {
        return meshGroupsMap;
    }

    public List<MeshGroup> getAnnonymousMeshGroups()
    {
        return annonymousMeshGroups;
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