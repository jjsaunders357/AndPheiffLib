/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.graphics;

import java.io.IOException;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.pheiffware.andpheifflib.sphere.engine.graphics.Settings.FilterQuality;

/**
 * A number of basic utilities for loading images and putting them in textures.
 */
public class ImageUtils
{

	/**
	 * Load a bitmap from the assets directory.
	 * 
	 * @param assetManager
	 * @param assetFileName
	 * @return
	 * @throws IOException
	 */
	public static Bitmap loadAssetImage(AssetManager assetManager, String assetFileName) throws IOException
	{
		return BitmapFactory.decodeStream(assetManager.open(assetFileName));
	}

	/**
	 * Generate an opengl texture and get its handle.
	 * 
	 * @return
	 */
	public static int genTexture()
	{
		int[] textureHandles = new int[1];
		GLES20.glGenTextures(1, textureHandles, 0);
		return textureHandles[0];
	}

	/**
	 * Loads a texture from the asset manager into a new texture, whose handle is returned.
	 * 
	 * Uses Settings to set filter quality. Sets wrap mode to clamp in both directions.
	 * 
	 * @param assetManager
	 * @param assetFileName
	 * @param generateMipMaps
	 * @return
	 * @throws GraphicsException
	 */
	public static int loadAssetImageIntoTexture(AssetManager assetManager, String assetFileName, boolean generateMipMaps) throws GraphicsException
	{
		return loadAssetImageIntoTexture(assetManager, assetFileName, generateMipMaps, Settings.instance.getFilterQuality(), GLES20.GL_CLAMP_TO_EDGE,
				GLES20.GL_CLAMP_TO_EDGE);
	}

	/**
	 * Loads a texture from the asset manager into a new texture, whose handle is returned.
	 * 
	 * @param assetManager
	 * @param assetFileName
	 * @param generateMipMaps
	 *            Set to true if it makes sense to try to use mip-maps for this texture. This may be ignored based on given filter quality.
	 * @param filterQuality
	 * @param sWrapMode
	 * @param tWrapMode
	 * @return
	 * @throws GraphicsException
	 */
	public static int loadAssetImageIntoTexture(AssetManager assetManager, String assetFileName, boolean generateMipMaps,
			FilterQuality filterQuality, int sWrapMode, int tWrapMode) throws GraphicsException
	{
		Bitmap bitmap;
		try
		{
			bitmap = loadAssetImage(assetManager, assetFileName);
		}
		catch (IOException exception)
		{
			throw new GraphicsException(exception);
		}
		int textureHandle = genTexture();
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
		setBoundTextureFilterParameters(generateMipMaps, filterQuality);
		setBoundTextureWrapParameters(sWrapMode, tWrapMode);
		return textureHandle;
	}

	public static int createColorRenderTexture(int pixelWidth, int pixelHeight, boolean alpha)
	{
		return createColorRenderTexture(pixelWidth, pixelHeight, alpha, Settings.instance.getFilterQuality(), GLES20.GL_CLAMP_TO_EDGE,
				GLES20.GL_CLAMP_TO_EDGE);
	}

	public static int createColorRenderTexture(int pixelWidth, int pixelHeight, boolean alpha, FilterQuality filterQuality, int sWrapMode,
			int tWrapMode)
	{
		int textureHandle = genTexture();
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		if (alpha)
		{
			GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, pixelWidth, pixelHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
		}
		else
		{
			GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, pixelWidth, pixelHeight, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, null);
		}
		setBoundTextureFilterParameters(false, filterQuality);
		setBoundTextureWrapParameters(sWrapMode, tWrapMode);
		return textureHandle;
	}

	public static int createDepthRenderTexture(int pixelWidth, int pixelHeight)
	{
		return createDepthRenderTexture(pixelWidth, pixelHeight, Settings.instance.getFilterQuality(), GLES20.GL_CLAMP_TO_EDGE,
				GLES20.GL_CLAMP_TO_EDGE);
	}

	public static int createDepthRenderTexture(int pixelWidth, int pixelHeight, FilterQuality filterQuality, int sWrapMode, int tWrapMode)
	{
		int textureHandle = genTexture();
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT, pixelWidth, pixelHeight, 0, GLES20.GL_DEPTH_COMPONENT,
				GLES20.GL_UNSIGNED_SHORT, null);
		setBoundTextureFilterParameters(false, filterQuality);
		setBoundTextureWrapParameters(sWrapMode, tWrapMode);
		return textureHandle;
	}

	public static void setBoundTextureFilterParameters(boolean generateMipMaps, FilterQuality filterQuality)
	{
		switch (filterQuality)
		{
		case HIGH:
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			if (generateMipMaps)
			{
				GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
			}
			else
			{
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			}
			break;
		case MEDIUM:
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			if (generateMipMaps)
			{
				GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST_MIPMAP_NEAREST);
			}
			else
			{
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			}
			break;
		case LOW:
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			break;
		}
	}

	public static void setBoundTextureWrapParameters(int sWrapMode, int tWrapMode)
	{
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, sWrapMode);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, tWrapMode);
	}

	/**
	 * Sets the given program parameter to use sampler 0. Then associates the given texture with sampler 0.
	 * 
	 * @param programHandle
	 * @param textureParameterName
	 * @param textureHandle
	 */
	public static void uniformTexture2D(int programHandle, String textureParameterName, int textureHandle)
	{
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		GLES20.glUniform1i(GLES20.glGetUniformLocation(programHandle, textureParameterName), 0);
	}

	/**
	 * Sets the given program parameter to use sampler 0. Then associates the given texture with sampler 0.
	 * 
	 * @param programHandle
	 * @param textureParameterName
	 * @param textureHandle
	 */
	public static void uniformTexture2D(int programHandle, String textureParameterName, int textureHandle, int samplerIndex)
	{
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + samplerIndex);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		GLES20.glUniform1i(GLES20.glGetUniformLocation(programHandle, textureParameterName), samplerIndex);
	}

	/**
	 * Creates a new frame buffer object.
	 * 
	 * @return
	 */
	public static int createFrameBuffer()
	{
		int[] frameBufferHandles = new int[1];
		GLES20.glGenFramebuffers(1, frameBufferHandles, 0);
		return frameBufferHandles[0];
	}

	public static void bindFrameBuffer(int frameBufferHandle, int colorRenderTextureHandle, int depthRenderTextureHandle)
	{
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle);
		if (frameBufferHandle != 0)
		{
			GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, colorRenderTextureHandle, 0);
			GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, depthRenderTextureHandle, 0);
		}
	}

}
