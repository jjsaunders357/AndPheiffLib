/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody;

import com.pheiffware.andpheifflib.sphere.engine.physics.InteractionException;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.physicalEntity.PhysicalEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.physicalEntity.PhysicalEntityCollision;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.physicalEntity.boundingVolume.BoundingSphere;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.LineSegment;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

/**
 * A polygon entity which can interact with circles.
 */
public class PolygonEntity extends PhysicalEntity
{
	public static void resolvePolygonSphereCollision(PolygonEntity polygonEntity, SphereEntity sphereEntity, float elapsedTime)
	{
		for (LineSegment lineSegment : polygonEntity.lineSegments)
		{
			PointOfImpact pointOfImpact = LineSegment.calcCollision(lineSegment, sphereEntity);
			if (pointOfImpact != null)
			{
				PhysicalEntityCollision collision = new PhysicalEntityCollision(polygonEntity, sphereEntity, pointOfImpact);
				collision.resolve();
			}
		}
	}

	private static Vec3F calcCenter(Vec3F[] points)
	{
		Vec3F center = new Vec3F(0, 0, 0);
		for (Vec3F point : points)
		{
			center.addTo(point);
		}
		center.scaleBy(1.0f / points.length);
		return new Vec3F(center.x, center.y, center.z);
	}

	// All the points composing the polygon
	private final Vec3F[] points;

	// A set of lineSegment objects (used for collisions)
	private final LineSegment[] lineSegments;

	private BoundingSphere boundingSphere;

	public PolygonEntity(Vec3F velocity, float mass, float coefficientOfRestitution, Vec3F[] points)
	{
		super(calcCenter(points), velocity, mass, coefficientOfRestitution);
		this.points = copyPoints(points);
		lineSegments = new LineSegment[points.length];
		for (int i = 0; i < points.length - 1; i++)
		{
			lineSegments[i] = new LineSegment(this.points[i], this.points[i + 1], -1);
		}
		lineSegments[lineSegments.length - 1] = new LineSegment(this.points[points.length - 1], this.points[0], 1);
		calcBoundingVolume();
	}

	/**
	 * @param points2
	 * @return
	 */
	private final Vec3F[] copyPoints(Vec3F[] points)
	{
		Vec3F[] pointsCopy = new Vec3F[points.length];
		for (int i = 0; i < points.length; i++)
		{
			pointsCopy[i] = new Vec3F(points[i]);
		}
		return pointsCopy;
	}

	/**
	 * Calculates the optimal spherical bounding volume (slowly)
	 */
	private void calcBoundingVolume()
	{
		Vec3F p1 = null;
		Vec3F p2 = null;
		float longestDistanceSquared = 0.0f;

		for (int i = 0; i < points.length - 1; i++)
		{
			for (int j = i; j < points.length; j++)
			{
				float distanceSquared = Vec3F.distanceSquared(points[i], points[j]);
				if (distanceSquared > longestDistanceSquared)
				{
					longestDistanceSquared = distanceSquared;
					p1 = points[i];
					p2 = points[j];
				}
			}
		}
		boundingSphere = new BoundingSphere(Vec3F.scale(Vec3F.add(p1, p2), 0.5f), (float) Math.sqrt(longestDistanceSquared) / 2);
	}

	/* (non-Javadoc)
	 * @see physics.entity.rigidBody.RigidBodyEntity#calcCollision(physics.entity.rigidBody.RigidBodyEntity)
	 */
	@Override
	public void resolveCollision(PhysicalEntity physicalEntity, float elapsedTime) throws InteractionException
	{
		if (physicalEntity instanceof SphereEntity)
		{
			resolvePolygonSphereCollision(this, (SphereEntity) physicalEntity, elapsedTime);
		}
	}

	/* (non-Javadoc)
	 * @see physics.entity.Entity#move(vec3f.Vec3F)
	 */
	@Override
	public void move(final float tx, final float ty, final float tz)
	{
		for (Vec3F point : points)
		{
			point.addTo(tx, ty, tz);
		}
		boundingSphere.move(tx, ty, tz);
		super.move(tx, ty, tz);
	}

	public final LineSegment[] getLineSegments()
	{
		return lineSegments;
	}

	public BoundingSphere getBoundingSphere()
	{
		return boundingSphere;
	}

}
