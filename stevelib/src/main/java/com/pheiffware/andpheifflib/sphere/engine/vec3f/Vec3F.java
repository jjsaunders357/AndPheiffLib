package com.pheiffware.andpheifflib.sphere.engine.vec3f;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Represents a 3d vector/point.
 */
public class Vec3F
{
	public static final Vec3F add(final Vec3F v1, final Vec3F v2)
	{
		return new Vec3F(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}

	public static final Vec3F sub(final Vec3F v1, final Vec3F v2)
	{
		return new Vec3F(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}

	public static final float dot(final Vec3F v1, final Vec3F v2)
	{
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}

	public static final Vec3F cross(final Vec3F v1, final Vec3F v2)
	{
		return new Vec3F(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x);
	}

	public static final float subDot(Vec3F vec1, Vec3F vec2, Vec3F dotVec)
	{
		return (vec1.x - vec2.x) * dotVec.x + (vec1.y - vec2.y) * dotVec.y + (vec1.z - vec2.z) * dotVec.z;
	}

	public static final Vec3F scale(Vec3F vec, float scale)
	{
		return new Vec3F(vec.x * scale, vec.y * scale, vec.z * scale);
	}

	public static float distance(final Vec3F v1, final Vec3F v2)
	{
		return (float) Math.sqrt(distanceSquared(v1, v2));
	}

	public static float distanceSquared(final Vec3F v1, final Vec3F v2)
	{
		float xdiff = (v1.x - v2.x);
		float ydiff = (v1.x - v2.x);
		float zdiff = (v1.x - v2.x);
		return xdiff * xdiff + ydiff * ydiff + zdiff * zdiff;
	}

	public float x, y, z;

	public Vec3F(final float x, final float y, final float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3F(final Vec3F vec)
	{
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	public final void toZero()
	{
		x = 0;
		y = 0;
		z = 0;
	}

	public void set(Vec3F vec)
	{
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	public final float magnitudeSquared()
	{
		return x * x + y * y + z * z;
	}

	public final float magnitude()
	{
		return (float) Math.sqrt(magnitudeSquared());
	}

	public final Vec3F getNormalizeVector()
	{
		final float magnitude = magnitude();
		final Vec3F result = new Vec3F(x / magnitude, y / magnitude, z / magnitude);
		return result;
	}

	public void normalize()
	{
		final float magnitude = magnitude();
		x = x / magnitude;
		y = y / magnitude;
		z = z / magnitude;
	}

	public final void addTo(final float x, final float y, final float z)
	{
		this.x += x;
		this.y += y;
		this.z += z;
	}

	public final void addTo(final Vec3F vec)
	{
		x += vec.x;
		y += vec.y;
		z += vec.z;
	}

	public final void addToScaledVector(final Vec3F vec, final float scaleVec)
	{
		x += vec.x * scaleVec;
		y += vec.y * scaleVec;
		z += vec.z * scaleVec;
	}

	public final void subFrom(final float x, final float y, final float z)
	{
		this.x -= x;
		this.y -= y;
		this.z -= z;
	}

	public final void subFromScaledVector(final Vec3F vec, final float scaleVec)
	{
		x -= vec.x * scaleVec;
		y -= vec.y * scaleVec;
		z -= vec.z * scaleVec;
	}

	public final void subFrom(final Vec3F vec)
	{
		x -= vec.x;
		y -= vec.y;
		z -= vec.z;
	}

	public final void scaleBy(final float scale)
	{
		x *= scale;
		y *= scale;
		z *= scale;
	}

	/**
	 * Cross product:
	 * vec X this
	 * 
	 * @param collisionNormal
	 */
	public void crossByLeft(Vec3F vec)
	{
		float tempx = vec.y * z - vec.z * y;
		float tempy = vec.z * x - vec.x * z;
		z = vec.x * y - vec.y * x;
		x = tempx;
		y = tempy;
	}

	/**
	 * Cross product:
	 * this X vec
	 * 
	 * @param vec
	 */
	public void crossByRight(Vec3F vec)
	{
		float tempx = y * vec.z - z * vec.y;
		float tempy = z * vec.x - x * vec.z;
		z = x * vec.y - y * vec.x;
		x = tempx;
		y = tempy;
	}

	// **************************2D special functions**************************
	// These all operate on the vector's x,y components, treating them as though
	// they are in a
	// plane.
	public final float getAngle2D()
	{
		return (float) Math.atan2(y, x);
	}

	public final float getRelativeAngle2D(final Vec3F v1)
	{
		float angle;
		angle = v1.getAngle2D() - getAngle2D();
		if (angle < 0.0)
			angle += 2 * PI;
		return angle;
	}

	public final void rotate2D(final float cosAngle, final float sinAngle)
	{
		float temp = x * cosAngle - y * sinAngle;
		y = x * sinAngle + y * cosAngle;
		x = temp;
	}

	public final void rotate2D(final float angle)
	{
		float temp = (float) (x * cos(angle) - y * sin(angle));
		y = (float) (x * sin(angle) + y * cos(angle));
		x = temp;
	}

	@Override
	public String toString()
	{
		return "(" + x + "," + y + "," + z + ")";
	}

	// @Override
	// public boolean equals(Object obj)
	// {
	// Vec3F vec3F = (Vec3F) obj;
	// if (vec3F.x == x && vec3F.y == y && vec3F.z == z)
	// {
	// return true;
	// }
	// else
	// {
	// return false;
	// }
	// }
	//
	// @Override
	// public int hashCode()
	// {
	// return Float.floatToRawIntBits(x) + Float.floatToRawIntBits(y) * 37 + Float.floatToRawIntBits(z) * (10001);
	// }

}
