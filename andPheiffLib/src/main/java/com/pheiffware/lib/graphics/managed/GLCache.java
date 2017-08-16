package com.pheiffware.lib.graphics.managed;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.managed.program.Technique;
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private final Map<String, Object> graphicsSystemConfig;
    private final List<GraphicsConfigListener> graphicsConfigListeners = new ArrayList<>();

    //Remembered texture images, used to avoid loading the same image twice
    private final Map<String, Texture2D> textureImageCache = new HashMap<>();

    public GLCache(AssetLoader al, int deviceGLVersion, Map<String, Object> graphicsSystemConfig, FilterQuality defaultFilterQuality, String shaderRootPath)
    {
        if (deviceGLVersion < 2)
        {
            throw new RuntimeException("Cannot work with openGL version below 2.0");
        }
        this.deviceGLVersion = deviceGLVersion;
        this.defaultFilterQuality = defaultFilterQuality;
        this.al = al;
        this.graphicsSystemConfig = graphicsSystemConfig;
        shaderBuilder = new ShaderBuilder(al, shaderRootPath);
        textureBinder = new TextureBinder(PheiffGLUtils.getNumTextureUnits(), new MostRecentTextureBindingStrategy(PheiffGLUtils.getNumTextureUnits()));
    }

    /**
     * Changes configuration settings for the graphics system.  This will notify all graphical objects, which care, to reconfigure themselves.
     * This will only be called on the rendering thread!
     *
     * @param graphicsSystemConfig the new configuration
     */
    public void configure(Map<String, Object> graphicsSystemConfig) throws GraphicsException
    {
        this.graphicsSystemConfig.clear();
        this.graphicsSystemConfig.putAll(graphicsSystemConfig);
        notifyGraphicsConfigListeners();
    }

    public void setConfigProperty(String name, Object value) throws GraphicsException
    {
        graphicsSystemConfig.put(name, value);
        notifyGraphicsConfigListeners();
    }

    public <T> T getConfigProperty(String name, Class<T> cls)
    {
        return (T) graphicsSystemConfig.get(name);
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

    public Program buildProgram(String... shaderPaths) throws GraphicsException
    {
        return buildProgram(new HashMap<String, Object>(), shaderPaths);
    }

    public Program buildProgram(Map<String, Object> localConfig, String... shaderPaths) throws GraphicsException
    {
        return new Program(shaderBuilder, localConfig, shaderPaths);
    }

    public <T extends Technique> T buildTechnique(Class<T> cls) throws GraphicsException
    {
        return buildTechnique(new HashMap<String, Object>(), cls);
    }

    public <T extends Technique> T buildTechnique(Class<T> cls, Object... nameValues) throws GraphicsException
    {
        if (nameValues.length % 2 != 0)
        {
            throw new IllegalArgumentException("Uneven number of arguments to buildTechnique");
        }
        Map<String, Object> localConfig = new HashMap<>();
        for (int i = 0; i < nameValues.length; i += 2)
        {
            String name = (String) nameValues[i];
            Object value = nameValues[i + 1];
            localConfig.put(name, value);
        }
        return buildTechnique(localConfig, cls);
    }

    public <T extends Technique> T buildTechnique(Map<String, Object> localConfig, Class<T> cls) throws GraphicsException
    {
        try
        {
            Constructor<T> constructor = cls.getConstructor(ShaderBuilder.class, Map.class);
            T technique = constructor.newInstance(shaderBuilder, localConfig);
            technique.onSystemConfigChanged(graphicsSystemConfig);
            addSystemConfigListener(technique);
            return technique;
        }
        catch (NoSuchMethodException e)
        {
            throw new GraphicsException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new GraphicsException(e);
        }
        catch (InstantiationException e)
        {
            throw new GraphicsException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new GraphicsException(e);
        }
    }

    public void addSystemConfigListener(GraphicsConfigListener listener)
    {
        graphicsConfigListeners.add(listener);
    }

    public void removeSystemConfigListener(GraphicsConfigListener listener)
    {
        graphicsConfigListeners.remove(listener);
    }

    private void notifyGraphicsConfigListeners() throws GraphicsException
    {
        for (GraphicsConfigListener listener : graphicsConfigListeners)
        {
            listener.onSystemConfigChanged(graphicsSystemConfig);
        }
    }

    public void destroy()
    {
        //TODO: dynamic buffers should be created through this class
        //TODO: Cleanup all directByteBuffers.  All other opengl resources get automatically wiped out by the system.
    }

}
