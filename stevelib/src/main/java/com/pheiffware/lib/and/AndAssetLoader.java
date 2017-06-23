package com.pheiffware.lib.and;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.pheiffware.lib.AssetLoader;

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

    @Override
    public Bitmap loadBitmap(String assetPath) throws IOException
    {
        return AndUtils.loadBitmapAsset(assetManager, assetPath);
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
