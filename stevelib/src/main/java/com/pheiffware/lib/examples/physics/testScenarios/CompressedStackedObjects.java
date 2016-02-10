/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.examples.physics.testScenarios;


import com.pheiffware.lib.geometry.d3.Vec3D;
import com.pheiffware.lib.physics.PhysicsSystem;
import com.pheiffware.lib.physics.entity.rigidBody.LineSegmentElevatorEntity;
import com.pheiffware.lib.physics.entity.rigidBody.LineSegmentEntity;

/**
 *
 */
public class CompressedStackedObjects extends ConstrainedStackedObjects
{
	private final float crusherMass;
	private final float maxSpeed;
	private final float acceleration;

	public CompressedStackedObjects(float scenarioRuntime, float left,
			float bottom, float radius, int rows, float gravity,
			float coefficientOfRestitution, float crusherMass, float maxSpeed,
			float acceleration)
	{
		super(scenarioRuntime, left, bottom, radius, rows, gravity,
				coefficientOfRestitution);
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
		LineSegmentEntity crushWall = new LineSegmentElevatorEntity(new Vec3D(
				0, 75, 0), new Vec3D(500, 75, 0), 1, crusherMass, 0.6f,
				new Vec3D(0, maxSpeed, 0), acceleration);
		crushWall.setName("crushWall");
		physicsSystem.addEntity(crushWall);
	}
}