/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.and.gui.graphics.openGL;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.AndAssetLoader;
import com.pheiffware.lib.and.AndUtils;
import com.pheiffware.lib.and.graphics.AndGraphicsUtils;
import com.pheiffware.lib.and.touch.TouchAnalyzer;
import com.pheiffware.lib.and.touch.TouchTransformListener;
import com.pheiffware.lib.geometry.Transform2D;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Extension of the canned surface view for OpenGL provided by Android to perform some extra setup and will send TouchTransform events to SimpleGLRenderer.
 */
public class SimpleGLView extends GLSurfaceView implements TouchTransformListener, GLSurfaceView.Renderer
{
    private final FilterQuality filterQuality;
    private final AssetManager assetManager;
    private final SimpleGLRenderer renderer;
    private final TouchAnalyzer touchAnalyzer;
    private GLCache GLCache;

    public SimpleGLView(Context context, SimpleGLRenderer renderer, FilterQuality filterQuality)
    {
        super(context);
        this.filterQuality = filterQuality;
        this.assetManager = context.getAssets();
        this.renderer = renderer;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        touchAnalyzer = new TouchAnalyzer(this, metrics.xdpi, metrics.ydpi);

        int requestedGLMajorVersion = Math.min(renderer.maxMajorGLVersion(), AndGraphicsUtils.getDeviceGLMajorVersion(context));
        setEGLContextClientVersion(requestedGLMajorVersion);
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

    @Override
    public void onSurfaceCreated(GL10 useless, EGLConfig config)
    {
        AndUtils.logLC(this, "SurfaceCreated");
        AssetLoader al = new AndAssetLoader(assetManager);
        //All resources held by GLCache will have been thrown away
        GLCache = new GLCache(AndGraphicsUtils.getDeviceGLVersion(getContext()), filterQuality, al);
        try
        {
            renderer.onSurfaceCreated(al, GLCache);
            PheiffGLUtils.assertNoError();
        }
        catch (GraphicsException e)
        {
            Log.e("Failed Surface Creation", "Error during surface creation", e);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        AndUtils.logLC(this, "SurfaceDestroyed");
        super.surfaceDestroyed(holder);
        GLCache.deallocate();
        //Destroy any reference to GL/EGL object (not sure if this matters).
        GLCache = null;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        AndUtils.logLC(this, "SurfaceResized");
        renderer.onSurfaceResize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        try
        {
            renderer.onDrawFrame();
            PheiffGLUtils.assertNoError();
        }
        catch (GraphicsException e)
        {
            Log.e("Fatal", "Error during surface render", e);
        }

    }
}
