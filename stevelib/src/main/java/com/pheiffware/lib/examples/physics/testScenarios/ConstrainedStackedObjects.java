/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.examples.physics.testScenarios;

import com.pheiffware.lib.geometry.Vec3D;
import com.pheiffware.lib.physics.PhysicsSystem;
import com.pheiffware.lib.physics.entity.physicalEntity.physicalEntities.WallEntity;

/**
 *
 */
public class ConstrainedStackedObjects extends StackedObjects
{

	public ConstrainedStackedObjects(double scenarioRuntime, int numSteps, double left, double bottom, double radius, int rows, double gravity,
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
		WallEntity wall2 = new WallEntity(new Vec3D(left, bottom, 0), new Vec3D(left, bottom - 500, 0), 1, new Vec3D(0, 0, 0), 0.6f);
		wall2.setName("Wall2");
		physicsSystem.addEntity(wall2);
		WallEntity wall3 = new WallEntity(new Vec3D(right, bottom, 0), new Vec3D(right, bottom - 500, 0), -1, new Vec3D(0, 0, 0), 0.6f);
		wall3.setName("Wall3");
		physicsSystem.addEntity(wall3);

	}
}
