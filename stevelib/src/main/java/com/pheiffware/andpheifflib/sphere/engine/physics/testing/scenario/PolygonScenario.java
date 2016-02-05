/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario;

import com.pheiffware.andpheifflib.sphere.engine.physics.PhysicsSystem;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.DirectionalGravityEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody.PolygonWallEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody.SphereEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody.WallEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.PhysicsScenario;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

public class PolygonScenario extends PhysicsScenario
{
	public PolygonScenario(float scenarioRuntime)
	{
		super(scenarioRuntime);
	}

	/* (non-Javadoc)
	 * @see physics.scenario.PhysicsScenario#generatePhysicsSystem()
	 */
	@Override
	public void setup(PhysicsSystem physicsSystem)
	{
		SphereEntity circle1 = new SphereEntity(new Vec3F(400, 450, 0), new Vec3F(0, 0, 0), 25, 1.0f, 50);
		physicsSystem.addEntity(circle1);

		//@formatter:off
		Vec3F[] points = new Vec3F[] 
		{
				new Vec3F(100.5f,700,0),
				new Vec3F(0.5f,600,0),
				new Vec3F(200.5f,500,0),
				new Vec3F(400.5f,600,0),
				new Vec3F(300.5f,700,0),
		};
		//@formatter:on
		PolygonWallEntity polygon = new PolygonWallEntity(new Vec3F(0, 0, 0), 0.7f, points);
		polygon.name = "Polygon1";
		physicsSystem.addEntity(polygon);
		PolygonWallEntity polygon2 = new PolygonWallEntity(new Vec3F(0, 0, 0), 0.7f, points);
		polygon2.name = "Polygon2";
		polygon2.move(new Vec3F(450, 0, 0));
		physicsSystem.addEntity(polygon2);

		physicsSystem.addEntity(new WallEntity(new Vec3F(0, 700, 0), new Vec3F(700, 700, 0), -1, new Vec3F(0, 0, 0), 0.9f));
		physicsSystem.addEntity(new DirectionalGravityEntity(new Vec3F(0, 500, 0)));
	}
}
