package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.geometry.collada.ColladaMaterial;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.texture.Texture2D;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steve on 6/19/2017.
 */

public abstract class ColladaLoader
{
    private final Map<String, ObjectHandle> objectHandleMap = new HashMap<>();
    protected final ObjectManager objectManager;
    protected final GLCache glCache;
    private final AssetLoader al;
    private final ColladaFactory colladaFactory;
    private final String imageDirectory;

    public ColladaLoader(ObjectManager objectManager, GLCache glCache, AssetLoader al, String imageDirectory, boolean homogenizePositions, ColladaMaterial defaultColladaMaterial)
    {
        this.objectManager = objectManager;
        this.glCache = glCache;
        this.al = al;
        this.imageDirectory = imageDirectory;
        colladaFactory = new ColladaFactory(homogenizePositions, defaultColladaMaterial);
    }

    public final void loadCollada(String assetPath) throws XMLParseException, IOException, GraphicsException
    {
        loadCollada(assetPath, "main");
    }

    public final void loadCollada(String assetPath, String defaultGroupID) throws XMLParseException, IOException, GraphicsException
    {
        Collada collada = colladaFactory.loadCollada(al, assetPath);
        for (String imageFileName : collada.imageFileNames)
        {
            imageFileName = getTexturePath(imageFileName);
            //If not already loaded
            if (glCache.getTexture(imageFileName) == null)
            {
                Texture2D texture = loadTexture2D(imageFileName);
                glCache.putTexture(imageFileName, texture);
            }
        }
        for (ColladaObject3D object3D : collada.anonymousObjects)
        {
            addObject(null, defaultGroupID, object3D);
        }
        for (Map.Entry<String, ColladaObject3D> entry : collada.objects.entrySet())
        {
            addObject(entry.getKey(), defaultGroupID, entry.getValue());
        }
    }

    protected void addObject(String name, String defaultGroupID, ColladaObject3D object3D)
    {
        Matrix4 initialMatrix = object3D.getInitialMatrix();
        ObjectHandle objectHandle = objectManager.startObject(getGroupID(name, object3D, defaultGroupID));
        if (name != null)
        {
            objectHandleMap.put(name, objectHandle);
        }
        for (int i = 0; i < object3D.getNumMeshes(); i++)
        {
            Mesh mesh = object3D.getMesh(i);
            ColladaMaterial material = object3D.getMaterial(i);
            addMesh(mesh, material, initialMatrix, name);
        }
        objectManager.endObject();
    }


    protected String getTexturePath(String imageFileName)
    {
        return imageDirectory + "/" + imageFileName;
    }

    protected String getGroupID(String objectName, ColladaObject3D object3D, String defaultGroupID)
    {
        return defaultGroupID;
    }

    protected abstract void addMesh(Mesh mesh, ColladaMaterial material, Matrix4 initialMatrix, String name);

    protected abstract Texture2D loadTexture2D(String imageFileName) throws GraphicsException;

    public ObjectHandle getHandle(String name)
    {
        return objectHandleMap.get(name);
    }
}
