/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario;

import com.pheiffware.andpheifflib.sphere.engine.physics.PhysicsSystem;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.DirectionalGravityEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody.LineSegmentElevatorEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.PhysicsScenario;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

/**
 *
 */
public class Elevator extends PhysicsScenario
{
	public Elevator(float scenarioRuntime)
	{
		super(scenarioRuntime);
	}

	/* (non-Javadoc)
	 * @see physics.scenario.PhysicsScenario#generatePhysicsSystem()
	 */
	@Override
	public void setup(PhysicsSystem physicsSystem)
	{
		physicsSystem.addEntity(new LineSegmentElevatorEntity(new Vec3F(0, 500, 0), new Vec3F(500, 500, 0), -1, 50000.0f, 1.0f,
				new Vec3F(0, -100, 0), 100.0f));
		physicsSystem.addEntity(new DirectionalGravityEntity(new Vec3F(0, 500, 0)));
	}
}