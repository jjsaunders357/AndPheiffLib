package com.pheiffware.lib.graphics.managed.texture.textureBuilders;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.texture.TextureBinder;
import com.pheiffware.lib.graphics.managed.texture.TextureCubeMap;

/**
 * Created by Steve on 6/22/2017.
 */

public class CubeColorRenderTextureBuilder extends TextureBuilder<TextureCubeMap>
{
    private final int width;
    private final int height;

    public CubeColorRenderTextureBuilder(TextureBinder textureBinder, FilterQuality defaultFilterQuality, int width, int height)
    {
        super(textureBinder, defaultFilterQuality, false);
        this.width = width;
        this.height = height;
    }


    @Override
    public TextureCubeMap build() throws GraphicsException
    {
        TextureCubeMap texture = new TextureCubeMap(textureBinder, width, height);
        int internalFormat;
        if (hasAlpha)
        {
            internalFormat = GLES20.GL_RGBA;
        }
        else
        {
            internalFormat = GLES20.GL_RGB;
        }

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, internalFormat, width, height, 0, internalFormat,
                GLES20.GL_UNSIGNED_SHORT, null);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, internalFormat, width, height, 0, internalFormat,
                GLES20.GL_UNSIGNED_SHORT, null);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, internalFormat, width, height, 0, internalFormat,
                GLES20.GL_UNSIGNED_SHORT, null);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, internalFormat, width, height, 0, internalFormat,
                GLES20.GL_UNSIGNED_SHORT, null);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, internalFormat, width, height, 0, internalFormat,
                GLES20.GL_UNSIGNED_SHORT, null);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, internalFormat, width, height, 0, internalFormat,
                GLES20.GL_UNSIGNED_SHORT, null);

        filterQuality.applyToBoundTexture2D(generateMipMaps);
        return texture;

    }
}
