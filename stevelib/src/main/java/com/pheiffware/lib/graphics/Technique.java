/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.graphics;

import com.pheiffware.lib.mesh.Mesh;

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