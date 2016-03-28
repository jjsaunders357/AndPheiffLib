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

/**
 * Extension of the canned surface view for OpenGL provided by Android to perform basic setup:
 * 1. Automatically handles pause/resume.
 * 2. Sends touch transform events to the SimpleGLRenderer
 */
//TODO: Handle cleanup? When onSurfaceCreated happens it implies that all existing textures, programs, etc have been automatically deleted. No need to do this work! http://developer.android.com/reference/android/opengl/GLSurfaceView.Renderer.html
public class SimpleGLView extends GLSurfaceView implements TouchTransformListener
{
    private final SimpleGLRenderer renderer;
    private final TouchAnalyzer touchAnalyzer;

    public SimpleGLView(Context context, SimpleGLRenderer renderer)
    {
        super(context);
        this.renderer = renderer;
        setEGLContextClientVersion(2);
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
