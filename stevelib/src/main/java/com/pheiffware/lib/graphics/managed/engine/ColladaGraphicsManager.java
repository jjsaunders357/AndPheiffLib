package com.pheiffware.lib.graphics.managed.engine;

import android.content.res.AssetManager;

import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaMaterial;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Program;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Comment me!
 * <p/>
 * Created by Steve on 4/19/2016.
 */
public abstract class ColladaGraphicsManager extends BaseGraphicsManager
{
    private List<ObjectRenderHandle> anonymousObjects = new ArrayList<>();

    public ColladaGraphicsManager(Program[] programs, String imagePath)
    {
        super(programs);
    }

    public static void quickLoadTextures(GLCache glCache, AssetManager am, String baseImagePath, Collada collada, int defaultWrapMode) throws GraphicsException
    {
        for (ColladaMaterial material : collada.materialsByName.values())
        {
            if (material.imageFileName != null)
            {
                String assetPath = baseImagePath + "/" + material.imageFileName;
                glCache.createImageTexture(am, material.imageFileName, assetPath, true, defaultWrapMode, defaultWrapMode);
            }
        }

    }

    public void addColladaObjects(Collada collada)
    {
        for (ColladaObject3D object : collada.anonymousObjects)
        {
            addColladaObject(object);
        }

    }

    public final ObjectRenderHandle addColladaObject(String name, ColladaObject3D colladaObject3D)
    {
        Mesh[] meshes = colladaObject3D.getMeshes();
        int[] programIndices = new int[colladaObject3D.getNumMeshes()];
        UniformNameValue[][] uniformNameValues = new UniformNameValue[colladaObject3D.getNumMeshes()][];
        for (int i = 0; i < programIndices.length; i++)
        {
            programIndices[i] = getProgramIndexForMesh(name, meshes[i]);
            uniformNameValues[i] = getDefaultUniformNameValues(name, colladaObject3D.getMaterial(i));
        }

        ObjectRenderHandle anonymousObject = addObject(meshes, programIndices, uniformNameValues);
        anonymousObjects.add(anonymousObject);
        return anonymousObject;
    }

    public final ObjectRenderHandle addColladaObject(ColladaObject3D colladaObject3D)
    {
        return addColladaObject(null, colladaObject3D);
    }


    //TODO: Comment
    protected abstract int getProgramIndexForMesh(String name, Mesh mesh);

    protected abstract UniformNameValue[] getDefaultUniformNameValues(String name, ColladaMaterial material);
}
