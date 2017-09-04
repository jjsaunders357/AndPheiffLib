package com.pheiffware.lib.and.gui.graphics.openGL;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pheiffware.lib.and.gui.LoggedFragment;

/**
 * A fragment containing a single BaseGameView, which is paused/resumed (to shutdown the rendering thread) based on the fragment life cycle.
 * <p/>
 * Created by Steve on 3/27/2016.
 */
public abstract class BaseGameFragment extends LoggedFragment implements SensorEventListener
{
    private final int[] forwardSensorTypes;
    private final int[] sensorSamplingPeriods;
    private SensorManager sensorManager;

    public BaseGameFragment()
    {
        this(new int[0], new int[0]);
    }

    public BaseGameFragment(int[] forwardSensorTypes, int[] sensorSamplingPeriods)
    {
        this.forwardSensorTypes = forwardSensorTypes;
        this.sensorSamplingPeriods = sensorSamplingPeriods;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    }

    /**
     * Must produce a view of type BaseGameView.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public abstract GameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @NonNull
    @Override
    public GameView getView()
    {
        //noinspection ConstantConditions
        return (GameView) super.getView();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        getView().onResume();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        for (int i = 0; i < forwardSensorTypes.length; i++)
        {
            Sensor rotationSensor = sensorManager.getDefaultSensor(forwardSensorTypes[i]);
            sensorManager.registerListener(this, rotationSensor, sensorSamplingPeriods[i]);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        getView().forwardSensorEvent(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    @Override
    public void onPause()
    {
        //Unregister all listeners
        if (forwardSensorTypes.length > 0)
        {
            sensorManager.unregisterListener(this);
        }
        super.onPause();
    }

    @Override
    public void onStop()
    {
        getView().onPause();
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onDetach()
    {
        sensorManager = null;
        super.onDetach();
    }
}
