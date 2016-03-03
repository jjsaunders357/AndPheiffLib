/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import android.content.res.AssetManager;
import android.graphics.Path;

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

	public static void deallocateDirectByteBuffer(ByteBuffer directByteBuffer)
	{
        //TODO: Implement, Use the reflection to call java.nio.DirectByteBuffer.free()
    }

    public static final double getTimeElapsed(long earlierTimeStamp) {
        return (System.nanoTime() - earlierTimeStamp) / 1000000000.0;
    }

    @SuppressWarnings("unchecked")
    public static <T> T loadObj(String path, Class<T> cls) throws IOException, ClassNotFoundException {
        try (InputStream file = new FileInputStream(path);
             InputStream buffer = new BufferedInputStream(file);
             ObjectInput input = new ObjectInputStream(buffer)) {
            return (T) input.readObject();
        }
    }

    public static void saveObj(String path, Object object) throws IOException {

        try (OutputStream file = new FileOutputStream(path);
             OutputStream buffer = new BufferedOutputStream(file);
             ObjectOutput output = new ObjectOutputStream(buffer)) {
            output.writeObject(object);
        }
    }

    /**
     * Quick and dirty way to copy and object using serialization.  THIS IS NOT EFFICIENT AT ALL.
     *
     * @param object
     * @return
     */
    public static <T> T copyObj(T object) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput output = new ObjectOutputStream(buffer);
            output.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer.toByteArray());
            return (T) new ObjectInputStream(bais).readObject();
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Copy Error", e);
        } catch (IOException e) {
            throw new AssertionError("Copy Error", e);
        }

    }

    public static URL getAssetURL(String assetPath) throws MalformedURLException
    {
        return new URL("file:///android_asset/" + assetPath);
    }
}
