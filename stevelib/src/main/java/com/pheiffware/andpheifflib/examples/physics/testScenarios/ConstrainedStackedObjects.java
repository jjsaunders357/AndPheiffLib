/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.andpheifflib.examples.physics.testScenarios;


import com.pheiffware.andpheifflib.geometry.d3.Vec3D;
import com.pheiffware.andpheifflib.physics.PhysicsSystem;
import com.pheiffware.andpheifflib.physics.entity.rigidBody.WallEntity;

/**
 *
 */
public class ConstrainedStackedObjects extends StackedObjects
{

	public ConstrainedStackedObjects(float scenarioRuntime, float left,
			float bottom, float radius, int rows, float gravity,
			float coefficientOfRestitution)
	{
		super(scenarioRuntime, left, bottom, radius, rows, gravity,
				coefficientOfRestitution);
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
		WallEntity wall2 = new WallEntity(new Vec3D(left, bottom, 0),
				new Vec3D(left, bottom - 500, 0), 1, new Vec3D(0, 0, 0), 0.6f);
		wall2.setName("Wall2");
		physicsSystem.addEntity(wall2);
		WallEntity wall3 = new WallEntity(new Vec3D(right, bottom, 0),
				new Vec3D(right, bottom - 500, 0), -1, new Vec3D(0, 0, 0), 0.6f);
		wall3.setName("Wall3");
		physicsSystem.addEntity(wall3);

	}
}
