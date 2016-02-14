/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.examples.andGraphics;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.pheiffware.lib.graphics.managed.ManGL;

/**
 * Extension of the canned surface view for OpenGL provided by Android. Key points:
 * 1. When pausing, call onPause().
 * 2. When resuming, call onResume().
 * 
 * When first started OR onResume(), the render's onSurfaceCreated method gets triggered.
 * This should load all textures/programs, etc.
 * 
 * Note:
 * When onSurfaceCreated happens it implies that all existing textures, programs, etc have been automatically deleted.
 * No need to do this work!
 * http://developer.android.com/reference/android/opengl/GLSurfaceView.Renderer.html
 * 
 */
public class TestGraphicsView extends GLSurfaceView
{

	public TestGraphicsView(Context context)
	{
		super(context);
		setEGLContextClientVersion(2);
        setRenderer(new TestRenderer2(new ManGL(context.getAssets())));
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
}
