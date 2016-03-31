/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.and.fragments.graphics;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.pheiffware.lib.and.touch.TouchAnalyzer;
import com.pheiffware.lib.and.touch.TouchTransformListener;
import com.pheiffware.lib.geometry.Transform2D;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.utils.Utils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Extension of the canned surface view for OpenGL provided by Android to perform basic setup: 1. Automatically handles pause/resume. 2. Sends touch transform events to the
 * SimpleGLRenderer
 */
public class SimpleGLView extends GLSurfaceView implements TouchTransformListener, GLSurfaceView.Renderer
{
    private final FilterQuality filterQuality;
    private final AssetManager assetManager;
    private final SimpleGLRenderer renderer;
    private final TouchAnalyzer touchAnalyzer;
    private ManGL manGL;

    public SimpleGLView(Context context, SimpleGLRenderer renderer, FilterQuality filterQuality)
    {
        super(context);
        this.filterQuality = filterQuality;
        this.assetManager = context.getAssets();
        this.renderer = renderer;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        touchAnalyzer = new TouchAnalyzer(this, metrics.xdpi, metrics.ydpi);

        //TODO: How to configure this?
        setEGLContextClientVersion(2);
        setRenderer(this);

        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
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

    public void onStart()
    {
        //Reallocate native memory buffers, if necessary
        if (manGL != null)
        {
            Utils.logLC(this, "ReallocateManGL");
            manGL.reallocate();
        }
    }

    public void onStop()
    {
        //Destroy native memory buffers.
        Utils.logLC(this, "DeallocateManGL");
        manGL.deallocate();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Utils.logLC(this, "SurfaceCreated");

        //Whenever this is called, either:
        //1. The entire activity was killed and is starting from scratch
        //2. onStop() was called at some point and native memory was deallocated
        //Either way, for simplicity, just create a new instance
        manGL = new ManGL(assetManager, filterQuality, gl, config);
        renderer.onSurfaceCreated(manGL);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        renderer.onSurfaceResize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        renderer.onDrawFrame();
    }
}
