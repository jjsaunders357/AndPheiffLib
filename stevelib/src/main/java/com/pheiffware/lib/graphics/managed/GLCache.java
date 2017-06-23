package com.pheiffware.lib.graphics.managed;

import android.opengl.GLES20;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.managed.texture.MostRecentTextureBindingStrategy;
import com.pheiffware.lib.graphics.managed.texture.Texture;
import com.pheiffware.lib.graphics.managed.texture.Texture2D;
import com.pheiffware.lib.graphics.managed.texture.TextureBinder;
import com.pheiffware.lib.graphics.managed.texture.TextureCubeMap;
import com.pheiffware.lib.graphics.managed.texture.textureBuilders.ColorRenderTextureBuilder;
import com.pheiffware.lib.graphics.managed.texture.textureBuilders.ImageTextureBuilder;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;

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
    private final TextureBinder textureBinder;
    private AssetLoader al;

    public GLCache(int deviceGLVersion, FilterQuality defaultFilterQuality, AssetLoader al)
    {
        this.defaultFilterQuality = defaultFilterQuality;
        this.deviceGLVersion = deviceGLVersion;
        this.al = al;
        if (deviceGLVersion < 2)
        {
            throw new RuntimeException("Cannot work with openGL version below 2.0");
        }

        textureBinder = new TextureBinder(PheiffGLUtils.getNumTextureUnits(), new MostRecentTextureBindingStrategy(PheiffGLUtils.getNumTextureUnits()));
    }

    //TODO: Remove name and just don't load if already present.  Provide an override option.

    /**
     * Creates a builder for a 2D texture containing an image.
     *
     * @param name           the name of the texture, which can be used to avoid loading more than once
     * @param imageAssetPath the location of the image to load
     * @return a builder to create the texture
     */
    public ImageTextureBuilder buildImageTex(final String name, String imageAssetPath)
    {
        return new ImageTextureBuilder(textureBinder, defaultFilterQuality, true, al, imageAssetPath,
                new TextureRegister()
                {
                    @Override
                    public void register(Texture texture)
                    {
                        textures.put(name, texture);
                    }
                }
        );
    }

    /**
     * Creates a builder for a 2D texture used for color rendering.
     *
     * @param width  width, in pixels, of texture
     * @param height height, in pixels, of texture
     * @return a builder to create the texture
     */
    public ColorRenderTextureBuilder buildColorRenderTex(int width, int height)
    {
        return new ColorRenderTextureBuilder(textureBinder, defaultFilterQuality, width, height);
    }

    /**
     * Generates a texture which can have colors rendered onto it.
     *
     * @param pixelWidth    width
     * @param pixelHeight   height
     * @param alpha         should there be an alpha channel?
     * @param filterQuality HIGH/MEDIUM/LOW (look up my definition)
     * @param sWrap         typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrap         typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @return GL handle to texture
     */
    public Texture createColorRenderTexture(String name, int pixelWidth, int pixelHeight, boolean alpha, FilterQuality filterQuality, int sWrap, int tWrap)
    {
        Texture texture = new Texture2D(textureBinder, pixelWidth, pixelHeight);
        if (alpha)
        {
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, pixelWidth, pixelHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        }
        else
        {
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, pixelWidth, pixelHeight, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, null);
        }
        filterQuality.applyToBoundTexture2D(false);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, sWrap);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, tWrap);
        textures.put(name, texture);
        return texture;
    }

    /**
     * Generates a texture which can have depth rendered onto it.
     *
     * @param pixelWidth    width
     * @param pixelHeight   height
     * @param filterQuality HIGH/MEDIUM/LOW (look up my definition)
     * @param sWrap         typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrap         typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @return GL handle to texture
     */
    public Texture createDepthRenderTexture(String name, int pixelWidth, int pixelHeight, FilterQuality filterQuality, int sWrap, int tWrap)
    {
        Texture texture = new Texture2D(textureBinder, pixelWidth, pixelHeight);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT, pixelWidth, pixelHeight, 0, GLES20.GL_DEPTH_COMPONENT, GLES20.GL_UNSIGNED_SHORT, null);
        filterQuality.applyToBoundTexture2D(false);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, sWrap);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, tWrap);
        textures.put(name, texture);
        return texture;
    }

    /**
     * Generates a texture which can have colors rendered onto it. Filter quality defaulted.
     *
     * @param pixelWidth  width
     * @param pixelHeight height
     * @param alpha       should there be an alpha channel?
     * @param sWrap       typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrap       typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @return GL handle to texture
     */
    public Texture createColorRenderTexture(String name, int pixelWidth, int pixelHeight, boolean alpha, int sWrap, int tWrap)
    {
        return createColorRenderTexture(name, pixelWidth, pixelHeight, alpha, defaultFilterQuality, sWrap, tWrap);
    }

    /**
     * Generates a texture which can have depth rendered onto it. Filter quality defaulted.
     *
     * @param pixelWidth  width
     * @param pixelHeight height
     * @param sWrap       typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrap       typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @return GL handle to texture
     */
    public Texture createDepthRenderTexture(String name, int pixelWidth, int pixelHeight, int sWrap, int tWrap)
    {
        return createDepthRenderTexture(name, pixelWidth, pixelHeight, defaultFilterQuality, sWrap, tWrap);
    }

    public TextureCubeMap createCubeDepthRenderTexture(String name, int pixelWidth, int pixelHeight, FilterQuality filterQuality)
    {
        //TODO: Page 258
        //TODO: Mipmap building
        return null;
    }

    public TextureCubeMap createCubeDepthRenderTexture(String name, int pixelWidth, int pixelHeight)
    {

        return null;
    }


    public void deallocate()
    {
        //Do not retain reference to this as it can leak memory
        al = null;
        //TODO: dynamic buffers should be created through this class
        //TODO: Cleanup all directByteBuffers.  All other opengl resources get automatically wiped out by the system.
    }

    /**
     * Retrieve a named texture.
     *
     * @param name
     * @return
     */
    public Texture getTexture(String name)
    {
        return textures.get(name);
    }

    public interface TextureRegister
    {
        void register(Texture texture);
    }
}
