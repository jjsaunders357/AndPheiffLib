/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.graphics;

import com.pheiffware.lib.graphics.buffer.CombinedVertexBuffer;
import com.pheiffware.lib.graphics.buffer.PrimitiveIndexBuffer;
import com.pheiffware.lib.mesh.Mesh;

/**
 *
 */
public class ColorTextureTechnique extends Technique
{
	private final int programHandle;
	public final PrimitiveIndexBuffer primitiveBuffer;
	public final CombinedVertexBuffer vertexBuffer;
	private int GLPrimitiveType;

	public ColorTextureTechnique(int programHandle, PrimitiveIndexBuffer primitiveBuffer, CombinedVertexBuffer vertexBuffer)
	{
		this.programHandle = programHandle;
		this.primitiveBuffer = primitiveBuffer;
		this.vertexBuffer = vertexBuffer;
	}

	@Override
	public void clear()
	{

	}

	@Override
	public void render()
	{
		vertexBuffer.transferDynamic();
		vertexBuffer.bind();
		primitiveBuffer.drawAll(GLPrimitiveType);
	}

	/* (non-Javadoc)
	 * @see com.pheiffware.sphereadventure.engine.graphics.Technique#init()
	 */
	@Override
	public void init()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.pheiffware.sphereadventure.engine.graphics.Technique#addMesh(com.pheiffware.sphereadventure.engine.mesh.Mesh)
	 */
	@Override
	public void addMesh(Mesh mesh)
	{
		// Put things into dynamic vertex buffers as appropriate

	}
}
