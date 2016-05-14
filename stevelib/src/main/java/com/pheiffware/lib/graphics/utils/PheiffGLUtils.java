package com.pheiffware.lib.graphics.utils;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.GraphicsException;

/**
 * Created by Steve on 2/9/2016.
 */
public class PheiffGLUtils
{
    /**
     * Gets the byte size of a base type.
     * @param type
     * @return
     */
    public static int getGLBaseTypeByteSize(int type)
    {
        switch (type)
        {
            case GLES20.GL_BOOL:
                return 1;
            case GLES20.GL_SHORT:
                return 2;
            case GLES20.GL_INT:
                return 4;
            case GLES20.GL_FLOAT:
                return 4;
            default:
                throw new RuntimeException("Cannot get byte size of unsupported opengl basetype: " + type);
        }
    }

    /**
     * Looks up the appropraite texture unit handle for a given index.  Example: 2 - GLES20.GL_TEXTURE2
     *
     * @param textureUnitIndex The index of the texture unit
     * @return
     */
    public static int getGLTextureUnitHandle(int textureUnitIndex)
    {
        switch (textureUnitIndex)
        {
            case 0:
                return GLES20.GL_TEXTURE0;
            case 1:
                return GLES20.GL_TEXTURE1;
            case 2:
                return GLES20.GL_TEXTURE2;
            case 3:
                return GLES20.GL_TEXTURE3;
            case 4:
                return GLES20.GL_TEXTURE4;
            case 5:
                return GLES20.GL_TEXTURE5;
            case 6:
                return GLES20.GL_TEXTURE6;
            case 7:
                return GLES20.GL_TEXTURE7;
            default:
                throw new RuntimeException("Cannot get size of unsupported opengl texture unit index: " + textureUnitIndex);
        }
    }

    /**
     * Creates a new frame buffer object.  This is typically used when rendering to textures.
     *
     * @return GL handle to frame buffer
     */
    public static int createFrameBuffer()
    {
        int[] frameBufferHandles = new int[1];
        GLES20.glGenFramebuffers(1, frameBufferHandles, 0);
        return frameBufferHandles[0];
    }

    /**
     * Make a given frameBuffer and texture render targets active. Example of setup for rendering to viewport: PheiffGLUtils.bindFrameBuffer(0, -1, -1);
     * <p/>
     * Example of setup for rendering to a texture: PheiffGLUtils.bindFrameBuffer(frameBufferHandle, colorRenderTextureHandle, depthRenderTextureHandle);
     *
     * @param frameBufferHandle        GL handle to a frame buffer object to use OR 0 for the main framebuffer (viewport)
     * @param colorRenderTextureHandle GL handle to the texture where colors should be rendered or -1 to not use this feature
     * @param depthRenderTextureHandle GL handle to the texture where depth information should be rendered  or -1 to not use this feature
     */
    public static void bindFrameBuffer(int frameBufferHandle, int colorRenderTextureHandle, int depthRenderTextureHandle)
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle);
        if (frameBufferHandle != 0)
        {
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, colorRenderTextureHandle, 0);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, depthRenderTextureHandle, 0);
        }
    }

    public static void assertFrameBufferStatus() throws GraphicsException
    {
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE)
        {
            throw new GraphicsException("Framebuffer failure.  Status = " + status);
        }
    }

    public static void assertNoError() throws GraphicsException
    {
        int error = GLES20.glGetError();
        switch (error)
        {
            case GLES20.GL_NO_ERROR:
                return;
            case GLES20.GL_INVALID_ENUM:
                throw new GraphicsException("Illegal enum value");
            case GLES20.GL_INVALID_VALUE:
                throw new GraphicsException("Numeric value out of range");
            case GLES20.GL_INVALID_OPERATION:
                throw new GraphicsException("Operation illegal in current state");
            case GLES20.GL_INVALID_FRAMEBUFFER_OPERATION:
                throw new GraphicsException("Framebuffer  object  is  not  complete");
        }
    }

}
