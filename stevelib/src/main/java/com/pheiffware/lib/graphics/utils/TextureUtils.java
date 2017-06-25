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

    public static int genCubeTextureForDepthRendering(int pixelWidth, int pixelHeight, FilterQuality filterQuality)
    {
        int textureHandle = genTexture();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureHandle);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GLES20.GL_DEPTH_COMPONENT, pixelWidth, pixelHeight, 0, GLES20.GL_DEPTH_COMPONENT,
                GLES20.GL_UNSIGNED_SHORT, null);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GLES20.GL_DEPTH_COMPONENT, pixelWidth, pixelHeight, 0, GLES20.GL_DEPTH_COMPONENT,
                GLES20.GL_UNSIGNED_SHORT, null);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GLES20.GL_DEPTH_COMPONENT, pixelWidth, pixelHeight, 0, GLES20.GL_DEPTH_COMPONENT,
                GLES20.GL_UNSIGNED_SHORT, null);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GLES20.GL_DEPTH_COMPONENT, pixelWidth, pixelHeight, 0, GLES20.GL_DEPTH_COMPONENT,
                GLES20.GL_UNSIGNED_SHORT, null);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GLES20.GL_DEPTH_COMPONENT, pixelWidth, pixelHeight, 0, GLES20.GL_DEPTH_COMPONENT,
                GLES20.GL_UNSIGNED_SHORT, null);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GLES20.GL_DEPTH_COMPONENT, pixelWidth, pixelHeight, 0, GLES20.GL_DEPTH_COMPONENT,
                GLES20.GL_UNSIGNED_SHORT, null);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_COMPARE_MODE, GLES30.GL_COMPARE_REF_TO_TEXTURE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_COMPARE_FUNC, GLES30.GL_LEQUAL);
        filterQuality.applyToBoundTexture2D(false);
        //TODO: Page 419
        //float texture (samplerCubeShadow sampler, vec4 P [, float bias] )
        return textureHandle;
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
