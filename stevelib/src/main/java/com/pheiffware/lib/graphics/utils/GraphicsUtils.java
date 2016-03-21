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

    /**
     * Given an array of non-homogeneous 3d vectors, create a new array homogeneous vectors, append the given element4 to all vectors and create a new array.
     *
     * @param nonHomogeneousVectors float data of the form [x1 y1 z1 x2 y2 z2 ...]
     * @param element4              the value for "w" coordinate to add
     * @return float data of the form [x1 y1 z1 w x2 y2 z2 w ...]
     */
    public static float[] homogenizeVec3Array(float[] nonHomogeneousVectors, float element4)
    {
        float[] homogeneousVectors = new float[(nonHomogeneousVectors.length / 3) * 4];
        int homogeneousIndex = 0, nonHomogeneousIndex = 0;
        while (homogeneousIndex < homogeneousVectors.length)
        {
            homogeneousVectors[homogeneousIndex++] = nonHomogeneousVectors[nonHomogeneousIndex++];
            homogeneousVectors[homogeneousIndex++] = nonHomogeneousVectors[nonHomogeneousIndex++];
            homogeneousVectors[homogeneousIndex++] = nonHomogeneousVectors[nonHomogeneousIndex++];
            homogeneousVectors[homogeneousIndex++] = element4;
        }
        return homogeneousVectors;
    }
}
