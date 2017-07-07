package com.pheiffware.lib;

import android.graphics.Bitmap;

import java.io.IOException;
import java.io.InputStream;

/**
 * Manages loading of files/resources from disc for the library.  Allows platform neutral ways of accessing resources, such as image files, shader code, etc.
 * <p/>
 * The assetPath argument to various methods, refers to some system resource such as a file path to load from.
 * <p/>
 * Created by Steve on 4/23/2016.
 */
public abstract class AssetLoader
{

    public abstract Bitmap loadBitmap(String assetPath) throws IOException;

    /**
     * Load the contents of the given assetPath as a String.
     *
     * @param assetPath
     * @return
     */
    public abstract String loadAssetAsString(String assetPath) throws IOException;

    public abstract InputStream getInputStream(String assetPath) throws IOException;
}
