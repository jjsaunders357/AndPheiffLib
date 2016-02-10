package com.pheiffware.lib.geometry.d3.intersect;


import com.pheiffware.lib.geometry.d3.Vec3D;

public class IntersectionInfo
{
	public final Vec3D intersectionNormal;
	public final double penetration;

	public IntersectionInfo(Vec3D intersectionNormal, double penetration)
	{
		super();

		this.intersectionNormal = intersectionNormal;
		this.penetration = penetration;
	}
}
