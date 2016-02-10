/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.graphics;

import com.pheiffware.andpheifflib.mesh.Mesh;

/**
 *
 */
public abstract class Technique
{
	public abstract void init();

	public abstract void addMesh(Mesh mesh);

	public abstract void clear();

	public abstract void render();
}