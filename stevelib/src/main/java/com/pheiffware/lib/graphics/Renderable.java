/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.graphics;

/**
 *
 */
public class Renderable
{
	private int normalTechnique;
	private int reflectTechnique;
	private int depthTechnique;
	public int[] techniques;

	public void normalRender(Technique[] techniques)
	{
		// techniques[normalTechnique].putFloats,etc.
	}

	public void reflectRender(Technique[] techniques)
	{
		// techniques[reflectTechnique].putFloats,etc.
	}

	public void depthRender(Technique[] techniques)
	{
		// techniques[depthTechnique].putFloats,etc.
	}
}
