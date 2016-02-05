/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.entity;

import com.pheiffware.andpheifflib.sphere.engine.physics.PhysicsSystem;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.physicalEntity.PhysicalEntity;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

/**
 * An entity which applies gravity to other physical entities to produce a universal acceleration of the given vector.
 */
public class DirectionalGravityEntity extends Entity
{
	private final Vec3F acceleration;

	public DirectionalGravityEntity(Vec3F acceleration)
	{
		this.acceleration = acceleration;
	}

	@Override
	public void ai(float elapsedTime, PhysicsSystem physicsSystem)
	{
		PhysicalEntity[] physicalEntities = physicsSystem.getPhysicalEntities();
		int numPhysicalEntities = physicsSystem.getNumPhysicalEntities();
		for (int i = 0; i < numPhysicalEntities; i++)
		{
			PhysicalEntity physicalEntity = physicalEntities[i];
			if (!physicalEntity.ignoresGravity())
			{
				physicalEntity.addForce(acceleration, physicalEntity.mass);
			}
		}
	}
}
