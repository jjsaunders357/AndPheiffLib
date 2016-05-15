package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaMaterial;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p/>
 * Created by Steve on 4/19/2016.
 */
public abstract class ColladaGraphicsLoader<M>
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

    private final BaseGraphicsManager<M> graphicsManager;
    private final Map<String, ObjectRenderHandle> namedObjects = new HashMap<>();
    private final List<ObjectRenderHandle> anonymousObjects = new ArrayList<>();

    public ColladaGraphicsLoader(BaseGraphicsManager<M> graphicsManager)
    {
        this.graphicsManager = graphicsManager;
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

    public final ObjectRenderHandle<M> addColladaObject(String name, ColladaObject3D colladaObject3D)
    {
        ObjectRenderHandle<M> renderHandle = graphicsManager.startNewObjectDef();
        Mesh[] meshes = colladaObject3D.getMeshes();
        for (int i = 0; i < meshes.length; i++)
        {
            Mesh mesh = meshes[i];
            BufferAndMaterial<M> bufferAndMaterial = getRenderMaterial(name, mesh, colladaObject3D.getMaterial(i));
            graphicsManager.addMesh(mesh, bufferAndMaterial.vertexBuffer, bufferAndMaterial.material);
        }
        graphicsManager.endObjectDef();

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


    public final ObjectRenderHandle<M> addColladaObject(ColladaObject3D colladaObject3D)
    {
        return addColladaObject(null, colladaObject3D);
    }


    public static class BufferAndMaterial<M>
    {
        public final StaticVertexBuffer vertexBuffer;
        public final M material;

        public BufferAndMaterial(StaticVertexBuffer vertexBuffer, M material)
        {
            this.vertexBuffer = vertexBuffer;
            this.material = material;
        }
    }

    /**
     * Figures out appropriate information about how to render/store the given mesh given various information
     *
     * @param objectName the name of the object this mesh is a part of
     * @param mesh       the mesh itself
     * @param colladaMaterial   the collada material used to render this
     * @return
     */
    protected abstract BufferAndMaterial<M> getRenderMaterial(String objectName, Mesh mesh, ColladaMaterial colladaMaterial);


}
