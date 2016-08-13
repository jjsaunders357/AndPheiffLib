/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.and.gui.graphics.openGL;

import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.SensorEvent;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.AndAssetLoader;
import com.pheiffware.lib.and.AndUtils;
import com.pheiffware.lib.and.graphics.AndGraphicsUtils;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Extension of the canned surface view for OpenGL provided by Android to perform some extra setup and will send TouchTransform events to GameRenderer.
 */
public class BaseGameView extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private final FilterQuality filterQuality;
    private final AssetManager assetManager;
    private final GameRenderer renderer;
    private final boolean forwardTouchEvents;
    private GLCache glCache;
    //Tracks whether onSurfaceCreated has been called yet (fully initialized surface/size).  If surfaceDestroyed happens, this is reset until onSurfaceCreated is called again.
    //Prevents messages from ever being sent to rendering thread if it has not been initialized yet.
    private boolean surfaceInitialized = false;

    public BaseGameView(Context context, GameRenderer renderer, FilterQuality filterQuality, boolean forwardTouchEvents)
    {
        super(context);
        this.filterQuality = filterQuality;
        this.assetManager = context.getAssets();
        this.renderer = renderer;
        this.forwardTouchEvents = forwardTouchEvents;


        int requestedGLMajorVersion = Math.min(renderer.maxMajorGLVersion(), AndGraphicsUtils.getDeviceGLMajorVersion(context));
        setEGLContextClientVersion(requestedGLMajorVersion);
        setRenderer(this);

        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 useless, EGLConfig config)
    {
        AndUtils.logLC(this, "SurfaceCreated");
        AssetLoader al = new AndAssetLoader(assetManager);
        //All resources held by glCache will have been thrown away
        glCache = new GLCache(AndGraphicsUtils.getDeviceGLVersion(getContext()), filterQuality, al);
        try
        {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            renderer.onSurfaceCreated(al, glCache, new SurfaceMetrics(metrics.xdpi, metrics.ydpi));
            PheiffGLUtils.assertNoError();
            surfaceInitialized = true;
        }
        catch (GraphicsException e)
        {
            Log.e("Failed Surface Creation", "Error during surface creation", e);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        AndUtils.logLC(this, "SurfaceResized");
        renderer.onSurfaceResize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        AndUtils.logLC(this, "SurfaceDestroyed");
        super.surfaceDestroyed(holder);
        surfaceInitialized = false;

        //Deallocate any memory in direct buffers and erase reference to AssetLoader
        glCache.deallocate();
        //Destroy any reference to GL/EGL object (not sure if this matters).
        glCache = null;
    }


    @Override
    public void onDrawFrame(GL10 gl)
    {
        try
        {
            renderer.onDrawFrame();
            PheiffGLUtils.assertNoError();
        }
        catch (Exception e)
        {
            Log.e("Fatal", "Error during surface render", e);
        }

    }

    public void forwardSensorEvent(final SensorEvent event)
    {
        if (surfaceInitialized)
        {
            queueEvent(new Runnable()
            {
                @Override
                public void run()
                {
                    renderer.onSensorChanged(event);
                }
            });
        }
    }

    public boolean onTouchEvent(final MotionEvent event)
    {
        if (surfaceInitialized && forwardTouchEvents)
        {
            queueEvent(new Runnable()
            {
                @Override
                public void run()
                {
                    renderer.onTouchEvent(event);
                }
            });
            return true;
        }
        return false;
    }
}
