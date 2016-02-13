/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.examples.physics.testScenarios;

import com.pheiffware.lib.geometry.Vec3D;
import com.pheiffware.lib.physics.PhysicsSystem;
import com.pheiffware.lib.physics.entity.physicalEntity.physicalEntities.LineSegmentElevatorEntity;
import com.pheiffware.lib.physics.entity.physicalEntity.physicalEntities.LineSegmentEntity;

/**
 *
 */
public class CompressedStackedObjects extends ConstrainedStackedObjects
{
	private final double crusherMass;
	private final double maxSpeed;
	private final double acceleration;

	public CompressedStackedObjects(double scenarioRuntime, int numSteps, double left, double bottom, double radius, int rows, double gravity,
									double coefficientOfRestitution, double crusherMass, double maxSpeed, double acceleration)
	{
		super(scenarioRuntime, numSteps, left, bottom, radius, rows, gravity, coefficientOfRestitution);
		this.crusherMass = crusherMass;
		this.maxSpeed = maxSpeed;
		this.acceleration = acceleration;
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
		LineSegmentEntity crushWall = new LineSegmentElevatorEntity(new Vec3D(0, 75, 0), new Vec3D(500, 75, 0), 1, crusherMass, 0.6f, new Vec3D(0,
				maxSpeed, 0), acceleration);
		crushWall.setName("crushWall");
		physicsSystem.addEntity(crushWall);
	}
}