package com.pheiffware.lib.graphics;

import com.pheiffware.lib.geometry.Angle;

/**
 * For manipulating 1 or more vectors 4D vectors stored in float arrays.
 * Created by Steve on 7/8/2017.
 */

public class Vec4F
{
    //Data array holding all vectors
    private float[] data;

    //4 separate offsets are maintained
    private int offsetX;
    private int offsetY;
    private int offsetZ;
    private int offsetW;

    public Vec4F(float[] data, int offset)
    {
        this.data = data;
        this.offsetX = offset;
        this.offsetY = offset + 1;
        this.offsetZ = offset + 2;
        this.offsetW = offset + 3;
    }

    public final void set(float[] data, int offset)
    {
        this.data = data;
        this.offsetX = offset;
        this.offsetY = offset + 1;
        this.offsetZ = offset + 2;
        this.offsetW = offset + 3;
    }

    public final void setIndex(int vectorIndex)
    {
        int vectorOffset = vectorIndex * 4;
        this.offsetX = vectorOffset;
        this.offsetY = vectorOffset;
        this.offsetZ = vectorOffset;
        this.offsetW = vectorOffset;
    }

    public final boolean next()
    {
        offsetX += 4;
        offsetY += 4;
        offsetZ += 4;
        offsetW += 4;
        return offsetX < data.length;
    }

    public final float magnitudeSquared()
    {
        return data[offsetX] * data[offsetX] + data[offsetY] * data[offsetY] + data[offsetZ] * data[offsetZ] + data[offsetW] * data[offsetW];
    }

    public final float magnitude()
    {
        return (float) Math.sqrt(magnitudeSquared());
    }

    public static float dot(Vec4F v1, Vec4F v2)
    {
        return v1.x() * v2.x() + v1.y() * v2.y() + v1.z() * v2.z() + v1.w() * v2.w();
    }

    /**
     * Rotate this vector away from the given vector until perpendicular and normalize the result.
     *
     * @param perpendicular
     */
    public final void setPerpendicularUnit(Vec4F perpendicular)
    {
        float dot = dot(this, perpendicular);
        setX(x() - perpendicular.x() * dot);
        setY(y() - perpendicular.y() * dot);
        setZ(z() - perpendicular.z() * dot);
        setW(w() - perpendicular.w() * dot);
        normalize();
    }

    private void normalize()
    {
        float magnitude = magnitude();
        scale(1f / magnitude);
    }

    public void scale(float scale)
    {
        setX(x() / scale);
        setY(y() / scale);
        setZ(z() / scale);
        setW(w() / scale);
    }

    /**
     * Rotate this towards another vector which is perpendicular, by a given amount.
     *
     * @param towardsPerp
     * @param angle
     */
    public final void rotateTowardsPerp(Vec4F towardsPerp, Angle angle)
    {
        setValues(
                x() * angle.cos + towardsPerp.x() * angle.sin,
                y() * angle.cos + towardsPerp.y() * angle.sin,
                z() * angle.cos + towardsPerp.z() * angle.sin,
                w() * angle.cos + towardsPerp.w() * angle.sin
        );
    }

    public final void setValues(float x, float y, float z, float w)
    {
        data[offsetX] = x;
        data[offsetY] = y;
        data[offsetZ] = z;
        data[offsetW] = w;
    }

    public final float setX(float x)
    {
        return data[offsetX] = x;
    }

    public final float setY(float y)
    {
        return data[offsetY] = y;
    }

    public final float setZ(float z)
    {
        return data[offsetZ] = z;
    }

    public final float setW(float w)
    {
        return data[offsetW] = w;
    }

    public final float x()
    {
        return data[offsetX];
    }

    public final float y()
    {
        return data[offsetY];
    }

    public final float z()
    {
        return data[offsetZ];
    }

    public final float w()
    {
        return data[offsetW];
    }

}
