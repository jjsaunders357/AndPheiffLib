/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody;

import com.pheiffware.andpheifflib.sphere.engine.physics.PhysicsSystem;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

//TODO: Make this movement algorithm more generally applied to any entity
/**
 * 
 */
public class LineSegmentElevatorEntity extends LineSegmentEntity
{
	private Vec3F maxVelocity;
	private float acceleration;
	private Vec3F direction;

	public LineSegmentElevatorEntity(Vec3F p1, Vec3F p2, int normalSide, float mass, float coefficientOfRestitution, Vec3F maxVelocity,
			float acceleration)
	{
		super(p1, p2, normalSide, new Vec3F(0, 0, 0), mass, coefficientOfRestitution);
		this.maxVelocity = maxVelocity;
		this.direction = maxVelocity.getNormalizeVector();
		this.acceleration = acceleration;
		setIgnoresGravity(true);
	}

	@Override
	public void ai(float elapsedTime, PhysicsSystem physicsSystem)
	{
		// Figure out force required to get to full speed

		// Amount of speed in the direction of maxVelocity
		float requiredVelocity = Vec3F.subDot(maxVelocity, velocity, direction);

		// Required acceleration
		float requiredAcceleration;

		requiredAcceleration = requiredVelocity / elapsedTime;
		if (requiredAcceleration > acceleration)
		{
			requiredAcceleration = acceleration;
		}

		addForce(direction, requiredAcceleration * mass);
	}
}
