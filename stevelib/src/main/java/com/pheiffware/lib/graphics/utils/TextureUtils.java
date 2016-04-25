/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.graphics.utils;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.FilterQuality;

/**
 * A number of basic utilities for setting up/using textures and loading images into them.
 */
public class TextureUtils
{
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
        //TODO: Could be replaced by a renderbuffer.  This supports multi-sampling.  After multi-sample, blit to a texture is required.  (opengl 3.0 only).
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
     * Binds a the given texture handle to a sampler index.
     * @param textureHandle
     * @param samplerIndex
     * @param textureType the type of the texture such as GL_TEXTURE_2D
     */
    public static void bindTextureToSampler(int textureHandle, int samplerIndex, int textureType)
    {
        int samplerHandle = PheiffGLUtils.getGLTextureUnitHandle(samplerIndex);
        GLES20.glActiveTexture(samplerHandle);
        GLES20.glBindTexture(textureType, textureHandle);
    }

    public static void bindTexture2DToSampler(int textureHandle, int samplerIndex)
    {
        bindTextureToSampler(textureHandle, samplerIndex, GLES20.GL_TEXTURE_2D);
    }
}
