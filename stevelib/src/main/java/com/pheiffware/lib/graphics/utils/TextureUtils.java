/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.graphics.utils;

import java.io.IOException;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.pheiffware.lib.graphics.FatalGraphicsException;
import com.pheiffware.lib.graphics.FilterQuality;

/**
 * A number of basic utilities for setting up/using textures and loading images into them.
 */
public class TextureUtils
{

    /**
     * Load an image from an asset file.
     *
     * @param assetManager
     * @param imageAssetPath Path to image
     * @return image object
     * @throws IOException
     */
    public static Bitmap loadAssetImage(AssetManager assetManager, String imageAssetPath) throws IOException
    {
        return BitmapFactory.decodeStream(assetManager.open(imageAssetPath));
    }

    /**
     * Generate a single opengl texture and get its handle.
     *
     * @return GL handle to texture
     */
    public static int genTexture()
    {
        int[] textureHandles = new int[1];
        GLES20.glGenTextures(1, textureHandles, 0);
        return textureHandles[0];
    }

    /**
     * Loads an image into a newly created texture.
     *
     * @param assetManager
     * @param imageAssetPath  image path
     * @param generateMipMaps Set to true if it makes sense to try to use mip-maps for this texture. This may be ignored based on given filter quality.
     * @param filterQuality   HIGH/MEDIUM/LOW (look up my definition)
     * @param sWrapMode       typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrapMode       typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @return GL handle to texture
     * @throws FatalGraphicsException
     */
    public static int genTextureFromImage(AssetManager assetManager, String imageAssetPath, boolean generateMipMaps,
                                          FilterQuality filterQuality, int sWrapMode, int tWrapMode) throws FatalGraphicsException
    {
        Bitmap bitmap;
        try
        {
            bitmap = loadAssetImage(assetManager, imageAssetPath);
        }
        catch (IOException exception)
        {
            throw new FatalGraphicsException(exception);
        }
        int textureHandle = genTexture();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        filterQuality.applyToBoundTexture(generateMipMaps);
        setBoundTextureWrapParameters(sWrapMode, tWrapMode);
        return textureHandle;
    }

    /**
     * Generates a texture which can have colors rendered onto it.
     *
     * @param pixelWidth    width
     * @param pixelHeight   height
     * @param alpha         should there be an alpha channel?
     * @param filterQuality HIGH/MEDIUM/LOW (look up my definition)
     * @param sWrapMode     typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrapMode     typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @return GL handle to texture
     */
    public static int genTextureForColorRendering(int pixelWidth, int pixelHeight, boolean alpha, FilterQuality filterQuality, int sWrapMode,
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
        filterQuality.applyToBoundTexture(false);
        setBoundTextureWrapParameters(sWrapMode, tWrapMode);
        return textureHandle;
    }

    /**
     * Generates a texture which can have depth rendered onto it.
     *
     * @param pixelWidth    width
     * @param pixelHeight   height
     * @param filterQuality HIGH/MEDIUM/LOW (look up my definition)
     * @param sWrapMode     typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrapMode     typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @return GL handle to texture
     */
    public static int genTextureForDepthRendering(int pixelWidth, int pixelHeight, FilterQuality filterQuality, int sWrapMode, int tWrapMode)
    {
        int textureHandle = genTexture();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT, pixelWidth, pixelHeight, 0, GLES20.GL_DEPTH_COMPONENT,
                GLES20.GL_UNSIGNED_SHORT, null);
        filterQuality.applyToBoundTexture(false);
        setBoundTextureWrapParameters(sWrapMode, tWrapMode);
        return textureHandle;
    }

    /**
     * Sets wrap mode for the currently bound texture
     *
     * @param sWrapMode typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrapMode typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     */
    public static void setBoundTextureWrapParameters(int sWrapMode, int tWrapMode)
    {
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, sWrapMode);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, tWrapMode);
    }

    /**
     * Set the active texture unit.  This is much more convenient than mucking around with constants like GLES20.GL_TEXTURE2.
     *
     * @param textureUnitIndex
     */
    public static void setActiveTextureUnit(int textureUnitIndex)
    {
        int textureUnitHandle = PheiffGLUtils.getGLTextureUnitHandle(textureUnitIndex);
        GLES20.glActiveTexture(textureUnitHandle);

    }

    /**
     * Sets up a texture as a uniform input for the given program.  This uses the specified texture unit.
     * If there a multiple texture inputs for the program each should be given a different text unit.
     *
     * @param programHandle    GL handle to the program
     * @param samplerParamName The name of the uniform sampler variable to be assigned the textureHandle
     * @param textureHandle    GL handle to texture
     * @param textureUnitIndex the index of the texture unit to use
     */
    public static void uniformTexture2D(int programHandle, String samplerParamName, int textureHandle, int textureUnitIndex)
    {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
        setActiveTextureUnit(textureUnitIndex);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(programHandle, samplerParamName), textureUnitIndex);
    }
}
