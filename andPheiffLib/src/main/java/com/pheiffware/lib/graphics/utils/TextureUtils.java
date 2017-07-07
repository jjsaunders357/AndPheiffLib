/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.graphics.utils;

import android.opengl.GLES20;
import android.opengl.GLES30;

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
     * Binds a the given texture handle to a sampler index.
     *
     * @param textureHandle
     * @param samplerIndex
     * @param textureType   the type of the texture such as GL_TEXTURE_2D
     */
    public static void bindTextureToSampler(int textureHandle, int samplerIndex, int textureType)
    {
        int samplerHandle = PheiffGLUtils.getGLTextureUnitHandle(samplerIndex);
        GLES20.glActiveTexture(samplerHandle);
        GLES20.glBindTexture(textureType, textureHandle);
    }
}
