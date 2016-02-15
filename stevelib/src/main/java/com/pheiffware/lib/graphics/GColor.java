package com.pheiffware.lib.graphics;

/**
 * Created by Steve on 2/14/2016.
 */
public class GColor
{
    private final float[] comps;

    public GColor(float[] comps)
    {
        this.comps = comps;
    }

    public GColor(float red, float green, float blue, float alpha)
    {
        comps = new float[]{red, green, blue, alpha};
    }
}
