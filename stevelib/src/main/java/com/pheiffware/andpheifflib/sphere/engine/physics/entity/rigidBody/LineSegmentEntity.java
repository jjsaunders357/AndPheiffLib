/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody;

import com.pheiffware.andpheifflib.sphere.engine.physics.InteractionException;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.physicalEntity.PhysicalEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.physicalEntity.PhysicalEntityCollision;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.LineSegment;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

/** 
 *  
 */
public abstract class LineSegmentEntity extends PhysicalEntity
{
	public static final void resolveLineSphereCollision(final LineSegmentEntity lineSegmentEntity, final SphereEntity sphere, final float elapsedTime)
	{
		PointOfImpact pointOfImpact = LineSegment.calcCollision(lineSegmentEntity.lineSegment, sphere);
		if (pointOfImpact != null)
		{
			PhysicalEntityCollision collision = new PhysicalEntityCollision(lineSegmentEntity, sphere, pointOfImpact);
			collision.resolve();
		}
	}

	private static Vec3F calcCenter(Vec3F p1, Vec3F p2)
	{
		return Vec3F.scale(Vec3F.add(p1, p2), 0.5f);
	}

	private final LineSegment lineSegment;

	public LineSegmentEntity(Vec3F p1, Vec3F p2, int normalSide, Vec3F velocity, float mass, float coefficientOfRestitution)
	{
		super(calcCenter(p1, p2), velocity, mass, coefficientOfRestitution);
		lineSegment = new LineSegment(p1, p2, normalSide);
	}

	/* (non-Javadoc)
	 * @see physics.entity.Entity#move(vec3f.Vec3F)
	 */
	@Override
	public void move(final float x, final float y, final float z)
	{
		lineSegment.p1.addTo(x, y, z);
		lineSegment.p2.addTo(x, y, z);
		super.move(x, y, z);
	}

	public final LineSegment getLineSegment()
	{
		return lineSegment;
	}

	@Override
	public void resolveCollision(PhysicalEntity physicalEntity, float elapsedTime) throws InteractionException
	{
		if (physicalEntity instanceof SphereEntity)
		{
			LineSegmentEntity.resolveLineSphereCollision(this, (SphereEntity) physicalEntity, elapsedTime);
		}
	}
}
