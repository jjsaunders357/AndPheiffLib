/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.andpheifflib.testing.physics.testScenarios;


import com.pheiffware.andpheifflib.geometry.Vec3D;
import com.pheiffware.andpheifflib.physics.PhysicsSystem;
import com.pheiffware.andpheifflib.physics.entity.DirectionalGravityEntity;
import com.pheiffware.andpheifflib.physics.entity.rigidBody.SphereEntity;

/**
 *
 */
public class ElevatorWithLoad extends Elevator
{
	public ElevatorWithLoad(float scenarioRuntime)
	{
		super(scenarioRuntime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see physics.scenario.PhysicsScenario#generatePhysicsSystem()
	 */
	@Override
	public void setup(PhysicsSystem physicsSystem)
	{
		super.setup(physicsSystem);
		SphereEntity circle1 = new SphereEntity(new Vec3D(250, 450, 0),
				new Vec3D(0, 0, 0), 25, 0.98f, 50);
		physicsSystem.addEntity(circle1);
		physicsSystem.addEntity(new DirectionalGravityEntity(new Vec3D(0, 500,
				0)));
	}
}