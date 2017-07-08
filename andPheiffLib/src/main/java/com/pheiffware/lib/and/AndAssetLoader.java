package com.pheiffware.lib.and;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.pheiffware.lib.AssetLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation backing the AssetLoader interface.   An AssetLoader reference should be given to a user, while a reference
 * to this kept in the background.
 * When appropriate the destroy method should be called to make sure the underlying AssetManager reference is not maintained.
 * Retaining a reference to this could keep the entire view/fragment/activity surrounding it from being deallocated.
 * This is especially true when the containing fragment's setRetainInstance(true) method was called.
 * <p>
 * Created by Steve on 4/23/2016.
 */
public class AndAssetLoader extends AssetLoader
{
    private AssetManager assetManager;

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

    /**
     * Cleanup!
     */
    public void destroy()
    {
        assetManager = null;
    }
}
