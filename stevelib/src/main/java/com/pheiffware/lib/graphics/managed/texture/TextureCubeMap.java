package com.pheiffware.lib.graphics.managed.texture;

import android.opengl.GLES20;

/**
 * Created by Steve on 6/20/2017.
 */

public class TextureCubeMap extends Texture
{
    //Face of the texture to attach for rendering (example: GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z)
    private int attachFace;
    private final int width;
    private final int height;

    public TextureCubeMap(TextureBinder textureBinder, int width, int height)
    {
        super(GLES20.GL_TEXTURE_CUBE_MAP, textureBinder);
        this.width = width;
        this.height = height;
    }

    @Override
    public void attach(int attachmentPoint)
    {
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, attachmentPoint, attachFace, handle, attachmentLevel);
    }

    /**
     * Set the face to render to when attached to a frame buffer
     *
     * @param attachFace face of the texture to attach for rendering (example: GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z)
     */
    public void setAttachFace(int attachFace)
    {
        this.attachFace = attachFace;
    }
}
