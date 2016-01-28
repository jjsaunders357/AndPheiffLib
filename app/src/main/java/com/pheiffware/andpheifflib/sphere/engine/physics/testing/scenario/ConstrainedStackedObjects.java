/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario;

import com.pheiffware.andpheifflib.sphere.engine.physics.PhysicsSystem;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody.WallEntity;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

/**
 *
 */
public class ConstrainedStackedObjects extends StackedObjects
{

	public ConstrainedStackedObjects(float scenarioRuntime, float left, float bottom, float radius, int rows, float gravity,
			float coefficientOfRestitution)
	{
		super(scenarioRuntime, left, bottom, radius, rows, gravity, coefficientOfRestitution);
	}

	/* (non-Javadoc)
	 * @see physics.scenario.PhysicsScenario#generatePhysicsSystem()
	 */
	@Override
	public void setup(PhysicsSystem physicsSystem)
	{
		super.setup(physicsSystem);
		WallEntity wall2 = new WallEntity(new Vec3F(left, bottom, 0), new Vec3F(left, bottom - 500, 0), 1, new Vec3F(0, 0, 0), 0.6f);
		wall2.setName("Wall2");
		physicsSystem.addEntity(wall2);
		WallEntity wall3 = new WallEntity(new Vec3F(right, bottom, 0), new Vec3F(right, bottom - 500, 0), -1, new Vec3F(0, 0, 0), 0.6f);
		wall3.setName("Wall3");
		physicsSystem.addEntity(wall3);

	}
}
