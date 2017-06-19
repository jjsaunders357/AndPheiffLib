package com.pheiffware.lib.graphics.managed.engine.newEngine;

import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by Steve on 6/19/2017.
 */

public class ObjectManager
{
    private final List<ObjectHandle> objects = new ArrayList<>();
    private final MeshDataManager meshDataManager = new MeshDataManager();
    private ObjectBuilder currentObject;

    public ObjectHandle startObject()
    {
        currentObject = new ObjectBuilder();
        objects.add(currentObject.objectHandle);
        return currentObject.objectHandle;
    }

    public void endObject()
    {
        currentObject.setupHandle();
    }

    public MeshHandle addMesh(Mesh mesh, EnumSet<VertexAttribute> dynamicAttributes, Technique technique, RenderPropertyValue[] renderProperties)
    {
        MeshHandle meshHandle = meshDataManager.addMesh(mesh, dynamicAttributes, technique, renderProperties);
        currentObject.addMesh(meshHandle);
        return meshHandle;
    }

    public MeshHandle addStaticMesh(Mesh mesh)
    {
        MeshHandle meshHandle = meshDataManager.addStaticMesh(mesh);
        currentObject.addMesh(meshHandle);
        return meshHandle;
    }

    public MeshHandle addStaticMesh(Mesh mesh, Technique technique)
    {
        MeshHandle meshHandle = meshDataManager.addStaticMesh(mesh, technique);
        currentObject.addMesh(meshHandle);
        return meshHandle;
    }

    public MeshHandle addStaticMesh(Mesh mesh, Technique technique, RenderPropertyValue[] renderProperties)
    {
        MeshHandle meshHandle = meshDataManager.addStaticMesh(mesh, technique, renderProperties);
        currentObject.addMesh(meshHandle);
        return meshHandle;
    }

    public MeshHandle addMesh(Mesh mesh, EnumSet<VertexAttribute> dynamicAttributes, Technique technique)
    {
        MeshHandle meshHandle = meshDataManager.addMesh(mesh, dynamicAttributes, technique);
        currentObject.addMesh(meshHandle);
        return meshHandle;
    }

    public void packAndTransfer()
    {
        meshDataManager.packAndTransfer();
    }

    public void transferDynamicData()
    {
        meshDataManager.transferDynamicData();
    }
}
