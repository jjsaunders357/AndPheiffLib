package com.pheiffware.lib.graphics.managed.texture.textureBuilders;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.texture.Texture2D;
import com.pheiffware.lib.graphics.managed.texture.TextureBinder;

/**
 * Created by Steve on 6/22/2017.
 */

public class DepthRenderTextureBuilder extends TextureBuilder<Texture2D>
{
    private final int width;
    private final int height;

    public DepthRenderTextureBuilder(TextureBinder textureBinder, FilterQuality defaultFilterQuality, int width, int height)
    {
        super(textureBinder, defaultFilterQuality, false);
        this.width = width;
        this.height = height;
    }

    @Override
    public Texture2D build() throws GraphicsException
    {
        Texture2D texture = new Texture2D(textureBinder, width, height);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT, width, height, 0, GLES20.GL_DEPTH_COMPONENT, GLES20.GL_UNSIGNED_SHORT, null);

        filterQuality.applyToBoundTexture2D(generateMipMaps);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, sWrap);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, tWrap);
        return texture;
    }
}
