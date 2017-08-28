package com.pheiffware.lib.graphics.managed.texture;

import android.opengl.GLES20;

/**
 * Created by Steve on 8/27/2017.
 */

public class NullTexture extends Texture
{
    public NullTexture(int type, TextureBinder textureBinder)
    {
        super(0, type, textureBinder);
    }

    @Override
    public void attach(int attachmentPoint)
    {
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, attachmentPoint, type, 0, 0);
    }
}
