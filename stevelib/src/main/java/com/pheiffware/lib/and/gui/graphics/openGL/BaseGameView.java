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
import com.pheiffware.lib.and.input.TouchAnalyzer;
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
    private final TouchAnalyzer touchAnalyzer;
    private final boolean forwardTouchEvents;
    private GLCache glCache;

    public BaseGameView(Context context, GameRenderer renderer, FilterQuality filterQuality, boolean forwardTouchEvents)
    {
        super(context);
        this.filterQuality = filterQuality;
        this.assetManager = context.getAssets();
        this.renderer = renderer;
        this.forwardTouchEvents = forwardTouchEvents;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        touchAnalyzer = new TouchAnalyzer(metrics.xdpi, metrics.ydpi);

        int requestedGLMajorVersion = Math.min(renderer.maxMajorGLVersion(), AndGraphicsUtils.getDeviceGLMajorVersion(context));
        setEGLContextClientVersion(requestedGLMajorVersion);
        setRenderer(this);

        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


    public boolean onTouchEvent(MotionEvent event)
    {
        if (forwardTouchEvents)
        {
            final TouchAnalyzer.TouchTransformEvent touchTransformEvent = touchAnalyzer.convertRawTouchEvent(event);
            if (touchTransformEvent != null)
            {
                queueEvent(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        renderer.touchTransformEvent(touchTransformEvent.numPointers, touchTransformEvent.transform);
                    }
                });
            }
            return true;
        }
        return false;
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
            renderer.onSurfaceCreated(al, glCache);
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

        //Deallocate any memory in direct buffers and erase reference to AssetLoader
        glCache.deallocate();
        //Destroy any reference to GL/EGL object (not sure if this matters).
        glCache = null;
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
        catch (Exception e)
        {
            Log.e("Fatal", "Error during surface render", e);
        }

    }
}
