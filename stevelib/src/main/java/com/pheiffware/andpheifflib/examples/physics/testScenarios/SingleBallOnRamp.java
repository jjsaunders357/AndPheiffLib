/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.andpheifflib.examples.physics.testScenarios;


import com.pheiffware.andpheifflib.geometry.d3.Vec3D;
import com.pheiffware.andpheifflib.physics.PhysicsSystem;
import com.pheiffware.andpheifflib.physics.entity.DirectionalGravityEntity;
import com.pheiffware.andpheifflib.physics.entity.rigidBody.SphereEntity;
import com.pheiffware.andpheifflib.physics.entity.rigidBody.WallEntity;
import com.pheiffware.andpheifflib.examples.physics.TestPhysicsScenario;

/**
 *
 */
public class SingleBallOnRamp extends TestPhysicsScenario
{

	public SingleBallOnRamp(float scenarioRuntime)
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
		SphereEntity circle1 = new SphereEntity(new Vec3D(250, 450, 0),
				new Vec3D(0, 0, 0), 25, 0.9f, 50);
		physicsSystem.addEntity(circle1);

		physicsSystem.addEntity(new WallEntity(new Vec3D(0, 450, 0), new Vec3D(
				1000, 550, 0), -1, new Vec3D(0, 0, 0), 1.0f));
		physicsSystem.addEntity(new DirectionalGravityEntity(new Vec3D(0, 500,
				0)));
	}
}
