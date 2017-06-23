package com.pheiffware.lib.graphics.managed.texture;

import android.opengl.GLES20;

/**
 * Created by Steve on 6/20/2017.
 */

public class Texture2D extends Texture
{
    public Texture2D(TextureBinder textureBinder, int width, int height)
    {
        super(GLES20.GL_TEXTURE_2D, textureBinder);
    }



    @Override
    public void attach(int attachmentPoint)
    {
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, attachmentPoint, GLES20.GL_TEXTURE_2D, handle, attachmentLevel);
    }
}
