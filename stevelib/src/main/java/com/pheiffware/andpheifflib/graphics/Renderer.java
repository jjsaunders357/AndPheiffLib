/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.graphics;

import android.graphics.Rect;
import android.opengl.GLES20;

/**
 * Displays the state of the physics system on the main render target (and possibly other render targets).
 */
public class Renderer
{
	int frameBufferHandle;
	int colorRenderTextureHandle;
	Rect viewPort;
	Renderable[] renderables;
	private Technique[] techniques;

	public Renderer()
	{

	}

	public void normalRender()
	{
		bindFrameBuffer();
		setupViewport();
		clear();
		for (Technique technique : techniques)
		{
			technique.clear();
		}
		for (Renderable renderable : renderables)
		{
			renderable.normalRender(techniques);
		}
		for (Technique technique : techniques)
		{
			technique.render();
		}
	}

	/**
	 * @param frameBufferHandle
	 * @param colorRenderTextureHandle
	 * 
	 */
	protected void bindFrameBuffer()
	{
		ImageUtils.bindFrameBuffer(frameBufferHandle, colorRenderTextureHandle, 0);
	}

	protected void setupViewport()
	{
		GLES20.glViewport(viewPort.left, viewPort.top, viewPort.right - viewPort.left, viewPort.bottom - viewPort.top);
	}

	private void clear()
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

	}
}
