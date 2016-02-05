/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.entity.physicalEntity;

import com.pheiffware.andpheifflib.sphere.engine.physics.InteractionException;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.Entity;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

/**
 * An entity with the concept of mass, force, acceleration, etc.
 */
// TODO: Remove rigid body package.
public abstract class PhysicalEntity extends Entity
{
	// Consider object stopped if its velocity falls below this
	private static final float STOPPED_VELOCITY_SQUARED = (float) Math.pow(0.0000001f, 2);

	// The position of the entity (generally the center)
	public final Vec3F center;

	// The velocity of the entity
	public final Vec3F velocity;

	// How much relative velocity between objects is conserved. The coefficients of the 2 objects are multiplied.
	public final float coefficientOfRestitution;

	// The mass
	public final float mass;

	// Pre-calculated inverse
	public final float inverseMass;

	// Pre-calculated root
	public final float sqrtMass;

	// Used to accumulate total force acting on entity during a time step
	private final Vec3F accumulatedForce;

	// (duh)
	private boolean ignoresGravity = false;

	public PhysicalEntity(Vec3F center, Vec3F velocity, float mass, float coefficientOfRestitution)
	{
		this.center = new Vec3F(center);
		this.velocity = new Vec3F(velocity);
		this.coefficientOfRestitution = coefficientOfRestitution;
		this.mass = mass;
		if (mass == Float.POSITIVE_INFINITY)
		{
			inverseMass = 0;
			sqrtMass = Float.POSITIVE_INFINITY;
		}
		else
		{
			inverseMass = 1.0f / mass;
			sqrtMass = (float) Math.sqrt(mass);
		}
		accumulatedForce = new Vec3F(0, 0, 0);
	}

	public void updateMotion(float elapsedTime)
	{
		float ax = accumulatedForce.x * inverseMass;
		float ay = accumulatedForce.y * inverseMass;
		float az = accumulatedForce.z * inverseMass;
		float atFactor = 0.5f * elapsedTime * elapsedTime;
		float tx = ax * atFactor + velocity.x * elapsedTime;
		float ty = ay * atFactor + velocity.y * elapsedTime;
		float tz = az * atFactor + velocity.z * elapsedTime;
		velocity.addTo(ax * elapsedTime, ay * elapsedTime, az * elapsedTime);
		move(tx, ty, tz);
		accumulatedForce.toZero();
	}

	public abstract void resolveCollision(PhysicalEntity physicalEntity, float elapsedTime) throws InteractionException;

	/**
	 * Move the entity's center and update all other related information such as bounding volume.
	 * 
	 * @param translation
	 */
	public void move(final float tx, final float ty, final float tz)
	{
		center.addTo(tx, ty, tz);
	}

	/**
	 * Convenience method for move (should not be used where efficiency matters)
	 * 
	 * @param translation
	 */
	public final void move(final Vec3F translation)
	{
		move(translation.x, translation.y, translation.z);
	}

	public final boolean hasMotionStopped()
	{
		return velocity.magnitudeSquared() < STOPPED_VELOCITY_SQUARED;
	}

	public final float getCoefficientOfRestitution()
	{
		return coefficientOfRestitution;
	}

	public void addForce(final Vec3F direction, final float magnitude)
	{
		accumulatedForce.x += direction.x * magnitude;
		accumulatedForce.y += direction.y * magnitude;
		accumulatedForce.z += direction.z * magnitude;
	}

	public boolean ignoresGravity()
	{
		return ignoresGravity;
	}

	public void setIgnoresGravity(boolean ignoreGravity)
	{
		this.ignoresGravity = ignoreGravity;
	}

	public void applyImpulse(final Vec3F impulse)
	{
		velocity.addTo(impulse);
	}

	public void applyImpulse(final Vec3F impulseNormal, final float magnitude)
	{
		velocity.addToScaledVector(impulseNormal, magnitude);
	}
}
