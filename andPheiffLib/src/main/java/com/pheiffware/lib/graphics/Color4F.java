package com.pheiffware.lib.graphics;

/**
 * Created by Steve on 2/14/2016.
 */
public class Color4F
{
    public final float[] comps;

    public Color4F(float[] comps)
    {
        this.comps = comps;
    }

    public Color4F(float red, float green, float blue, float alpha)
    {
        comps = new float[]{red, green, blue, alpha};
    }

    public float getRed()
    {
        return comps[0];
    }

    public float getGreen()
    {
        return comps[1];
    }

    public float getBlue()
    {
        return comps[2];
    }

    public float getAlpha()
    {
        return comps[3];
    }
    @Override
    public boolean equals(Object o)
    {
        Color4F other = (Color4F) o;
        return comps[0] == other.comps[0] &&
                comps[1] == other.comps[1] &&
                comps[2] == other.comps[2] &&
                comps[3] == other.comps[3];

    }
}
