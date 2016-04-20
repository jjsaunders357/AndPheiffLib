package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * When processing the main scene:
 * MeshGroups already loaded from the library_nodes scene are injected into the node map ahead of time.
 * Top-level objects are not flattened
 * Created by Steve on 2/19/2016.
 */
class SceneColladaNodeProcessor extends BaseColladaNodeProcessor
{
    //Objects which have the name attribute set.
    private Map<String, ColladaObject3D> objects = new HashMap<>();

    //Objects without a name attribute set.
    private List<ColladaObject3D> anonymousObjects = new LinkedList<>();

    /**
     * On creation, this processes all nodes in the given element hierarchy, using provided libraryMeshGroups, to create ColladaObject3D instances.
     * It does not flatten top level objects and instead initializes objects geometry untransformed, but with initial transform specified.
     *
     * @param element                   what to parse
     * @param materialsByID             library of materials, mapped by id which may be looked up.
     * @param geometries                a map from ids to ColladaGeometries
     * @param ignoreMaterialAssignments if parsing blender, materials will already have been assigned inside ColladaGeometries and what is encountered in this node structure is ambiguous and should be ignored.
     * @param libraryMeshGroups         any previous parsed meshGroups mapped by id
     * @throws XMLParseException
     */
    public SceneColladaNodeProcessor(Element element, Map<String, ColladaMaterial> materialsByID, Map<String, ColladaGeometry> geometries, boolean ignoreMaterialAssignments, Map<String, MeshGroup> libraryMeshGroups) throws XMLParseException
    {
        super(materialsByID, geometries, ignoreMaterialAssignments);
        injectLibraryNodes(libraryMeshGroups);
        List<MeshGroupProxy> topLevelMeshGroupProxies = getMeshGroupProxies(element);
        for (MeshGroupProxy topLevelMeshGroupProxy : topLevelMeshGroupProxies)
        {
            String name = topLevelMeshGroupProxy.getName();
            Matrix4 transform = topLevelMeshGroupProxy.getTransform();
            MeshGroup meshGroup = topLevelMeshGroupProxy.retrieveMeshGroup(false);
            ColladaObject3D colladaObject3D = new ColladaObject3D(transform, meshGroup.collapseMeshLists());
            if (name == null)
            {
                anonymousObjects.add(colladaObject3D);
            }
            else
            {
                //We don't want to lose any objects which have the same name (author just didn't care about name) so dump them in anonymous bin.
                if (!objects.containsKey(name))
                {
                    objects.put(name, colladaObject3D);
                }
                else
                {
                    anonymousObjects.add(colladaObject3D);
                }
            }
        }
    }

    private void injectLibraryNodes(Map<String, MeshGroup> libraryMeshGroups)
    {
        for (Map.Entry<String, MeshGroup> entry : libraryMeshGroups.entrySet())
        {
            MeshGroup meshGroup = entry.getValue();
            meshGroupProxies.put(entry.getKey(), new DirectMeshGroupProxy(meshGroup));
        }
    }

    public Map<String, ColladaObject3D> getObjects()
    {
        return objects;
    }

    public List<ColladaObject3D> getAnonymousObjects()
    {
        return anonymousObjects;
    }
}
