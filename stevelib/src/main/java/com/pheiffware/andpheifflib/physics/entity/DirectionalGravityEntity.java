/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.andpheifflib.physics.entity;


import com.pheiffware.andpheifflib.geometry.d3.Vec3D;
import com.pheiffware.andpheifflib.physics.PhysicsSystem;
import com.pheiffware.andpheifflib.physics.entity.physicalEntity.PhysicalEntity;

/**
 * An entity which applies gravity to other physical entities to produce a
 * universal acceleration of the given vector.
 */
public class DirectionalGravityEntity extends Entity
{
	private final Vec3D acceleration;

	public DirectionalGravityEntity(Vec3D acceleration)
	{
		this.acceleration = acceleration;
	}

	@Override
	public void ai(double elapsedTime, PhysicsSystem physicsSystem)
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
