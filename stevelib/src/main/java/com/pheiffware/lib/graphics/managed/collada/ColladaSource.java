package com.pheiffware.lib.graphics.managed.collada;

/**
 * Created by Steve on 2/15/2016.
 */
class ColladaSource
{
    public final int count;
    public final int stride;
    public final float[] floats;

    public ColladaSource(int count, int stride, float[] floats)
    {
        this.count = count;
        this.stride = stride;
        this.floats = floats;
    }

    public void transfer(int sourceIndex, float[] dest, int destIndex)
    {
        for (int i = 0; i < stride; i++)
        {
            dest[destIndex * stride + i] = floats[sourceIndex * stride + i];
        }
    }
}
