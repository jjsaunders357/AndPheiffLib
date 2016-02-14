package com.pheiffware.lib.graphics.managed;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.pheiffware.lib.graphics.FatalGraphicsException;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.utils.TextureUtils;

/**
 * Created by Steve on 2/13/2016.
 */
public class Texture
{
    private final int handle;

    public Texture(int handle)
    {
        this.handle = handle;
    }

    public int getHandle()
    {
        return handle;
    }
}
