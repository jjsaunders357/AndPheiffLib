/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario;

import com.pheiffware.andpheifflib.sphere.engine.physics.PhysicsSystem;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody.SphereEntity;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

/**
 *
 */
public class ConstrainedStackedObjectsDrop extends ConstrainedStackedObjects
{
	public ConstrainedStackedObjectsDrop(float scenarioRuntime, float left, float bottom, float radius, int rows, float gravity,
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
		SphereEntity circle = new SphereEntity(new Vec3F(left + 3 * radius, 0, 0), new Vec3F(0, 0, 0), 5 * 2 * 2, coefficientOfRestitution,
				radius * 2);
		circle.setName("Circledrop");
		physicsSystem.addEntity(circle);

	}
}
