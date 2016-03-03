/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.graphics.utils;

import java.io.IOException;
import java.nio.IntBuffer;

import android.content.res.AssetManager;
import android.opengl.GLES20;

import com.pheiffware.lib.utils.Utils;
import com.pheiffware.lib.graphics.GraphicsException;

/**
 * Utility methods for loading/compiling shaders and programs.  Root location for loading files is the assets folder.
 */
public class ProgramUtils
{
    /**
     * Loads the specified vertex and fragment shaders and links them into a single program.
     *
     * @param assetManager
     * @param vertexAssetPath   Path to the shader file
     * @param fragmentAssetPath Path to the shader file
     * @return GL handle to program
     * @throws GraphicsException
     */
    public static int createProgram(AssetManager assetManager, String vertexAssetPath, String fragmentAssetPath) throws GraphicsException
    {
        int vertexShaderHandle = createShader(assetManager, GLES20.GL_VERTEX_SHADER, vertexAssetPath);
        int fragmentShaderHandle = createShader(assetManager, GLES20.GL_FRAGMENT_SHADER, fragmentAssetPath);
        return createProgram(vertexShaderHandle, fragmentShaderHandle);
    }

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
     * Loads and compiles a shader from a file.
     *
     * @param assetManager
     * @param shaderType      GLES20.GL_VERTEX_SHADER | GLES20.GL_FRAGMENT_SHADER
     * @param shaderAssetPath Path to the shader file
     * @return GL handle to shader
     * @throws GraphicsException
     */
    public static int createShader(AssetManager assetManager, int shaderType, String shaderAssetPath) throws GraphicsException
    {
        try
        {
            return createShader(shaderType, Utils.loadAssetAsString(assetManager, shaderAssetPath));
        }
        catch (IOException | GraphicsException exception)
        {
            throw new GraphicsException("Cannot load shader: " + shaderAssetPath, exception);
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