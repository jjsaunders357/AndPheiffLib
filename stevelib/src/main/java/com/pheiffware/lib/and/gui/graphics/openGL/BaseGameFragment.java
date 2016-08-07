package com.pheiffware.lib.and.gui.graphics.openGL;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pheiffware.lib.and.gui.LoggedFragment;
import com.pheiffware.lib.graphics.FilterQuality;

/**
 * A fragment containing a single BaseGameView initialized with the given renderer.  This can be configured to register to listen to sensor events and forward them to the
 * renderer.
 * <p/>
 * <p/>
 * Created by Steve on 3/27/2016.
 */
public abstract class BaseGameFragment extends LoggedFragment implements SensorEventListener
{
    private final GameRenderer renderer;
    private final FilterQuality defaultFilterQuality;
    private final boolean forwardTouchEvents;
    private final boolean forwardRotationSensorEvents;
    //Should rotation sensor events be forwarded to the renderer
    private BaseGameView baseGameView = null;
    private SensorManager sensorManager;

    /**
     * @param renderer                    The renderer which will handle display
     * @param defaultFilterQuality        This is defined for all created textures by default
     * @param forwardTouchEvents          touchEvents will be forwarded to the renderer
     * @param forwardRotationSensorEvents rotationSensorEvents will be forwarded to the renderer
     */

    public BaseGameFragment(GameRenderer renderer, FilterQuality defaultFilterQuality, boolean forwardTouchEvents, boolean forwardRotationSensorEvents)
    {
        this.renderer = renderer;
        this.defaultFilterQuality = defaultFilterQuality;
        this.forwardTouchEvents = forwardTouchEvents;
        this.forwardRotationSensorEvents = forwardRotationSensorEvents;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        baseGameView = new BaseGameView(getContext(), renderer, defaultFilterQuality, forwardTouchEvents);
        return baseGameView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        baseGameView.onResume();

        if (forwardRotationSensorEvents)
        {
            Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onPause()
    {
        if (forwardRotationSensorEvents)
        {
            sensorManager.unregisterListener(this);
        }
        baseGameView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        baseGameView = null;
    }

    @Override
    public void onDetach()
    {
        sensorManager = null;
        super.onDetach();
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        baseGameView.forwardSensorEvent(event);
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy)
    {
    }

    public GameRenderer getRenderer()
    {
        return renderer;
    }
}
