package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.graphics.managed.mesh.MeshGroup;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Steve on 2/20/2016.
 */
class ColladaNode implements MeshGroupProxy
{
    private final String id;
    private final String name;
    private final float[] transformMatrix;
    private final List<MeshGroupProxy> children = new LinkedList<>();
    private MeshGroup meshGroup = null;

    ColladaNode(String id, String name, float[] transformMatrix)
    {
        this.id = id;
        this.name = name;
        this.transformMatrix = transformMatrix;
    }

    public void addMeshRetrievables(List<MeshGroupProxy> meshGroupProxies)
    {
        children.addAll(meshGroupProxies);
    }

    @Override
    public String getID()
    {
        return id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public float[] getTransform()
    {
        return transformMatrix;
    }

    @Override
    public MeshGroup retrieveMeshGroup(boolean flatten)
    {
        if (meshGroup == null)
        {
            //For nodes which are not top-level collapse all child nodes by transforming them and amalgamating them into one mesh.
            //Only top-level nodes are ever seen and they have their initial transform specified, but unapplied.
            meshGroup = new MeshGroup(transformMatrix);
            for (MeshGroupProxy meshGroupProxy : children)
            {
                MeshGroup childMeshGroup = meshGroupProxy.retrieveMeshGroup(true);
                if (flatten)
                {
                    childMeshGroup.applyMatrixTransform(transformMatrix);
                }
                meshGroup.add(childMeshGroup);
            }
        }
        return meshGroup;
    }
}
