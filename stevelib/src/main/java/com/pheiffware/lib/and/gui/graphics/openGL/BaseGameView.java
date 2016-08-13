package com.pheiffware.lib.and.gui.graphics.openGL;

import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.util.Log;
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
 * Extension of the canned surface view for OpenGL provided by Android to perform some extra setup GameRenderer.
 */
public class BaseGameView extends GLSurfaceView implements GLSurfaceView.Renderer, SensorEventListener
{
    private final FilterQuality filterQuality;
    private final AssetManager assetManager;
    private final GameRenderer renderer;
    private final boolean forwardRotationSensorEvents;
    private final SensorManager sensorManager;

    private GLCache glCache;
    //Tracks whether onSurfaceCreated has been called yet (fully initialized surface/size).  If surfaceDestroyed happens, this is reset until onSurfaceCreated is called again.
    //Prevents messages from ever being sent to rendering thread if it has not been initialized yet.
    private boolean surfaceInitialized = false;

    public BaseGameView(Context context, GameRenderer renderer, FilterQuality filterQuality, boolean forwardRotationSensorEvents)
    {
        super(context);
        this.filterQuality = filterQuality;
        this.assetManager = context.getAssets();
        this.renderer = renderer;
        this.forwardRotationSensorEvents = forwardRotationSensorEvents;
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

        int requestedGLMajorVersion = Math.min(renderer.maxMajorGLVersion(), AndGraphicsUtils.getDeviceGLMajorVersion(context));
        setEGLContextClientVersion(requestedGLMajorVersion);
        setRenderer(this);

        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (forwardRotationSensorEvents)
        {
            Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
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

    @Override
    public void onPause()
    {
        if (forwardRotationSensorEvents)
        {
            sensorManager.unregisterListener(this);
        }
        super.onPause();
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


    public void onSensorChanged(SensorEvent event)
    {
        forwardSensorEvent(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    public void forwardSensorEvent(final SensorEvent event)
    {
        //TODO: Is it safe to pass SensorEvent objects into another thread or do they need to be copied?
        if (isSurfaceInitialized())
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

    public boolean isSurfaceInitialized()
    {
        return surfaceInitialized;
    }
}
