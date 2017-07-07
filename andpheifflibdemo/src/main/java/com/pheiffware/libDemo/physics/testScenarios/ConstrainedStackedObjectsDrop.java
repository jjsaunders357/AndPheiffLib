/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.libDemo.physics.testScenarios;

import com.pheiffware.lib.geometry.Vec3D;
import com.pheiffware.lib.physics.PhysicsSystem;
import com.pheiffware.lib.physics.entity.physicalEntity.physicalEntities.SphereEntity;

/**
 *
 */
public class ConstrainedStackedObjectsDrop extends ConstrainedStackedObjects
{
	public ConstrainedStackedObjectsDrop(double scenarioRuntime, int numSteps, double left, double bottom, double radius, int rows, double gravity,
										 double coefficientOfRestitution)
	{
		super(scenarioRuntime, numSteps, left, bottom, radius, rows, gravity, coefficientOfRestitution);
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
		SphereEntity circle = new SphereEntity(new Vec3D(left + 3 * radius, 0, 0), new Vec3D(0, 0, 0), 5 * 2 * 2, coefficientOfRestitution,
				radius * 2);
		circle.setName("Circledrop");
		physicsSystem.addEntity(circle);

	}
}
