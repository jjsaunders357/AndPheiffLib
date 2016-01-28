/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody;

import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

//TODO: Remove this (since polygons don't need it anymore).
/**
 * Basic information a point of impact between to rigid bodies
 */
public class PointOfImpact
{
	public final Vec3F collisionNormal;
	public final float penetration;

	public PointOfImpact(Vec3F collisionNormal, float penetration)
	{
		super();
		this.collisionNormal = collisionNormal;
		this.penetration = penetration;
	}
}
