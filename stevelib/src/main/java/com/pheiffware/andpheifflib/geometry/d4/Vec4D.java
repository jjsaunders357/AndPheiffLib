package com.pheiffware.andpheifflib.geometry.d4;

public class Vec4D
{
	public double x, y, z, w;

	public static final Vec4D add(final Vec4D v1, final Vec4D v2)
	{
		return new Vec4D(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z, v1.w + v2.w);
	}

	public static final Vec4D scale(Vec4D vec, double scale)
	{
		return new Vec4D(vec.x * scale, vec.y * scale, vec.z * scale, vec.w
				* scale);
	}

	public static Vec4D normalize(final Vec4D v1)
	{
		final double magnitude = v1.magnitude();
		final Vec4D result = new Vec4D(v1.x / magnitude, v1.y / magnitude, v1.z
				/ magnitude, v1.w / magnitude);
		return result;
	}

	public static Vec4D lerp(final Vec4D v1, final Vec4D v2, double weight)
	{
		return add(scale(v1, 1 - weight), scale(v2, weight));
	}

	public Vec4D(final double x, final double y, final double z, final double w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public final double magnitude()
	{
		return (double) Math.sqrt(magnitudeSquared());
	}

	public final double magnitudeSquared()
	{
		return x * x + y * y + z * z + w * w;
	}

	public final String toString()
	{
		return String.format("[%.3f,%.3f,%.3f,%.3f]", x, y, z, w);
	}
}
