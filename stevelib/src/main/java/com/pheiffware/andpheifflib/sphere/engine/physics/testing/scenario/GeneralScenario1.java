/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario;

import com.pheiffware.andpheifflib.sphere.engine.physics.PhysicsSystem;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody.SphereEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody.WallEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.PhysicsScenario;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

/**
 *
 */
public class GeneralScenario1 extends PhysicsScenario
{
	public GeneralScenario1(float scenarioRuntime)
	{
		super(scenarioRuntime);
	}

	/* (non-Javadoc)
	 * @see physics.scenario.PhysicsScenario#generatePhysicsSystem()
	 */
	@Override
	public void setup(PhysicsSystem physicsSystem)
	{
		SphereEntity circle1 = new SphereEntity(new Vec3F(0, 120, 0), new Vec3F(350, 80, 0), 25, 0.9f, 40);
		physicsSystem.addEntity(circle1);
		physicsSystem.addEntity(new SphereEntity(new Vec3F(200, 200, 0), new Vec3F(0, 0, 0), 5, 0.9f, 20));
		physicsSystem.addEntity(new SphereEntity(new Vec3F(250, 200, 0), new Vec3F(0, 0, 0), 5, 0.9f, 20));
		physicsSystem.addEntity(new SphereEntity(new Vec3F(300, 200, 0), new Vec3F(0, 0, 0), 5, 0.9f, 20));
		physicsSystem.addEntity(new SphereEntity(new Vec3F(350, 200, 0), new Vec3F(0, 0, 0), 5, 0.9f, 20));

		SphereEntity circle2 = new SphereEntity(new Vec3F(0, 500, 0), new Vec3F(250, -80, 0), 25, 0.5f, 40);
		physicsSystem.addEntity(circle2);
		physicsSystem.addEntity(new SphereEntity(new Vec3F(200, 400, 0), new Vec3F(0, 0, 0), 5, 0.5f, 20));
		physicsSystem.addEntity(new SphereEntity(new Vec3F(250, 400, 0), new Vec3F(0, 0, 0), 5, 0.5f, 20));
		physicsSystem.addEntity(new SphereEntity(new Vec3F(300, 400, 0), new Vec3F(0, 0, 0), 5, 0.5f, 20));
		physicsSystem.addEntity(new SphereEntity(new Vec3F(350, 400, 0), new Vec3F(0, 0, 0), 5, 0.5f, 20));

		physicsSystem.addEntity(new WallEntity(new Vec3F(20, 20, 0), new Vec3F(20, 700, 0), -1, new Vec3F(0, 0, 0), 1.0f));
		physicsSystem.addEntity(new WallEntity(new Vec3F(20, 700, 0), new Vec3F(700, 700, 0), -1, new Vec3F(0, 0, 0), 1.0f));
		physicsSystem.addEntity(new WallEntity(new Vec3F(700, 20, 0), new Vec3F(700, 700, 0), 1, new Vec3F(0, 0, 0), 1.0f));
		physicsSystem.addEntity(new WallEntity(new Vec3F(20, 20, 0), new Vec3F(700, 20, 0), 1, new Vec3F(0, 0, 0), 1.0f));
	}
}
