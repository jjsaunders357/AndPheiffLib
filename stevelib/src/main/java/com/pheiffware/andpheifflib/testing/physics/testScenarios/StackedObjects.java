/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.andpheifflib.testing.physics.testScenarios;


import com.pheiffware.andpheifflib.geometry.Vec3D;
import com.pheiffware.andpheifflib.physics.PhysicsSystem;
import com.pheiffware.andpheifflib.physics.entity.DirectionalGravityEntity;
import com.pheiffware.andpheifflib.physics.entity.rigidBody.SphereEntity;
import com.pheiffware.andpheifflib.physics.entity.rigidBody.WallEntity;
import com.pheiffware.andpheifflib.testing.physics.TestPhysicsScenario;

/**
 *
 */
public class StackedObjects extends TestPhysicsScenario
{

	protected final float left;
	protected final float right;
	protected final float bottom;
	protected final float radius;
	protected final float gravity;
	protected final float coefficientOfRestitution;
	protected final int rows;

	public StackedObjects(float scenarioRuntime, float left, float bottom,
			float radius, int rows, float gravity,
			float coefficientOfRestitution)
	{
		super(scenarioRuntime);
		this.left = left;
		this.rows = rows;
		this.bottom = bottom;
		this.radius = radius;
		this.gravity = gravity;
		this.coefficientOfRestitution = coefficientOfRestitution;
		right = left + rows * radius * 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see physics.scenario.PhysicsScenario#generatePhysicsSystem()
	 */
	@Override
	public void setup(PhysicsSystem physicsSystem)
	{
		Vec3D startCircleLocation = new Vec3D(left + radius, bottom - radius, 0);
		float radiusSqrt3 = (float) (radius * Math.sqrt(3));
		physicsSystem.addEntity(new WallEntity(new Vec3D(left, bottom, 0),
				new Vec3D(right, bottom, 0), -1, new Vec3D(0, 0, 0), 0.6f));

		for (int i = rows; i > 0; i--)
		{
			addCircleLine(physicsSystem, startCircleLocation, i);
			startCircleLocation = Vec3D.add(startCircleLocation, new Vec3D(
					radius, -radiusSqrt3, 0));
		}

		physicsSystem.addEntity(new DirectionalGravityEntity(new Vec3D(0,
				gravity, 0)));
	}

	private void addCircleLine(PhysicsSystem physicsSystem, Vec3D location,
			int number)
	{
		for (int i = 0; i < number; i++)
		{
			physicsSystem.addEntity(new SphereEntity(location, new Vec3D(0, 0,
					0), 5, coefficientOfRestitution, radius));
			location = new Vec3D(location.x + radius * 2.0f, location.y,
					location.z);
		}
	}
}
