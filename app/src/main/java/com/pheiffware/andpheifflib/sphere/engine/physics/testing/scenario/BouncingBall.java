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
public class BouncingBall extends PhysicsScenario
{
	public BouncingBall(float scenarioRuntime)
	{
		super(scenarioRuntime);
	}

	/* (non-Javadoc)
	 * @see physics.scenario.PhysicsScenario#generatePhysicsSystem()
	 */
	@Override
	public void setup(PhysicsSystem physicsSystem)
	{
		SphereEntity circle1 = new SphereEntity(new Vec3F(250, 300, 0), new Vec3F(0, 0, 0), 25, 0.9f, 50);
		physicsSystem.addEntity(circle1);

		physicsSystem.addEntity(new WallEntity(new Vec3F(0, 500, 0), new Vec3F(500, 500, 0), -1, new Vec3F(0, 0, 0), 0.6f));
		physicsSystem.addEntity(new DirectionalGravityEntity(new Vec3F(0, 500, 0)));
	}
}
