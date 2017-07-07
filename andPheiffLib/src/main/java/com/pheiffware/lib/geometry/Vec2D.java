package com.pheiffware.lib.geometry;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by Steve on 3/9/2016.
 */
public class Vec2D
{
    public static Vec2D add(Vec2D v1, Vec2D v2)
    {
        return new Vec2D(v1.x + v2.x, v1.y + v2.y);
    }

    public static Vec2D sub(Vec2D v1, Vec2D v2)
    {
        return new Vec2D(v1.x - v2.x, v1.y - v2.y);
    }

    public double x, y;

    public Vec2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public void addTo(double x, double y)
    {
        this.x += x;
        this.y += y;
    }

    public final void addTo(final Vec2D vec)
    {
        x += vec.x;
        y += vec.y;
    }

    public void scaleBy(final double scale)
    {
        x *= scale;
        y *= scale;
    }

    public void scaleBy(Vec2D scale)
    {
        x *= scale.x;
        y *= scale.y;
    }

    public final double magnitudeSquared()
    {
        return x * x + y * y;
    }

    public final double magnitude()
    {
        return Math.sqrt(magnitudeSquared());
    }

    public final void rotate90()
    {
        double temp = x;
        x = y;
        y = -temp;
    }

    /**
     * Will return meaningless result for 0,0.
     *
     * @return
     */
    public final double getAngle()
    {
        return Math.atan2(y, x);
    }

    public final double getRelativeAngle(final Vec3D v1)
    {
        double angle;
        angle = v1.getAngle2D() - getAngle();
        if (angle < 0.0)
        {
            angle += 2 * PI;
        }
        return angle;
    }

    public final void rotate(final double cosAngle, final double sinAngle)
    {
        double temp = x * cosAngle - y * sinAngle;
        y = x * sinAngle + y * cosAngle;
        x = temp;
    }

    public final void rotate(final double angleRadians)
    {
        double c = cos(angleRadians);
        double s = sin(angleRadians);
        rotate(c, s);
    }

    public final void rotateAround(final double angleRadians, final Vec3D centerOfRotation)
    {
        x -= centerOfRotation.x;
        y -= centerOfRotation.y;
        rotate(angleRadians);
        x += centerOfRotation.x;
        y += centerOfRotation.y;
    }

    @Override
    public String toString()
    {
        return String.format("(%1.2f,%1.2f)", x, y);
    }

}
