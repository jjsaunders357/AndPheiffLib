package com.pheiffware.lib.graphics.managed;

import android.content.res.AssetManager;
import android.opengl.GLES20;

import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.utils.ProgramUtils;
import com.pheiffware.lib.graphics.utils.TextureUtils;

import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * A core object which manages references to and between graphics objects. Created by Steve on 2/13/2016.
 */
public class ManGL
{
    private final AssetManager assetManager;
    private final Map<String, Integer> vertexShaders = new HashMap<>();
    private final Map<String, Integer> fragmentShaders = new HashMap<>();
    private final Map<String, Program> programs = new HashMap<>();
    private final Map<String, Texture> textures = new HashMap<>();
    private final FilterQuality defaultFilterQuality;

    public ManGL(AssetManager assetManager, FilterQuality defaultFilterQuality, GL10 gl, EGLConfig config)
    {
        this.assetManager = assetManager;
        this.defaultFilterQuality = defaultFilterQuality;
    }

    /**
     * Creates a vertex shader from the given asset path if not already loaded.
     *
     * @param vertexShaderAssetPath
     * @return
     * @throws GraphicsException
     */
    public int getVertexShader(String vertexShaderAssetPath) throws GraphicsException
    {
        Integer vertexShaderHandle = vertexShaders.get(vertexShaderAssetPath);
        if (vertexShaderHandle == null)
        {
            vertexShaderHandle = ProgramUtils.createShader(assetManager, GLES20.GL_VERTEX_SHADER, vertexShaderAssetPath);
            vertexShaders.put(vertexShaderAssetPath, vertexShaderHandle);
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
    public int getFragmentShader(String fragmentShaderAssetPath) throws GraphicsException
    {
        Integer fragmentShaderHandle = fragmentShaders.get(fragmentShaderAssetPath);
        if (fragmentShaderHandle == null)
        {
            fragmentShaderHandle = ProgramUtils.createShader(assetManager, GLES20.GL_FRAGMENT_SHADER, fragmentShaderAssetPath);
            fragmentShaders.put(fragmentShaderAssetPath, fragmentShaderHandle);
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
    public Program createProgram(String name, String vertexShaderAssetPath, String fragmentShaderAssetPath) throws GraphicsException
    {
        int vertexShaderHandle = getVertexShader(vertexShaderAssetPath);
        int fragmentShaderHandle = getFragmentShader(fragmentShaderAssetPath);
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
    public Texture createImageTexture(String imageAssetPath, boolean generateMipMaps, FilterQuality filterQuality, int sWrapMode, int tWrapMode) throws GraphicsException
    {
        Texture texture = new Texture(TextureUtils.genTextureFromImage(assetManager, imageAssetPath, generateMipMaps, filterQuality, sWrapMode, tWrapMode));
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

    public Program getProgram(String name)
    {
        return programs.get(name);
    }

    @Deprecated
    public AssetManager getAssetManager()
    {
        return assetManager;
    }

    public void reallocate()
    {
        //TODO: Dynamic buffers, among other resources require explicit memory deallocation.
        //Whenever an activity/fragment stops these resources are deallocated in the deallocate method.  This method will reallocate them if necessary after a deallocate
    }

    public void deallocate()
    {
    }
}
