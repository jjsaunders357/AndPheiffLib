package com.pheiffware.lib.graphics.managed;

import com.pheiffware.lib.graphics.utils.TextureUtils;

/**
 * Created by Steve on 2/13/2016.
 */
public class Texture
{
    //The type of the texture such as GL_TEXTURE_2D
    private final int type;

    //The openGL handler to this texture
    private final int handle;

    //The sampler this texture was last bound to in a call to bindToSampler().  This allows this object to be both a reference to the texture data as well as serve
    //as a sampler reference when appropriate.
    private int sampler;

    public Texture(int type, int handle)
    {
        this.type = type;
        this.handle = handle;
    }

    public int getHandle()
    {
        return handle;
    }

    public void bindToSampler(int sampler)
    {
        this.sampler = sampler;
        TextureUtils.bindTextureToSampler(handle, sampler, type);
    }

    public int getSampler()
    {
        return sampler;
    }
}
