/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.graphics.program;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.IntBuffer;

import android.content.res.AssetManager;
import android.opengl.GLES20;

import com.pheiffware.andpheifflib.Utils;
import com.pheiffware.andpheifflib.graphics.GraphicsException;

/**
 *
 */
public class Shader
{

	/**
	 * Creates a shader. Keeps a reference for deallocation later.
	 * 
	 * @param shaderType
	 * @return
	 * @throws GraphicsException
	 * @throws IOException
	 */
	public static int createShader(int shaderType, AssetManager assetManager, String assetFileName) throws GraphicsException
	{
		try
		{
			return createShader(shaderType, Utils.loadAssetAsString(assetManager, assetFileName));
		}
		catch (UnsupportedEncodingException exception)
		{
			throw new GraphicsException("Cannot load shader", exception);
		}
		catch (IOException exception)
		{
			throw new GraphicsException("Cannot load shader", exception);
		}
	}

	/**
	 * Creates a shader. Keeps a reference for deallocation later.
	 * 
	 * @param gl
	 * @param shaderType
	 * @param code
	 * @return
	 * @throws GraphicsException
	 */
	public static int createShader(int shaderType, String code) throws GraphicsException
	{
		int shaderHandle = GLES20.glCreateShader(shaderType);
		GLES20.glShaderSource(shaderHandle, code);
		GLES20.glCompileShader(shaderHandle);
		testShaderStatus(shaderHandle);
		return shaderHandle;
	}

	/**
	 * Tests the newly created shader's status
	 * 
	 * @param gl
	 * @param shaderHandle
	 * @throws GraphicsException
	 */
	private static void testShaderStatus(int shaderHandle) throws GraphicsException
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
