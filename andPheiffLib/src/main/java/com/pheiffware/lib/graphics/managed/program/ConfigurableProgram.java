package com.pheiffware.lib.graphics.managed.program;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.GraphicsException;

import java.nio.IntBuffer;

/**
 * Created by Steve on 8/7/2017.
 */

public class ConfigurableProgram extends Program
{
    //Reference to the shader handles used to build this program
    private final int[] shaderHandles;

    private static int buildProgram(int[] shaderHandles) throws GraphicsException
    {
        int handle = GLES20.glCreateProgram();
        for (int i = 0; i < shaderHandles.length; i++)
        {
            GLES20.glAttachShader(handle, shaderHandles[i]);
        }
        GLES20.glLinkProgram(handle);
        assertProgramStatus(handle);
        return handle;
    }

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

    public ConfigurableProgram(int[] shaderHandles) throws GraphicsException
    {
        super(buildProgram(shaderHandles));
        this.shaderHandles = shaderHandles;
    }
}
