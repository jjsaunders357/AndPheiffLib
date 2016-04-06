package com.pheiffware.lib.graphics.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;

import com.pheiffware.lib.graphics.GraphicsException;

/**
 * Created by Steve on 2/9/2016.
 */
public class PheiffGLUtils
{
    //3.0
    //Useful reference for remembering the minimum amount openGL has to provide
    public static final int MINMAX_TEXTURE_SIZE = 2048;
    public static final int MINMAX_3D_TEXTURE_SIZE = 256;
    public static final int MINMAX_COLOR_ATTACHMENTS = 4;
    public static final int MINMAX_VERTEX_ATTRIBUTES = 16;
    public static final int MINMAX_VERTEX_UNIFORM_VECTORS = 256; //Includes constants and immediates.  This is number of vectors (4 elements).  See special rules about how this works.
    public static final int MINMAX_FRAGMENT_UNIFORM_VECTORS = 224;


    public static final int GL_VERSION_31 = 0x30001;
    public static final int GL_VERSION_30 = 0x30000;
    public static final int GL_VERSION_20 = 0x20000;
    public static final int GL_VERSION_11 = 0x10001;
    public static final int GL_VERSION_10 = 0x10000;


    /**
     * Get the major openGL version that is actually available on this device (ie 3).
     *
     * @param context
     * @return
     */
    public static int getDeviceGLMajorVersion(Context context)
    {
        int deviceGLVersion = getDeviceGLVersion(context);
        return deviceGLVersion >> 4;
    }

    /**
     * Gets the openGL version that is actually available on this device (ie GL_VERSION_31).
     *
     * @param context
     * @return
     */
    public static int getDeviceGLVersion(Context context)
    {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager
                .getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion;
    }

    /**
     * Get the dimension of a gl semantic Example: GL_FLOAT_VEC2 has dimension 2
     *
     * @param type gl semantic constant
     * @return dimension
     */
    public static int getGLTypeDims(int type)
    {
        switch (type)
        {
            case GLES20.GL_FLOAT:
                return 1;
            case GLES20.GL_FLOAT_VEC2:
                return 2;
            case GLES20.GL_FLOAT_VEC3:
                return 3;
            case GLES20.GL_FLOAT_VEC4:
                return 4;
            case GLES20.GL_INT:
                return 1;
            case GLES20.GL_INT_VEC2:
                return 2;
            case GLES20.GL_INT_VEC3:
                return 3;
            case GLES20.GL_INT_VEC4:
                return 4;
            case GLES20.GL_BOOL:
                return 1;
            case GLES20.GL_BOOL_VEC2:
                return 2;
            case GLES20.GL_BOOL_VEC3:
                return 3;
            case GLES20.GL_BOOL_VEC4:
                return 4;
            case GLES20.GL_FLOAT_MAT2:
                return 4;
            case GLES20.GL_FLOAT_MAT3:
                return 9;
            case GLES20.GL_FLOAT_MAT4:
                return 16;
            default:
                throw new RuntimeException("Cannot get size of unsupported opengl semantic: " + type);
        }
    }

    /**
     * Get the "base semantic" for a gl semantic Example, for GL_FLOAT_VEC4, the base semantic is GL_FLOAT.
     *
     * @param type the gl semantic
     * @return the corresponding base semantic
     */
    public static int getGLBaseType(int type)
    {
        switch (type)
        {
            case GLES20.GL_FLOAT:
                return GLES20.GL_FLOAT;
            case GLES20.GL_FLOAT_VEC2:
                return GLES20.GL_FLOAT;
            case GLES20.GL_FLOAT_VEC3:
                return GLES20.GL_FLOAT;
            case GLES20.GL_FLOAT_VEC4:
                return GLES20.GL_FLOAT;
            case GLES20.GL_INT:
                return GLES20.GL_INT;
            case GLES20.GL_INT_VEC2:
                return GLES20.GL_INT;
            case GLES20.GL_INT_VEC3:
                return GLES20.GL_INT;
            case GLES20.GL_INT_VEC4:
                return GLES20.GL_INT;
            case GLES20.GL_BOOL:
                return GLES20.GL_BOOL;
            case GLES20.GL_BOOL_VEC2:
                return GLES20.GL_BOOL;
            case GLES20.GL_BOOL_VEC3:
                return GLES20.GL_BOOL;
            case GLES20.GL_BOOL_VEC4:
                return GLES20.GL_BOOL;
            case GLES20.GL_FLOAT_MAT2:
                return GLES20.GL_FLOAT;
            case GLES20.GL_FLOAT_MAT3:
                return GLES20.GL_FLOAT;
            case GLES20.GL_FLOAT_MAT4:
                return GLES20.GL_FLOAT;
            default:
                throw new RuntimeException("Cannot get size of unsupported opengl semantic: " + type);
        }
    }

    /**
     * Get the size of a gl semantic
     *
     * @param type a semantic such as GLES20.GL_FLOAT
     * @return size in bytes of the semantic
     */
    public static int getGLTypeSize(int type)
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
                throw new RuntimeException("Cannot get size of unsupported opengl semantic: " + type);
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
