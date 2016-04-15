package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.MeshGroup;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * When processing the library nodes top-level objects are flattened
 * Created by Steve on 2/19/2016.
 */
class LibraryColladaNodeProcessor extends BaseColladaNodeProcessor
{
    private final Map<String, MeshGroup> libraryMeshGroups = new HashMap<>();

    /**
     * @param element                   what to parse
     * @param materialsByID             library of materials, mapped by id which may be looked up.
     * @param geometries                a map from ids to ColladaGeometries
     * @param ignoreMaterialAssignments if parsing blender, materials will already have been assigned inside ColladaGeometries and what is encountered in this node structure is ambiguous and should be ignored.
     * @throws XMLParseException
     */
    public LibraryColladaNodeProcessor(Element element, Map<String, Material> materialsByID, Map<String, ColladaGeometry> geometries, boolean ignoreMaterialAssignments) throws XMLParseException
    {
        super(materialsByID, geometries, ignoreMaterialAssignments);
        List<MeshGroupProxy> topLevelMeshGroupProxies = getMeshGroupProxies(element);
        for (MeshGroupProxy topLevelMeshGroupProxy : topLevelMeshGroupProxies)
        {
            //It is unlikely there will be any top-level library nodes with transforms,
            //but if these are referenced and further transformed, this would be a problem.
            //This gets flattened library node (transform applied)
            MeshGroup meshGroup = topLevelMeshGroupProxy.retrieveMeshGroup(true);

            String id = topLevelMeshGroupProxy.getID();
            if (id == null)
            {
                throw new XMLParseException("Top level library element did not have id");
            }
            else
            {
                this.libraryMeshGroups.put(id, meshGroup);
            }

        }
    }

    public Map<String, MeshGroup> getLibraryMeshGroups()
    {
        return libraryMeshGroups;
    }

}