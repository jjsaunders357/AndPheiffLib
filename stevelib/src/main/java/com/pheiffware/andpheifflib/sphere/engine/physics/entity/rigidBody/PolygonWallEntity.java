/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody;

import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

/**
 *
 */
public class PolygonWallEntity extends PolygonEntity
{
	public PolygonWallEntity(Vec3F velocity, float coefficientOfRestitution, Vec3F[] points)
	{
		super(velocity, Float.POSITIVE_INFINITY, coefficientOfRestitution, points);
	}

	@Override
	public void updateMotion(float elapsedTime)
	{
		// Never moves
	}
}
