package com.pheiffware.lib.graphics.managed;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.ParseException;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.program.ConfigurableProgram;
import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.managed.program.shader.ShaderBuilder;
import com.pheiffware.lib.graphics.managed.texture.MostRecentTextureBindingStrategy;
import com.pheiffware.lib.graphics.managed.texture.Texture2D;
import com.pheiffware.lib.graphics.managed.texture.TextureBinder;
import com.pheiffware.lib.graphics.managed.texture.textureBuilders.ColorRenderTextureBuilder;
import com.pheiffware.lib.graphics.managed.texture.textureBuilders.CubeColorRenderTextureBuilder;
import com.pheiffware.lib.graphics.managed.texture.textureBuilders.CubeDepthRenderTextureBuilder;
import com.pheiffware.lib.graphics.managed.texture.textureBuilders.DepthRenderTextureBuilder;
import com.pheiffware.lib.graphics.managed.texture.textureBuilders.ImageTextureBuilder;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


//TODO: sRGB textures and immutable textures (opengl 3.0 only)
//TODO: glBlendFunc and glDepthMask(false) - turn off after rendering opaque objects.  Turn back on again after translucent objects.
//TODO: multisample enable (opengl 3.0 only)

/**
 * Keeps references to core graphics objects for caching, reconfiguration and eventual clean up.
 * <p/>
 * Created by Steve on 2/13/2016.
 */
public class GLCache
{
    private final int deviceGLVersion;
    private final FilterQuality defaultFilterQuality;
    private final TextureBinder textureBinder;
    private final AssetLoader al;
    private final ShaderBuilder shaderBuilder;
    private final Map<String, Object> systemConfig = new HashMap<>();

    //Remembered texture images, used to avoid loading the same image twice
    private final Map<String, Texture2D> textureImageCache = new HashMap<>();

    public GLCache(AssetLoader al, int deviceGLVersion, FilterQuality defaultFilterQuality, String shaderRootPath)
    {
        if (deviceGLVersion < 2)
        {
            throw new RuntimeException("Cannot work with openGL version below 2.0");
        }
        this.deviceGLVersion = deviceGLVersion;
        this.defaultFilterQuality = defaultFilterQuality;
        this.al = al;
        shaderBuilder = new ShaderBuilder(al, shaderRootPath);
        textureBinder = new TextureBinder(PheiffGLUtils.getNumTextureUnits(), new MostRecentTextureBindingStrategy(PheiffGLUtils.getNumTextureUnits()));
    }

    /**
     * Allows retrieval of already loaded/cached textures.
     *
     * @param ID
     * @return
     */
    public Texture2D getTexture(String ID)
    {
        return textureImageCache.get(ID);
    }

    /**
     * Allows storing of textures, by ID, for retrieval later
     *
     * @param ID
     * @param texture
     */
    public void putTexture(String ID, Texture2D texture)
    {
        textureImageCache.put(ID, texture);
    }

    /**
     * Creates a builder for a 2D texture containing an image.
     *
     * @param imageAssetPath the location of the image to load
     * @return a builder to create the texture
     */
    public ImageTextureBuilder buildImageTex(String imageAssetPath)
    {
        return new ImageTextureBuilder(textureBinder, defaultFilterQuality, true, al, imageAssetPath);
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
     * Creates a builder for a 2D texture used for depth rendering.
     *
     * @param width  width, in pixels, of texture
     * @param height height, in pixels, of texture
     * @return a builder to create the texture
     */
    public DepthRenderTextureBuilder buildDepthTex(int width, int height)
    {
        return new DepthRenderTextureBuilder(textureBinder, defaultFilterQuality, width, height);
    }

    public CubeColorRenderTextureBuilder buildCubeColorTex(int width, int height)
    {
        return new CubeColorRenderTextureBuilder(textureBinder, defaultFilterQuality, width, height);
    }

    public CubeDepthRenderTextureBuilder buildCubeDepthTex(int width, int height)
    {
        return new CubeDepthRenderTextureBuilder(textureBinder, defaultFilterQuality, width, height);
    }

    private Program buildProgram(Map<String, Object> versionConfig, String... shaderPaths) throws ParseException, GraphicsException, IOException
    {
        ConfigurableProgram configurableProgram = new ConfigurableProgram(versionConfig, shaderPaths);
        configurableProgram.configure(shaderBuilder, systemConfig);
        return configurableProgram;
    }

    public void destroy()
    {
        //TODO: dynamic buffers should be created through this class
        //TODO: Cleanup all directByteBuffers.  All other opengl resources get automatically wiped out by the system.
    }

}
