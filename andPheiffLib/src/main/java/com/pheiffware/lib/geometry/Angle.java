package com.pheiffware.lib.geometry;

/**
 * When doing calculations with angle, sin/cos is what usually matters.  This abstracts angle to these quantities and radians/degrees conversions.
 * Created by Steve on 7/8/2017.
 */

public class Angle
{
    public static final float radiansPerDegree = (float) (Math.PI / 180.0);
    public static final float degreesPerRadian = (float) (180.0 / Math.PI);

    public static Angle newAtan(float y, float x)
    {
        return new Angle((float) Math.atan2(y, x));
    }

    public static Angle newRadians(float radians)
    {
        return new Angle(radians);
    }

    public static Angle newDegrees(float degrees)
    {
        float radians = radiansPerDegree * degrees;
        return new Angle(radians);
    }

    public float cos;
    public float sin;

    private Angle(float radians)
    {
        this.cos = (float) Math.cos(radians);
        this.sin = (float) Math.sin(radians);
    }

    public final void setDegrees(float degrees)
    {
        float radians = radiansPerDegree * degrees;
        this.cos = (float) Math.cos(radians);
        this.sin = (float) Math.sin(radians);
    }

    public final void setRadians(float radians)
    {
        this.cos = (float) Math.cos(radians);
        this.sin = (float) Math.sin(radians);
    }

    public final void setXY(float x, float y)
    {
        float radians = (float) Math.atan2(y, x);
        setRadians(radians);
    }

    /**
     * Set the state of the angle, to the negative number of radians/degrees.
     */
    public void negate()
    {
        sin = -sin;
    }
}
