package com.pheiffware.lib.and;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.utils.TextureUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * AssetLoader for Android.
 * <p/>
 * Created by Steve on 4/23/2016.
 */
public class AndAssetLoader extends AssetLoader
{
    private final AssetManager assetManager;

    public AndAssetLoader(AssetManager assetManager)
    {
        this.assetManager = assetManager;
    }

    /**
     * Loads a bitmap into a texture.
     *
     * @param bitmap          bitmap to load into text
     * @param generateMipMaps Set to true if it makes sense to try to use mip-maps for this texture. This may be ignored based on given filter quality.
     * @param filterQuality   HIGH/MEDIUM/LOW (look up my definition)
     * @param sWrapMode       typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrapMode       typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @return GL handle to texture
     * @throws GraphicsException
     */
    private int genTextureFromImage(Bitmap bitmap, boolean generateMipMaps,
                                    FilterQuality filterQuality, int sWrapMode, int tWrapMode) throws GraphicsException
    {
        int textureHandle = TextureUtils.genTexture();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        filterQuality.applyToBoundTexture(generateMipMaps);
        TextureUtils.setBoundTextureWrapParameters(sWrapMode, tWrapMode);
        return textureHandle;
    }

    /**
     * Loads an image into a newly created texture.
     *
     * @param assetPath       image path
     * @param generateMipMaps Set to true if it makes sense to try to use mip-maps for this texture. This may be ignored based on given filter quality.
     * @param filterQuality   HIGH/MEDIUM/LOW (look up my definition)
     * @param sWrapMode       typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrapMode       typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @return GL handle to texture
     * @throws GraphicsException
     */
    public int loadGLTextureFromImage(String assetPath, boolean generateMipMaps,
                                      FilterQuality filterQuality, int sWrapMode, int tWrapMode) throws GraphicsException
    {
        Bitmap bitmap;
        try
        {
            bitmap = AndUtils.loadBitmapAsset(assetManager, assetPath);
            return genTextureFromImage(bitmap, generateMipMaps, filterQuality, sWrapMode, tWrapMode);
        }
        catch (IOException exception)
        {
            throw new GraphicsException(exception);
        }
    }

    @Override
    public String loadAssetAsString(String assetPath) throws IOException
    {
        return AndUtils.loadAssetAsString(assetManager, assetPath);
    }

    @Override
    public InputStream getInputStream(String assetPath) throws IOException
    {
        return assetManager.open(assetPath);
    }
}
