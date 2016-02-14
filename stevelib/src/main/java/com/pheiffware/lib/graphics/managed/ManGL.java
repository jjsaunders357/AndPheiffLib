package com.pheiffware.lib.graphics.managed;

import android.content.res.AssetManager;
import android.opengl.GLES20;

import com.pheiffware.lib.graphics.FatalGraphicsException;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.utils.ProgramUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * A core object which manages references to and between graphics objects.
 * Created by Steve on 2/13/2016.
 */
public class ManGL
{
    private final AssetManager assetManager;
    private final Map<String, Integer> vertexShaders = new HashMap<>();
    private final Map<String, Integer> fragmentShaders = new HashMap<>();
    private final Map<String, Program> programs = new HashMap<>();

    public ManGL(AssetManager assetManager)
    {
        this.assetManager = assetManager;
    }

    public int createVertexShader(String vertexShaderAssetPath) throws FatalGraphicsException
    {
        int vertexShaderHandle = ProgramUtils.createShader(assetManager, GLES20.GL_VERTEX_SHADER, vertexShaderAssetPath);
        vertexShaders.put(vertexShaderAssetPath, vertexShaderHandle);
        return vertexShaderHandle;
    }

    public int createFragmentShader(String fragmentShaderAssetPath) throws FatalGraphicsException
    {
        int fragmentShaderHandle = ProgramUtils.createShader(assetManager, GLES20.GL_FRAGMENT_SHADER, fragmentShaderAssetPath);
        vertexShaders.put(fragmentShaderAssetPath, fragmentShaderHandle);
        return fragmentShaderHandle;
    }

    public Program createProgram(String name, String vertexShaderAssetPath, String fragmentShaderAssetPath) throws FatalGraphicsException
    {
        int vertexShaderHandle = createVertexShader(vertexShaderAssetPath);
        int fragmentShaderHandle = createFragmentShader(fragmentShaderAssetPath);
        Program program = new Program(vertexShaderHandle, fragmentShaderHandle);
        programs.put(name, program);
        return program;
    }

    public Program getProgram(String testProgram)
    {
        return programs.get(testProgram);
    }

    @Deprecated
    public AssetManager getAssetManager()
    {
        return assetManager;
    }
}
