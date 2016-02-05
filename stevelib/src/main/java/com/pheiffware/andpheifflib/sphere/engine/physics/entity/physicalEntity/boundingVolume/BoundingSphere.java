/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.entity.physicalEntity.boundingVolume;

import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

/**
 * A bounding sphere around an object.
 */
public class BoundingSphere
{
	public boolean overlapping(BoundingSphere otherSphere)
	{
		double distanceSquared = Vec3F.distanceSquared(center, otherSphere.center);
		return distanceSquared < (radius + otherSphere.radius) * (radius + otherSphere.radius);
	}

	private Vec3F center;
	private float radius;

	public BoundingSphere(Vec3F center, float radius)
	{
		this.center = center;
		this.radius = radius;
	}

	/**
	 * @param boundingCenter
	 * @param boundingRadius
	 */
	public final void modify(Vec3F center, float radius)
	{
		this.center = center;
		this.radius = radius;
	}

	public final void move(final float x, final float y, final float z)
	{
		center.addTo(x, y, z);
	}

	public final Vec3F getCenter()
	{
		return center;
	}

	public final float getRadius()
	{
		return radius;
	}

}
