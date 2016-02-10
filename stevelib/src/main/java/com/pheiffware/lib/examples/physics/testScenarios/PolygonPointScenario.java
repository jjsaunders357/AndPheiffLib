/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.examples.physics.testScenarios;


import com.pheiffware.lib.geometry.d3.Vec3D;
import com.pheiffware.lib.physics.PhysicsSystem;
import com.pheiffware.lib.physics.entity.DirectionalGravityEntity;
import com.pheiffware.lib.physics.entity.rigidBody.PolygonWallEntity;
import com.pheiffware.lib.physics.entity.rigidBody.SphereEntity;
import com.pheiffware.lib.physics.entity.rigidBody.WallEntity;
import com.pheiffware.lib.examples.physics.TestPhysicsScenario;

public class PolygonPointScenario extends TestPhysicsScenario
{
	public PolygonPointScenario(float scenarioRuntime)
	{
		super(scenarioRuntime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see physics.scenario.PhysicsScenario#generatePhysicsSystem()
	 */
	@Override
	public void setup(PhysicsSystem physicsSystem)
	{
		SphereEntity circle1 = new SphereEntity(new Vec3D(400, 200, 0),
				new Vec3D(0, 0, 0), 25, 1.0f, 50);
		physicsSystem.addEntity(circle1);

		// @formatter:off
		Vec3D[] points = new Vec3D[]
		{ new Vec3D(300, 700, 0), new Vec3D(200, 600, 0),
				new Vec3D(400, 500, 0), new Vec3D(600, 600, 0),
				new Vec3D(500, 700, 0), };
		// @formatter:on
		PolygonWallEntity polygon = new PolygonWallEntity(new Vec3D(0, 0, 0),
				1.0f, points);
		physicsSystem.addEntity(polygon);
		physicsSystem.addEntity(new WallEntity(new Vec3D(0, 700, 0), new Vec3D(
				700, 700, 0), -1, new Vec3D(0, 0, 0), 1.0f));
		physicsSystem.addEntity(new DirectionalGravityEntity(new Vec3D(0, 500,
				0)));
	}
}
