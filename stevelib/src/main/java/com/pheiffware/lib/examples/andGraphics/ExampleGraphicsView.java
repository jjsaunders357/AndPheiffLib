/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.examples.andGraphics;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.pheiffware.lib.and.touch.TouchAnalyzer;
import com.pheiffware.lib.and.touch.TouchTransformListener;
import com.pheiffware.lib.geometry.Transform2D;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.managed.ManGL;

/**
 * Extension of the canned surface view for OpenGL provided by Android. Key points: 1. When pausing, call onPause(). 2. When resuming, call onResume().
 * <p/>
 * When first started OR onResume(), the render's onSurfaceCreated method gets triggered. This should load all textures/programs, etc.
 * <p/>
 * Note: When onSurfaceCreated happens it implies that all existing textures, programs, etc have been automatically deleted. No need to do this work!
 * http://developer.android.com/reference/android/opengl/GLSurfaceView.Renderer.html
 */
public class ExampleGraphicsView extends GLSurfaceView implements TouchTransformListener
{
    private final ExampleMeshRenderer renderer;
    private final TouchAnalyzer touchAnalyzer;

    public ExampleGraphicsView(Context context)
    {
        super(context);
        setEGLContextClientVersion(2);
        renderer = new ExampleMeshRenderer(new ManGL(context.getAssets(), FilterQuality.MEDIUM));
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        touchAnalyzer = new TouchAnalyzer(this, metrics.xdpi, metrics.ydpi);
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    public boolean onTouchEvent(MotionEvent event)
    {
        touchAnalyzer.interpretRawEvent(event);
        return true;
    }

    @Override
    public void touchTransformEvent(final int numPointers, final Transform2D transform)
    {
        queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                renderer.touchTransformEvent(numPointers, transform);
            }
        });
    }
}
