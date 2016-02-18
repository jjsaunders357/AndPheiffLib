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

    @Override
    public boolean equals(Object o)
    {
        GColor other = (GColor) o;
        return comps[0] == other.comps[0] &&
                comps[1] == other.comps[1] &&
                comps[2] == other.comps[2] &&
                comps[3] == other.comps[3];

    }
}
