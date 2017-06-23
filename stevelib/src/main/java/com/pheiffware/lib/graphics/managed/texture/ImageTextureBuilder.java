package com.pheiffware.lib.graphics.managed.texture;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import com.pheiffware.lib.and.AndUtils;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;

import java.io.IOException;

/**
 * Created by Steve on 6/22/2017.
 */

public class ImageTextureBuilder extends TextureBuilder<Texture2D>
{
    private final AssetManager assetManager;
    private String imageAssetPath;

    public ImageTextureBuilder(TextureBinder textureBinder, FilterQuality defaultFilterQuality, boolean defaultGenerateMipMaps, AssetManager assetManager, String imageAssetPath)
    {
        super(textureBinder, defaultFilterQuality, defaultGenerateMipMaps);
        this.imageAssetPath = imageAssetPath;
        this.assetManager = assetManager;
    }

    @Override
    public Texture2D build() throws GraphicsException
    {
        try
        {
            Bitmap bitmap = AndUtils.loadBitmapAsset(assetManager, imageAssetPath);
            Texture2D texture = new Texture2D(textureBinder, bitmap.getWidth(), bitmap.getHeight());

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.handle);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();

            filterQuality.applyToBoundTexture2D(generateMipMaps);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, sWrap);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, tWrap);
            if (generateMipMaps)
            {
                GLES30.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
            }
            return texture;
        }
        catch (IOException exception)
        {
            throw new GraphicsException(exception);
        }
    }
}
