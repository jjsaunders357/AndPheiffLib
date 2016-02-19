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
 * Used to process all nodes under library_nodes and visual_scene elements.  Note: in SketchUp a top-level "SketchUp" node is what is parsed on visual_scene_elements.
 * This recursively descends through a node hierarchy composed of 3 types of nodes:
 * 1. nodes: Possibly include initial matrix transformation information and will contain other nodes
 * 2. geometry-instances: Reference to previously parsed geometry data.  These are always leaves.
 * 3. node-instances: References to other nodes.  These only occur under library_nodes elements.  These are always leaves.
 *
 * This requires 3 passes:
 * Pass 1: Goes through top level nodes and builds tree for each one.  Also creates a map from id to any node objects encountered.  The roots of these trees are returned.
 * Pass 2: Goes through the root of each tree returned, retrieves the map and adds it to the list of anonymous mesh groups.  Note: SketchUp creates these for all geometry which is not grouped.
 * a. For geometry-instances this just returns a mesh group (easily built)
 * b. For node-instances this looks up the node in the previously built map and returns its meshgroup
 * c. For nodes themselves, this gets mesh groups from all children and combines them (recursively)
 * Pass 3: For each instance (top-level) node in the nodeMap, it retrieves the mesh group as described above.  All meshGroups are removed from the list of anonymous mesh groups.
 *
 * When processing the main scene, meshgroups already loaded from the library_nodes scene are injected into the node map ahead of time.
 * Created by Steve on 2/16/2016.
 */
public class ColladaNodeProcessor
{
    private final InstanceGeometryParser instanceGeometryParser;
    private final Map<String, ColladaNode> nodeMap = new HashMap<>();
    private final Map<String, MeshGroup> instanceMeshGroups = new HashMap<>();
    //List used here instead of Set to make it consistent for testing
    private final List<MeshGroup> annonymousInstanceMeshGroups = new ArrayList<>();

    public ColladaNodeProcessor(Element element, Map<String, Material> materials, Map<String, ColladaGeometry> geometries, Map<String, MeshGroup> libraryMeshGroups, boolean ignoreMaterialAssignments) throws XMLParseException
    {
        instanceGeometryParser = new InstanceGeometryParser(materials, geometries, ignoreMaterialAssignments);
        injectLibraryNodes(libraryMeshGroups);
        List<MeshGroupRetrievable> topLevelMeshGroupRetrievables = getMeshGroupRetrievables(element, true);
        for (MeshGroupRetrievable topLevelMeshGroupRetrievable : topLevelMeshGroupRetrievables)
        {
            annonymousInstanceMeshGroups.add(topLevelMeshGroupRetrievable.retrieveMeshGroup());
        }
        registerInstanceMeshGroups();
    }

    private void injectLibraryNodes(Map<String, MeshGroup> libraryMeshGroups)
    {
        for (Map.Entry<String, MeshGroup> entry : libraryMeshGroups.entrySet())
        {
            MeshGroup meshGroup = entry.getValue();
            nodeMap.put(entry.getKey(), new LibraryColladaNode(meshGroup));
        }
    }

    private void registerInstanceMeshGroups()
    {
        for (Map.Entry<String, ColladaNode> entry : nodeMap.entrySet())
        {
            ColladaNode colladaNode = entry.getValue();
            if (colladaNode.isInstanceNode())
            {
                MeshGroup meshGroup = colladaNode.retrieveMeshGroup();
                instanceMeshGroups.put(entry.getKey(), meshGroup);
                annonymousInstanceMeshGroups.remove(meshGroup);
            }
        }
    }

    private List<MeshGroupRetrievable> getMeshGroupRetrievables(Element element, boolean createInstanceNodes) throws XMLParseException
    {
        List<MeshGroupRetrievable> meshGroupRetrievables = new LinkedList<>();
        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling())
        {
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                Element childElement = (Element) child;
                if (childElement.getTagName().equals("node"))
                {
                    MeshGroupRetrievable meshGroupRetrievable = processNodeElementAndRegister(childElement, createInstanceNodes);
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

    private ColladaNode processNodeElementAndRegister(Element element, boolean isInstanceNode) throws XMLParseException
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
        ParsedColladaNode parsedColladaNode = new ParsedColladaNode(transformMatrix, isInstanceNode);
        List<MeshGroupRetrievable> meshGroupRetrievables = getMeshGroupRetrievables(element, false);
        parsedColladaNode.addMeshRetrievables(meshGroupRetrievables);
        String id = element.getAttribute("id");
        nodeMap.put(id, parsedColladaNode);
        return parsedColladaNode;
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
            MeshGroupRetrievable meshGroupRetrievable = nodeMap.get(referenceNodeID);
            return meshGroupRetrievable.retrieveMeshGroup();
        }
    }

    private interface ColladaNode extends MeshGroupRetrievable
    {
        boolean isInstanceNode();
    }

    private class LibraryColladaNode implements ColladaNode
    {
        private final MeshGroup meshGroup;

        public LibraryColladaNode(MeshGroup meshGroup)
        {
            this.meshGroup = meshGroup;
        }

        @Override
        public boolean isInstanceNode()
        {
            return false;
        }

        @Override
        public MeshGroup retrieveMeshGroup()
        {
            return meshGroup;
        }
    }

    private class ParsedColladaNode implements ColladaNode
    {
        private final float[] transformMatrix;
        private final boolean instanceNode;
        private final List<MeshGroupRetrievable> children = new LinkedList<>();
        private MeshGroup meshGroup = null;

        ParsedColladaNode(float[] transformMatrix, boolean instanceNode)
        {
            this.transformMatrix = transformMatrix;
            this.instanceNode = instanceNode;
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
                    if (!instanceNode)
                    {
                        childMeshGroup.applyMatrixTransform(transformMatrix);
                    }
                    meshGroup.add(childMeshGroup);
                }
            }
            return meshGroup;
        }

        public boolean isInstanceNode()
        {
            return instanceNode;
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
    }

    private interface MeshGroupRetrievable
    {
        MeshGroup retrieveMeshGroup();
    }

    public Map<String, MeshGroup> getInstanceMeshGroups()
    {
        return instanceMeshGroups;
    }

    public List<MeshGroup> getAnnonymousInstanceMeshGroups()
    {
        return annonymousInstanceMeshGroups;
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