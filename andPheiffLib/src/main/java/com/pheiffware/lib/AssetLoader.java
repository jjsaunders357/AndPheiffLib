package com.pheiffware.lib;

import android.graphics.Bitmap;

import java.io.IOException;
import java.io.InputStream;

/**
 * Manages loading of files/assets/resources from disc for the library.
 * <p/>
 * Created by Steve on 4/23/2016.
 */
public interface AssetLoader
{

    Bitmap loadBitmap(String assetPath) throws IOException;

    /**
     * Load the contents of the given assetPath as a String.
     *
     * @param assetPath
     * @return
     */
    String loadAssetAsString(String assetPath) throws IOException;

    InputStream getInputStream(String assetPath) throws IOException;
}
