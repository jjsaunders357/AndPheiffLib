/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario;

import com.pheiffware.andpheifflib.sphere.engine.physics.PhysicsSystem;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.DirectionalGravityEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody.SphereEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody.WallEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.PhysicsScenario;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

/**
 *
 */
public class SingleBallOnRamp extends PhysicsScenario
{

	public SingleBallOnRamp(float scenarioRuntime)
	{
		super(scenarioRuntime);
	}

	/* (non-Javadoc)
	 * @see physics.scenario.PhysicsScenario#generatePhysicsSystem()
	 */
	@Override
	public void setup(PhysicsSystem physicsSystem)
	{
		SphereEntity circle1 = new SphereEntity(new Vec3F(250, 450, 0), new Vec3F(0, 0, 0), 25, 0.9f, 50);
		physicsSystem.addEntity(circle1);

		physicsSystem.addEntity(new WallEntity(new Vec3F(0, 450, 0), new Vec3F(1000, 550, 0), -1, new Vec3F(0, 0, 0), 1.0f));
		physicsSystem.addEntity(new DirectionalGravityEntity(new Vec3F(0, 500, 0)));
	}
}
