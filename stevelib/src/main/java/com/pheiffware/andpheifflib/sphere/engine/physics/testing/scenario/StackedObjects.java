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
public class StackedObjects extends PhysicsScenario
{

	protected final float left;
	protected final float right;
	protected final float bottom;
	protected final float radius;
	protected final float gravity;
	protected final float coefficientOfRestitution;
	protected final int rows;

	public StackedObjects(float scenarioRuntime, float left, float bottom, float radius, int rows, float gravity, float coefficientOfRestitution)
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

	/* (non-Javadoc)
	 * @see physics.scenario.PhysicsScenario#generatePhysicsSystem()
	 */
	@Override
	public void setup(PhysicsSystem physicsSystem)
	{
		Vec3F startCircleLocation = new Vec3F(left + radius, bottom - radius, 0);
		float radiusSqrt3 = (float) (radius * Math.sqrt(3));
		physicsSystem.addEntity(new WallEntity(new Vec3F(left, bottom, 0), new Vec3F(right, bottom, 0), -1, new Vec3F(0, 0, 0), 0.6f));

		for (int i = rows; i > 0; i--)
		{
			addCircleLine(physicsSystem, startCircleLocation, i);
			startCircleLocation = Vec3F.add(startCircleLocation, new Vec3F(radius, -radiusSqrt3, 0));
		}

		physicsSystem.addEntity(new DirectionalGravityEntity(new Vec3F(0, gravity, 0)));
	}

	private void addCircleLine(PhysicsSystem physicsSystem, Vec3F location, int number)
	{
		for (int i = 0; i < number; i++)
		{
			physicsSystem.addEntity(new SphereEntity(location, new Vec3F(0, 0, 0), 5, coefficientOfRestitution, radius));
			location = new Vec3F(location.x + radius * 2.0f, location.y, location.z);
		}
	}
}
