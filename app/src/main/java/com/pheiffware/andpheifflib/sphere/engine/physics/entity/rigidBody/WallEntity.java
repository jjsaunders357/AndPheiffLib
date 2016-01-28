/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody;

import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

/**
 * A static wall.
 */
public class WallEntity extends LineSegmentEntity
{
	public WallEntity(Vec3F p1, Vec3F p2, int normalSide, Vec3F velocity, float coefficientOfRestitution)
	{
		super(p1, p2, normalSide, velocity, Float.POSITIVE_INFINITY, coefficientOfRestitution);
	}

	@Override
	public void updateMotion(float elapsedTime)
	{
		// Never moves
	}
}
