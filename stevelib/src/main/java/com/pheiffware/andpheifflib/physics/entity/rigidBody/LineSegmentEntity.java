/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.andpheifflib.physics.entity.rigidBody;

import com.pheiffware.andpheifflib.geometry.d3.Vec3D;
import com.pheiffware.andpheifflib.geometry.d3.intersect.IntersectCalc;
import com.pheiffware.andpheifflib.geometry.d3.intersect.IntersectionInfo;
import com.pheiffware.andpheifflib.geometry.d3.shapes.OrientedLineSegment;
import com.pheiffware.andpheifflib.geometry.d3.shapes.Sphere;
import com.pheiffware.andpheifflib.physics.InteractionException;
import com.pheiffware.andpheifflib.physics.entity.physicalEntity.PhysicalEntity;
import com.pheiffware.andpheifflib.physics.entity.physicalEntity.PhysicalEntityCollision;

/**
 *  
 */
public abstract class LineSegmentEntity extends PhysicalEntity
{
	public static final void resolveLineSphereCollision(
			final LineSegmentEntity lineSegmentEntity,
			final SphereEntity sphere, final double elapsedTime)
	{
		IntersectionInfo intersectionInfo = IntersectCalc.calcIntersect2D(
				lineSegmentEntity.lineSegment, new Sphere(sphere.getCenter(),
						sphere.getRadius()));
		if (intersectionInfo != null)
		{
			PhysicalEntityCollision collision = new PhysicalEntityCollision(
					lineSegmentEntity, sphere, intersectionInfo);
			collision.resolve();
		}
	}

	private final OrientedLineSegment lineSegment;

	public LineSegmentEntity(Vec3D p1, Vec3D p2, int normalSide,
			Vec3D velocity, double mass, double coefficientOfRestitution)
	{
		super(velocity, mass, coefficientOfRestitution);
		lineSegment = new OrientedLineSegment(p1, p2, normalSide);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see physics.entity.Entity#move(vec3f.Vec3F)
	 */
	@Override
	public void move(final double x, final double y, final double z)
	{
		lineSegment.p1.addTo(x, y, z);
		lineSegment.p2.addTo(x, y, z);
	}

	public final OrientedLineSegment getLineSegment()
	{
		return lineSegment;
	}

	@Override
	public void resolveCollision(PhysicalEntity physicalEntity,
			double elapsedTime) throws InteractionException
	{
		if (physicalEntity instanceof SphereEntity)
		{
			LineSegmentEntity.resolveLineSphereCollision(this,
					(SphereEntity) physicalEntity, elapsedTime);
		}
	}
}
