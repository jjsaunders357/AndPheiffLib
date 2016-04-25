package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaMaterial;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Technique;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Comment me!
 * <p/>
 * Created by Steve on 4/19/2016.
 */
public abstract class ColladaGraphicsManager extends BaseGraphicsManager
{
    /**
     * Simple function to load all textures in a given file into the cache, for a given image directory.  All images are named based on the base file name.
     *
     * @param glCache
     * @param imageAssetDirectory
     * @param collada
     * @param defaultWrapMode
     * @throws GraphicsException
     */
    public static void quickLoadTextures(GLCache glCache, String imageAssetDirectory, Collada collada, int defaultWrapMode) throws GraphicsException
    {
        for (ColladaMaterial material : collada.materialsByName.values())
        {
            if (material.imageFileName != null)
            {
                String assetPath = imageAssetDirectory + "/" + material.imageFileName;
                glCache.createImageTexture(material.imageFileName, assetPath, true, defaultWrapMode, defaultWrapMode);
            }
        }
    }

    private final Map<String, ObjectRenderHandle> namedObjects = new HashMap<>();
    private final List<ObjectRenderHandle> anonymousObjects = new ArrayList<>();

    public ColladaGraphicsManager(Technique[] techniques)
    {
        super(techniques);
    }

    public void addColladaObjects(Collada collada)
    {
        for (ColladaObject3D object : collada.anonymousObjects)
        {
            addColladaObject(null, object);
        }
        for (Map.Entry<String, ColladaObject3D> entry : collada.objects.entrySet())
        {
            addColladaObject(entry.getKey(), entry.getValue());
        }
    }

    public final ObjectRenderHandle addColladaObject(String name, ColladaObject3D colladaObject3D)
    {
        ObjectRenderHandle renderHandle = startNewObjectDef();
        Mesh[] meshes = colladaObject3D.getMeshes();
        for (int i = 0; i < meshes.length; i++)
        {
            Mesh mesh = meshes[i];
            Technique technique = getTechniqueForMesh(name, mesh);
            PropertyValue[] propertyValuesArray = getPropertyValuesForMaterial(name, colladaObject3D.getMaterial(i));
            addMesh(meshes[i], technique, propertyValuesArray);
        }
        endObjectDef();

        if (name == null)
        {
            anonymousObjects.add(renderHandle);
        }
        else
        {
            namedObjects.put(name, renderHandle);
        }
        return renderHandle;
    }


    public final ObjectRenderHandle addColladaObject(ColladaObject3D colladaObject3D)
    {
        return addColladaObject(null, colladaObject3D);
    }


    //TODO: Comment
    protected abstract Technique getTechniqueForMesh(String objectName, Mesh mesh);

    protected abstract PropertyValue[] getPropertyValuesForMaterial(String objectName, ColladaMaterial material);
}
