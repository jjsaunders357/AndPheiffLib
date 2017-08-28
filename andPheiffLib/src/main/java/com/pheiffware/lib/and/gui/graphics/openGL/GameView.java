package com.pheiffware.lib.and.gui.graphics.openGL;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.pheiffware.lib.R;
import com.pheiffware.lib.and.AndAssetLoader;
import com.pheiffware.lib.and.AndUtils;
import com.pheiffware.lib.and.graphics.AndGraphicsUtils;
import com.pheiffware.lib.and.input.TouchAnalyzer;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Extension of the canned surface view for OpenGL provided by Android to perform some extra setup GameRenderer.
 */
public class GameView extends GLSurfaceView implements GLSurfaceView.Renderer, SensorEventListener
{
    //Maximum time allowed for an action to be considered a tap event
    private static final double maxTouchTapTime = 0.2;

    private final FilterQuality filterQuality;
    private final AndAssetLoader assetLoader;
    private final GameRenderer renderer;
    private final boolean forwardRotationSensorEvents;
    private final boolean forwardTouchTransformEvents;
    private final SensorManager sensorManager;
    private final TouchAnalyzer touchAnalyzer;

    //Tracks whether onSurfaceCreated has been called yet (fully initialized surface/size).  If surfaceDestroyed happens, this is reset until onSurfaceCreated is called again.
    //Prevents sensor and other messages from ever being sent to rendering thread if it has not been initialized yet.
    private boolean surfaceInitialized = false;

    public GameView(Context context, GameRenderer renderer, FilterQuality filterQuality, boolean forwardRotationSensorEvents, boolean forwardTouchTransformEvents)
    {
        super(context);
        this.filterQuality = filterQuality;
        this.assetLoader = new AndAssetLoader(context.getAssets());
        this.renderer = renderer;
        this.forwardRotationSensorEvents = forwardRotationSensorEvents;
        this.forwardTouchTransformEvents = forwardTouchTransformEvents;
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        touchAnalyzer = new TouchAnalyzer(metrics.xdpi, metrics.ydpi, maxTouchTapTime);
        requestOpenGLVersion(context);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private void requestOpenGLVersion(Context context)
    {
        int maxHardwareSupportedGLVersion = AndGraphicsUtils.getDeviceGLVersion(context);
        if (renderer.getMinSupportedGLVersion() > maxHardwareSupportedGLVersion)
        {
            String errorMessage = String.format(getContext().getString(R.string.MinOpenGLVersionError), AndGraphicsUtils.glVersionString(renderer.getMinSupportedGLVersion()));
            Log.e("Fatal", errorMessage);
        }
        int requestedGLVersion = Math.min(renderer.getMaxSupportedGLVersion(), maxHardwareSupportedGLVersion);
        //Can only request major versions
        int requestedGLMajorVersion = requestedGLVersion >> 16;
        setEGLContextClientVersion(requestedGLMajorVersion);
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
        try
        {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            //TODO 0.25 = 1/4: Get this from loaded system state somehow
            Map<String, Object> graphicsSystemConfig = new HashMap<>();
            renderer.onSurfaceCreated(assetLoader, AndGraphicsUtils.getDeviceGLVersion(getContext()), filterQuality, graphicsSystemConfig, new SystemInfo(metrics.xdpi, metrics.ydpi));
            PheiffGLUtils.assertNoError();
            surfaceInitialized = true;
        }
        catch (GraphicsException e)
        {
            //TODO 1.0 = 1/1: How to kill program gracefully on exception here?
            //Log.e("Fatal", "Error during surface creation", e);
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
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
        renderer.onSurfaceDestroyed();
        assetLoader.destroy();
        surfaceInitialized = false;
    }


    @Override
    public void onSensorChanged(SensorEvent event)
    {
        forwardSensorEvent(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return forwardTouchTransformEvent(event);
    }


    /**
     * Forwards a touch event to the renderer as a onTouchTransformEvent.
     *
     * @param event
     * @return
     */
    protected boolean forwardTouchTransformEvent(final MotionEvent event)
    {

        if (isSurfaceInitialized() && forwardTouchTransformEvents)
        {
            //Must process event in gui thread as the event object itself is modified (its not safe to pass to another thread).
            final TouchAnalyzer.TouchEvent touchEvent = touchAnalyzer.convertRawTouchEvent(event);

            if (touchEvent != null)
            {
                queueEvent(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (touchEvent.touchTransformEvent != null)
                        {
                            renderer.onTouchTransformEvent(touchEvent.touchTransformEvent);
                        }
                        else
                        {
                            renderer.onTouchTapEvent(touchEvent.touchTapEvent);
                        }
                    }
                });
            }
            return true;
        }
        return false;
    }

    public void forwardSensorEvent(final SensorEvent event)
    {
        if (isSurfaceInitialized())
        {
            queueEvent(new Runnable()
            {
                @Override
                public void run()
                {
                    long timestamp = event.timestamp;
                    float[] values = Arrays.copyOf(event.values, event.values.length);
                    int type = event.sensor.getType();
                    renderer.onSensorChanged(type, values, timestamp);
                }
            });
        }
    }

    public boolean isSurfaceInitialized()
    {
        return surfaceInitialized;
    }
}
