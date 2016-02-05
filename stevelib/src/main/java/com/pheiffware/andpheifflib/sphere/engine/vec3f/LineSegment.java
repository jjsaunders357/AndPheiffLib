/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.vec3f;

import com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody.PointOfImpact;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody.SphereEntity;

/**
 * Represents a line segment between p1 and p2.
 */
public class LineSegment
{
	/**
	 * Calculates normal collisions between a circle and the line-segment.
	 * 
	 * @param lineSegment
	 * @param circle
	 * @return
	 */
	public static PointOfImpact calcCollision(LineSegment lineSegment, SphereEntity circle)
	{
		// All references to line refer to the infinite line as opposed to the segment.
		float centerToLineDistance = Vec3F.subDot(circle.center, lineSegment.p1, lineSegment.unitNormal);

		// The center of the circle is actually past the line.
		if (centerToLineDistance < 0)
		{
			return null;
		}

		float linePenetration = circle.getRadius() - centerToLineDistance;

		// The circle is penetrating the line
		if (linePenetration > 0)
		{
			Vec3F circleCenter = circle.center;
			// Distance, projected along line, from p1 (can be negative)
			float positionOnLine = lineSegment.getProjectedPositionOnLine(circleCenter);

			// Quick check for no collision
			if (positionOnLine <= -circle.getRadius() || positionOnLine >= lineSegment.length + circle.getRadius())
			{
				return null;
			}
			else
			{

				Vec3F collisionNormal;
				float penetration;
				if (positionOnLine < 0)
				{
					collisionNormal = new Vec3F(circleCenter.x - lineSegment.p1.x, circleCenter.y - lineSegment.p1.y, circleCenter.z
							- lineSegment.p1.z);
					float collisionNormalLength = collisionNormal.magnitude();
					penetration = circle.getRadius() - collisionNormalLength;
					if (penetration < 0)
					{
						return null;
					}
					collisionNormal.scaleBy(1.0f / collisionNormalLength);
				}
				else if (positionOnLine > lineSegment.length)
				{
					collisionNormal = new Vec3F(circleCenter.x - lineSegment.p2.x, circleCenter.y - lineSegment.p2.y, circleCenter.z
							- lineSegment.p2.z);
					float collisionNormalLength = collisionNormal.magnitude();
					penetration = circle.getRadius() - collisionNormalLength;
					if (penetration < 0)
					{
						return null;
					}
					collisionNormal.scaleBy(1.0f / collisionNormalLength);
				}
				else
				{
					collisionNormal = lineSegment.unitNormal;
					penetration = linePenetration;
					// Move along inverse collision normal from circle's center to radius and then back by 1/2 penetration.
				}
				return new PointOfImpact(collisionNormal, penetration);
			}
		}
		else
		{
			return null;
		}
	}

	protected static Vec3F calcCenter(Vec3F p1, Vec3F p2)
	{
		return Vec3F.scale(Vec3F.add(p1, p2), 0.5f);
	}

	// End point 1
	public final Vec3F p1;

	// End point 2
	public final Vec3F p2;

	// Which side the line is "facing". Rotates normal this angle from the tangent
	private final float normalTangentCosAngle;
	private final float normalTangentSinAngle;

	// Unit vector in direction of the line
	private final Vec3F unitTangent;

	// Unit vector perpendicular to line (and z-axis)
	private final Vec3F unitNormal;

	// Length
	private float length;

	public LineSegment(Vec3F p1, Vec3F p2, int normalSide)
	{
		this.p1 = p1;
		this.p2 = p2;
		this.normalTangentCosAngle = (float) Math.cos(normalSide * (Math.PI / 2.0));
		this.normalTangentSinAngle = (float) Math.sin(normalSide * (Math.PI / 2.0));
		unitTangent = new Vec3F(0, 0, 0);
		unitNormal = new Vec3F(0, 0, 0);
		reshape();
	}

	/**
	 * Call if the underlying vertices move relative to each other to redefine the line segment. If they both simply translate together, this is
	 * unnecessary.
	 */
	public final void reshape()
	{
		unitTangent.x = p2.x - p1.x;
		unitTangent.y = p2.y - p1.y;
		unitTangent.z = p2.z - p1.z;
		length = unitTangent.magnitude();
		unitTangent.scaleBy(1.0f / length);
		unitNormal.set(unitTangent);
		unitNormal.rotate2D(normalTangentCosAngle, normalTangentSinAngle);
	}

	/**
	 * Gets the position of point projected onto the line. Will return 0 at p1 and |p2| at p2
	 * 
	 * @param point
	 * @return
	 */
	private float getProjectedPositionOnLine(Vec3F point)
	{
		return Vec3F.subDot(point, p1, unitTangent);
	}

	public final float getLength()
	{
		return length;
	}

	public final Vec3F getUnitTangent()
	{
		return unitTangent;
	}

	public final Vec3F getUnitNormal()
	{
		return unitNormal;
	}

}
