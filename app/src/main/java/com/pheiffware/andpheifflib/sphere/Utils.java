/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * General utilities.
 */
public class Utils
{
	/**
	 * Loads the contents of a file in the assets directory as a string. Use '/' as separator.
	 * 
	 * @param assetManager
	 * @param assetFileName
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public static String loadAssetAsString(AssetManager assetManager, String assetFileName) throws UnsupportedEncodingException, IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(assetFileName), "UTF-8"));
		int character;
		StringBuilder builder = new StringBuilder();
		do
		{
			character = reader.read();
			if (character == -1)
			{
				break;
			}
			else
			{
				builder.append((char) character);
			}
		} while (true);
		return builder.toString();
	}

	/**
	 * Computes the size of an opengl type.
	 * 
	 * @param type
	 * @return
	 */
	public static int GLTypeToSize(int type)
	{
		switch (type)
		{
		case GLES20.GL_FLOAT:
			return 4;
		case GLES20.GL_FLOAT_VEC2:
			return 8;
		case GLES20.GL_FLOAT_VEC3:
			return 12;
		case GLES20.GL_FLOAT_VEC4:
			return 16;
		case GLES20.GL_INT:
			return 4;
		case GLES20.GL_INT_VEC2:
			return 8;
		case GLES20.GL_INT_VEC3:
			return 12;
		case GLES20.GL_INT_VEC4:
			return 16;
		case GLES20.GL_BOOL:
			return 1;
		case GLES20.GL_BOOL_VEC2:
			return 1;
		case GLES20.GL_BOOL_VEC3:
			return 1;
		case GLES20.GL_BOOL_VEC4:
			return 1;
		case GLES20.GL_FLOAT_MAT2:
			return 16;
		case GLES20.GL_FLOAT_MAT3:
			return 36;
		case GLES20.GL_FLOAT_MAT4:
			return 64;
		default:
			return 0;
		}
	}

	public static float[] createTranslationMatrix(float x, float y, float z)
	{
		float[] result = new float[16];
		Matrix.setIdentityM(result, 0);
		Matrix.translateM(result, 0, x, y, z);
		return result;
	}

	public static float[] createScaleMatrix(float x, float y, float z)
	{
		float[] result = new float[16];
		Matrix.setIdentityM(result, 0);
		Matrix.scaleM(result, 0, x, y, z);
		return result;
	}

	public static float[] createRotationMatrix(float angle, float x, float y, float z)
	{
		float[] result = new float[16];
		Matrix.setIdentityM(result, 0);
		Matrix.rotateM(result, 0, angle, x, y, z);
		return result;
	}

	public static float[] multiplyMatrix(float[] lhs, float[] rhs)
	{
		float[] result = new float[16];
		Matrix.multiplyMM(result, 0, lhs, 0, rhs, 0);
		return result;
	}

	/**
	 * Creates a projection matrix. You generally want to set flipVertical to true when using this to render to a texture as texture coordinates are
	 * backward.
	 * 
	 * @param fieldOfViewY
	 *            The field of view in the y direction (in degrees)
	 * 
	 * @param aspect
	 * @param zNear
	 * @param zFar
	 * @param flipVertical
	 * @return
	 */
	public static float[] generateProjectionMatrix(float fieldOfViewY, float aspect, float zNear, float zFar, boolean flipVertical)
	{
		float[] matrix = new float[16];

		float top = (float) (zNear * Math.tan(Math.PI / 180.0 * fieldOfViewY));
		float right = top * aspect;
		if (flipVertical)
		{
			top *= -1;
		}
		float bottom = -top;
		float left = -right;
		Matrix.frustumM(matrix, 0, left, right, bottom, top, zNear, zFar);
		return matrix;
	}

	/**
	 * Actively destroys a direct buffer. Calling this guarantees that memory is freed immediately.
	 * NOTE: Does not work in android!
	 */
	// Possible solution:
	// JNIEXPORT jobject JNICALL Java_com_foo_allocNativeBuffer(JNIEnv* env, jobject thiz, jlong size)
	// {
	// void* buffer = malloc(size);
	// jobject directBuffer = env->NewDirectByteBuffer(buffer, size);
	// return directBuffer;
	// }
	//
	// JNIEXPORT void JNICALL Java_comfoo_freeNativeBuffer(JNIEnv* env, jobject thiz, jobject bufferRef)
	// {
	// void *buffer = env->GetDirectBufferAddress(bufferRef);
	//
	// free(buffer);
	// }
	public static void deallocateDirectByteBuffer(ByteBuffer directByteBuffer)
	{

	}

}
