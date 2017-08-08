/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.graphics.utils;

import android.opengl.GLES20;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;

import java.io.IOException;
import java.nio.IntBuffer;

/**
 * Utility methods for loading/compiling shaders and programs.
 */
public class ProgramUtils
{
    /**
     * Loads a program from the given asset path.
     *
     * @param al
     * @param vertexShaderAssetPath
     * @param fragmentShaderAssetPath
     * @return
     * @throws GraphicsException
     */
    public static int loadProgram(AssetLoader al, String vertexShaderAssetPath, String fragmentShaderAssetPath) throws GraphicsException
    {
        int vertexShaderHandle = loadVertexShader(al, vertexShaderAssetPath);
        int fragmentShaderHandle = loadFragmentShader(al, fragmentShaderAssetPath);
        return ProgramUtils.createProgram(vertexShaderHandle, fragmentShaderHandle);
    }

    /**
     * Loads a vertex shader from the given asset path.
     *
     * @param al
     * @param vertexShaderAssetPath
     * @return
     * @throws GraphicsException
     */
    public static int loadVertexShader(AssetLoader al, String vertexShaderAssetPath) throws GraphicsException
    {
        try
        {
            String code = al.loadAssetAsString(vertexShaderAssetPath);
            return ProgramUtils.createShader(GLES20.GL_VERTEX_SHADER, code);
        }
        catch (GraphicsException e)
        {
            throw new GraphicsException("Could not compile shader \"" + vertexShaderAssetPath + "\":\n " + e.getMessage());
        }
        catch (IOException e)
        {
            throw new GraphicsException(e);
        }
    }

    /**
     * Loads a fragment shader from the given asset path.
     *
     * @param al
     * @param fragmentShaderAssetPath
     * @return
     * @throws GraphicsException
     */
    public static int loadFragmentShader(AssetLoader al, String fragmentShaderAssetPath) throws GraphicsException
    {
        try
        {
            String code = al.loadAssetAsString(fragmentShaderAssetPath);
            return ProgramUtils.createShader(GLES20.GL_FRAGMENT_SHADER, code);
        }
        catch (GraphicsException e)
        {
            throw new GraphicsException("Could not compile shader \"" + fragmentShaderAssetPath + "\":\n " + e.getMessage());
        }
        catch (IOException e)
        {
            throw new GraphicsException(e);
        }
    }
    //GLES32.GL_GEOMETRY_SHADER


    /**
     * Link program from loaded fragment/vertex shaders.
     *
     * @param vertexShaderHandle   GL handle to vertex shader
     * @param fragmentShaderHandle GL handle to fragment shader
     * @return GL handle to program
     * @throws GraphicsException
     */
    public static int createProgram(int vertexShaderHandle, int fragmentShaderHandle) throws GraphicsException
    {
        int handle = GLES20.glCreateProgram();

        GLES20.glAttachShader(handle, vertexShaderHandle);
        GLES20.glAttachShader(handle, fragmentShaderHandle);
        GLES20.glLinkProgram(handle);
        assertProgramStatus(handle);
        GLES20.glUseProgram(handle);
        return handle;
    }

    /**
     * Tests if a given program linked correctly and throws an appropriate exception if not.
     *
     * @param programHandle GL handle to program
     * @throws GraphicsException
     */
    private static void assertProgramStatus(int programHandle) throws GraphicsException
    {
        IntBuffer linkStatus = IntBuffer.allocate(1);
        GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus);
        if (linkStatus.get(0) == 0)
        {
            String infoLog = GLES20.glGetProgramInfoLog(programHandle);
            GLES20.glDeleteShader(programHandle);
            throw new GraphicsException("The program failed to link: " + infoLog);
        }
    }

    /**
     * Compiles a shader from code.
     *
     * @param shaderType GLES20.GL_VERTEX_SHADER | GLES20.GL_FRAGMENT_SHADER
     * @param code       Shader code itself
     * @return GL handle to shader
     * @throws GraphicsException
     */
    public static int createShader(int shaderType, String code) throws GraphicsException
    {
        int shaderHandle = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(shaderHandle, code);
        GLES20.glCompileShader(shaderHandle);
        assertShaderStatus(shaderHandle);
        return shaderHandle;
    }

    /**
     * Tests that a shader compiled correctly and throws an appropriate exception if not.
     *
     * @param shaderHandle GL handle to shader
     * @throws GraphicsException
     */
    private static void assertShaderStatus(int shaderHandle) throws GraphicsException
    {
        IntBuffer statusBuffer = IntBuffer.allocate(1);
        GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, statusBuffer);
        int status = statusBuffer.get(0);
        if (status != GLES20.GL_TRUE)
        {
            String infoLog = GLES20.glGetShaderInfoLog(shaderHandle);
            GLES20.glDeleteShader(shaderHandle);
            throw new GraphicsException("Shader failed to compile!: " + infoLog);
        }
    }
}