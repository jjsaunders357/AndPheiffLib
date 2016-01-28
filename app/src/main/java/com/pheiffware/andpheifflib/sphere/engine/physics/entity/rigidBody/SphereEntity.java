package com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody;

import com.pheiffware.andpheifflib.sphere.engine.physics.InteractionException;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.physicalEntity.PhysicalEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.physicalEntity.PhysicalEntityCollision;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

/**
 * Represents a spherical entity.
 */
public class SphereEntity extends PhysicalEntity
{
	private final float radius;

	public SphereEntity(final Vec3F center, final Vec3F velocity, final float mass, final float coefficientOfRestitution, final float radius)
	{
		super(center, velocity, mass, coefficientOfRestitution);
		this.radius = radius;
	}

	// TODO: Optimize
	public final static void resolveSphereSphereCollision(final SphereEntity sphere1, final SphereEntity sphere2)
	{
		float xdiff = sphere2.center.x - sphere1.center.x;
		float ydiff = sphere2.center.y - sphere1.center.y;
		float zdiff = sphere2.center.z - sphere1.center.z;

		float distance = (float) Math.sqrt(xdiff * xdiff + ydiff * ydiff + zdiff * zdiff);
		float penetration = sphere2.radius + sphere1.radius - distance;
		if (penetration > 0)
		{
			float invDistance = 1.0f / distance;
			PhysicalEntityCollision collision = new PhysicalEntityCollision(sphere1, sphere2, new Vec3F(xdiff * invDistance, ydiff * invDistance,
					zdiff * invDistance), penetration);
			collision.resolve();
		}
	}

	/**
	 * @return
	 */
	public final float getRadius()
	{
		return radius;
	}

	/* (non-Javadoc)
	 * @see com.pheiffware.sphereadventure.engine.physics.entity.physicalEntity.PhysicalEntity#resolveCollision(com.pheiffware.sphereadventure.engine.physics.entity.physicalEntity.PhysicalEntity, float)
	 */
	@Override
	public void resolveCollision(PhysicalEntity physicalEntity, float elapsedTime) throws InteractionException
	{
		// physicalEntity.resolveCollision(this, elapsedTime);
		if (physicalEntity instanceof SphereEntity)
		{
			SphereEntity.resolveSphereSphereCollision(this, (SphereEntity) physicalEntity);
		}
		else if (physicalEntity instanceof LineSegmentElevatorEntity)
		{
			LineSegmentEntity.resolveLineSphereCollision((LineSegmentEntity) physicalEntity, this, elapsedTime);
		}
	}
}
