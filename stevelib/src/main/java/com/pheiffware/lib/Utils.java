/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import android.content.res.AssetManager;

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
	public static String loadAssetAsString(AssetManager assetManager, String assetFileName) throws IOException
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
