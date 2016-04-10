package com.pheiffware.lib.graphics.managed;

import android.content.res.AssetManager;
import android.opengl.GLES20;

import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.utils.ProgramUtils;
import com.pheiffware.lib.graphics.utils.TextureUtils;
import com.pheiffware.lib.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


//TODO: glDepthRange(n,f) (might make sense for the sphere)

//TODO: sRGB textures and immutable textures (opengl 3.0 only)
//TODO: glBlendFunc and glDepthMask(false) - turn off after rendering opaque objects.  Turn back on again after translucent objects.
//TODO: multisample enable (opengl 3.0 only)

/**
 * A core object which manages references to and between graphics objects.
 * <p/>
 * Created by Steve on 2/13/2016.
 */
public class ManGL
{
    private final Map<String, Integer> vertexShaders = new HashMap<>();
    private final Map<String, Integer> fragmentShaders = new HashMap<>();
    private final Map<String, Program> programs = new HashMap<>();
    private final Map<String, Texture> textures = new HashMap<>();
    private final FilterQuality defaultFilterQuality;
    private final int deviceGLVersion;

    public ManGL(int deviceGLVersion, FilterQuality defaultFilterQuality)
    {
        this.defaultFilterQuality = defaultFilterQuality;
        this.deviceGLVersion = deviceGLVersion;
        if (deviceGLVersion < 2)
        {
            throw new RuntimeException("Cannot work with openGL version below 2.0");
        }
    }

    /**
     * Creates a vertex shader from the given asset path if not already loaded.
     *
     * @param vertexShaderAssetPath
     * @return
     * @throws GraphicsException
     */
    public int getVertexShader(AssetManager am, String vertexShaderAssetPath) throws GraphicsException
    {
        Integer vertexShaderHandle = vertexShaders.get(vertexShaderAssetPath);
        if (vertexShaderHandle == null)
        {
            try
            {
                String code = Utils.loadAssetAsString(am, vertexShaderAssetPath);
                vertexShaderHandle = ProgramUtils.createShader(GLES20.GL_VERTEX_SHADER, code);
                vertexShaders.put(vertexShaderAssetPath, vertexShaderHandle);
            }
            catch (IOException e)
            {
                throw new GraphicsException(e);
            }
        }
        return vertexShaderHandle;
    }

    /**
     * Creates a fragment shader from the given asset path if not already loaded.
     *
     * @param fragmentShaderAssetPath
     * @return
     * @throws GraphicsException
     */
    public int getFragmentShader(AssetManager am, String fragmentShaderAssetPath) throws GraphicsException
    {
        Integer fragmentShaderHandle = fragmentShaders.get(fragmentShaderAssetPath);
        if (fragmentShaderHandle == null)
        {
            try
            {
                String code = Utils.loadAssetAsString(am, fragmentShaderAssetPath);
                fragmentShaderHandle = ProgramUtils.createShader(GLES20.GL_FRAGMENT_SHADER, code);
                fragmentShaders.put(fragmentShaderAssetPath, fragmentShaderHandle);
            }
            catch (IOException e)
            {
                throw new GraphicsException(e);
            }
        }
        return fragmentShaderHandle;
    }

    /**
     * Creates a program from given shaders.
     *
     * @param name
     * @param vertexShaderAssetPath
     * @param fragmentShaderAssetPath
     * @return
     * @throws GraphicsException
     */
    public Program createProgram(AssetManager am, String name, String vertexShaderAssetPath, String fragmentShaderAssetPath) throws GraphicsException
    {
        int vertexShaderHandle = getVertexShader(am, vertexShaderAssetPath);
        int fragmentShaderHandle = getFragmentShader(am, fragmentShaderAssetPath);
        Program program = new Program(vertexShaderHandle, fragmentShaderHandle);
        programs.put(name, program);
        return program;
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
    public Texture createImageTexture(AssetManager am, String imageAssetPath, boolean generateMipMaps, FilterQuality filterQuality, int sWrapMode, int tWrapMode) throws GraphicsException
    {
        Texture texture = new Texture(TextureUtils.genTextureFromImage(am, imageAssetPath, generateMipMaps, filterQuality, sWrapMode, tWrapMode));
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
    public Texture createImageTexture(AssetManager am, String imageAssetPath, boolean generateMipMaps, int sWrapMode, int tWrapMode) throws GraphicsException
    {
        return createImageTexture(am, imageAssetPath, generateMipMaps, defaultFilterQuality, sWrapMode, tWrapMode);
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

    public Program getProgram(String name)
    {
        return programs.get(name);
    }

    public void deallocate()
    {
        //TODO: Cleanup all directByteBuffers.  All other opengl resources get automatically wiped out by the system.
    }
}
