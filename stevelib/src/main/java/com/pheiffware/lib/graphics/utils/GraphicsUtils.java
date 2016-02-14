package com.pheiffware.lib.graphics.utils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

/**
 * Created by Steve on 2/13/2016.
 */
public class GraphicsUtils
{
    /**
     * Load an image from an asset file.
     *
     * @param assetManager
     * @param imageAssetPath Path to image
     * @return image object
     * @throws IOException
     */
    public static Bitmap loadBitmapAsset(AssetManager assetManager, String imageAssetPath) throws IOException
    {
        return BitmapFactory.decodeStream(assetManager.open(imageAssetPath));
    }
}
