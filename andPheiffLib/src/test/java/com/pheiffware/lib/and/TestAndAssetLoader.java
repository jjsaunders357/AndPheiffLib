package com.pheiffware.lib.and;

import android.graphics.Bitmap;

import com.pheiffware.lib.AssetLoader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * An asset loader which can be used during local machine testing.  This simply loads files from the project's src/test/assets folder.
 * <p>
 * When running test, you must set the working directory to MODULE_DIR (use button on right in run configuration window)!!
 * Created by Steve on 8/3/2017.
 */

public class TestAndAssetLoader implements AssetLoader
{
    @Override
    public Bitmap loadBitmap(String assetPath) throws IOException
    {
        throw new RuntimeException("Unsuported feature");
    }

    @Override
    public String loadAssetAsString(String assetPath) throws IOException
    {
        FileInputStream input = getInputStream(assetPath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        int character;
        StringBuilder builder = new StringBuilder();
        do
        {
            character = reader.read();
            if (character == -1)
            {
                break;
            }
            else
            {
                builder.append((char) character);
            }
        } while (true);
        return builder.toString();
    }

    @Override
    public FileInputStream getInputStream(String assetPath) throws IOException
    {
        return new FileInputStream("src/test/assets/" + assetPath);
    }
}
