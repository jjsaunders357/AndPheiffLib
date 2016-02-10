/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.examples.physics.testScenarios;


import com.pheiffware.lib.geometry.d3.Vec3D;
import com.pheiffware.lib.physics.PhysicsSystem;
import com.pheiffware.lib.physics.entity.DirectionalGravityEntity;
import com.pheiffware.lib.physics.entity.rigidBody.LineSegmentElevatorEntity;
import com.pheiffware.lib.examples.physics.TestPhysicsScenario;

/**
 *
 */
public class Elevator extends TestPhysicsScenario
{
	public Elevator(float scenarioRuntime)
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
		physicsSystem.addEntity(new LineSegmentElevatorEntity(new Vec3D(0, 500,
				0), new Vec3D(500, 500, 0), -1, 50000.0f, 1.0f, new Vec3D(0,
				-100, 0), 100.0f));
		physicsSystem.addEntity(new DirectionalGravityEntity(new Vec3D(0, 500,
				0)));
	}
}