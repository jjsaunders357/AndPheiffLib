/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario;

import com.pheiffware.andpheifflib.sphere.engine.physics.PhysicsSystem;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.DirectionalGravityEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody.SphereEntity;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

/**
 *
 */
public class ElevatorWithLoad extends Elevator
{
	public ElevatorWithLoad(float scenarioRuntime)
	{
		super(scenarioRuntime);
	}

	/* (non-Javadoc)
	 * @see physics.scenario.PhysicsScenario#generatePhysicsSystem()
	 */
	@Override
	public void setup(PhysicsSystem physicsSystem)
	{
		super.setup(physicsSystem);
		SphereEntity circle1 = new SphereEntity(new Vec3F(250, 450, 0), new Vec3F(0, 0, 0), 25, 0.98f, 50);
		physicsSystem.addEntity(circle1);
		physicsSystem.addEntity(new DirectionalGravityEntity(new Vec3F(0, 500, 0)));
	}
}