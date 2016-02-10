/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.graphics.program;

import java.nio.IntBuffer;

import android.opengl.GLES20;

import com.pheiffware.andpheifflib.graphics.GraphicsException;

/**
 * Creates a program, throwing a descriptive exception if link fails.
 */
public class Program
{
	public static int createProgram(int vertexShaderHandle, int fragmentShaderHandle) throws GraphicsException
	{
		int handle = GLES20.glCreateProgram();

		GLES20.glAttachShader(handle, vertexShaderHandle);
		GLES20.glAttachShader(handle, fragmentShaderHandle);
		GLES20.glLinkProgram(handle);
		testProgramStatus(handle);
		GLES20.glUseProgram(handle);
		return handle;
	}

	private static void testProgramStatus(int handle) throws GraphicsException
	{
		IntBuffer linkStatus = IntBuffer.allocate(1);
		GLES20.glGetProgramiv(handle, GLES20.GL_LINK_STATUS, linkStatus);
		if (linkStatus.get(0) == 0)
		{
			String infoLog = GLES20.glGetProgramInfoLog(handle);
			GLES20.glDeleteShader(handle);
			throw new GraphicsException("The program failed to link: " + infoLog);
		}
	}
}