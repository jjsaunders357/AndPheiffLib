package com.pheiffware.lib.graphics.managed;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.utils.TextureUtils;

import java.util.HashMap;
import java.util.Map;


//TODO: glDepthRange(n,f) (might make sense for the sphere)

//TODO: sRGB textures and immutable textures (opengl 3.0 only)
//TODO: glBlendFunc and glDepthMask(false) - turn off after rendering opaque objects.  Turn back on again after translucent objects.
//TODO: multisample enable (opengl 3.0 only)

/**
 * Keeps references to core graphics objects which should only be loaded once or need to be cleaned up later.
 * <p/>
 * Created by Steve on 2/13/2016.
 */
public class GLCache
{
    private final Map<String, Texture> textures = new HashMap<>();
    private final FilterQuality defaultFilterQuality;
    private final int deviceGLVersion;
    private final AssetLoader ga;

    public GLCache(int deviceGLVersion, FilterQuality defaultFilterQuality, AssetLoader ga)
    {
        this.defaultFilterQuality = defaultFilterQuality;
        this.deviceGLVersion = deviceGLVersion;
        this.ga = ga;
        if (deviceGLVersion < 2)
        {
            throw new RuntimeException("Cannot work with openGL version below 2.0");
        }
    }


    /**
     * Loads an image into a newly created texture or gets previously loaded texture.
     *
     * @param imageAssetPath  image path
     * @param generateMipMaps Set to true if it makes sense to try to use mip-maps for this texture. This may be ignored based on given filter quality.
     * @param filterQuality   HIGH/MEDIUM/LOW (look up my definition)
     * @param sWrapMode       typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrapMode       typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @return GL handle to texture
     * @throws GraphicsException
     */
    public Texture createImageTexture(String imageAssetPath, boolean generateMipMaps, FilterQuality filterQuality, int sWrapMode, int tWrapMode) throws GraphicsException
    {
        Texture texture = new Texture(ga.loadGLTextureFromImage(imageAssetPath, generateMipMaps, filterQuality, sWrapMode, tWrapMode));
        textures.put(imageAssetPath, texture);
        return texture;
    }

    /**
     * Generates a texture which can have colors rendered onto it.
     *
     * @param pixelWidth    width
     * @param pixelHeight   height
     * @param alpha         should there be an alpha channel?
     * @param filterQuality HIGH/MEDIUM/LOW (look up my definition)
     * @param sWrapMode     typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrapMode     typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @return GL handle to texture
     */
    public Texture createColorRenderTexture(String name, int pixelWidth, int pixelHeight, boolean alpha, FilterQuality filterQuality, int sWrapMode, int tWrapMode)
    {
        Texture texture = new Texture(TextureUtils.genTextureForColorRendering(pixelWidth, pixelHeight, alpha, filterQuality, sWrapMode, tWrapMode));
        textures.put(name, texture);
        return texture;
    }

    /**
     * Generates a texture which can have depth rendered onto it.
     *
     * @param pixelWidth    width
     * @param pixelHeight   height
     * @param filterQuality HIGH/MEDIUM/LOW (look up my definition)
     * @param sWrapMode     typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrapMode     typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @return GL handle to texture
     */
    public Texture createDepthRenderTexture(String name, int pixelWidth, int pixelHeight, FilterQuality filterQuality, int sWrapMode, int tWrapMode)
    {
        Texture texture = new Texture(TextureUtils.genTextureForDepthRendering(pixelWidth, pixelHeight, filterQuality, sWrapMode, tWrapMode));
        textures.put(name, texture);
        return texture;
    }


    /**
     * Loads an image into a newly created texture or gets previously loaded texture. Filter quality defaulted.
     *
     * @param imageAssetPath  image path
     * @param generateMipMaps Set to true if it makes sense to try to use mip-maps for this texture. This may be ignored based on given filter quality.
     * @param sWrapMode       typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrapMode       typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @return GL handle to texture
     * @throws GraphicsException
     */
    public Texture createImageTexture(String imageAssetPath, boolean generateMipMaps, int sWrapMode, int tWrapMode) throws GraphicsException
    {
        return createImageTexture(imageAssetPath, generateMipMaps, defaultFilterQuality, sWrapMode, tWrapMode);
    }

    /**
     * Generates a texture which can have colors rendered onto it. Filter quality defaulted.
     *
     * @param pixelWidth  width
     * @param pixelHeight height
     * @param alpha       should there be an alpha channel?
     * @param sWrapMode   typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrapMode   typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @return GL handle to texture
     */
    public Texture createColorRenderTexture(String name, int pixelWidth, int pixelHeight, boolean alpha, int sWrapMode, int tWrapMode)
    {
        return createColorRenderTexture(name, pixelWidth, pixelHeight, alpha, defaultFilterQuality, sWrapMode, tWrapMode);
    }

    /**
     * Generates a texture which can have depth rendered onto it. Filter quality defaulted.
     *
     * @param pixelWidth  width
     * @param pixelHeight height
     * @param sWrapMode   typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrapMode   typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @return GL handle to texture
     */
    public Texture createDepthRenderTexture(String name, int pixelWidth, int pixelHeight, int sWrapMode, int tWrapMode)
    {
        return createDepthRenderTexture(name, pixelWidth, pixelHeight, defaultFilterQuality, sWrapMode, tWrapMode);
    }

    public void deallocate()
    {
        //TODO: dynamic buffers should be created through this class
        //TODO: Cleanup all directByteBuffers.  All other opengl resources get automatically wiped out by the system.
    }
}
