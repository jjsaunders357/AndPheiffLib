package com.pheiffware.lib.and.input;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * A sensor which attempts to track position and orientation of a device.
 * <p/>
 * Created by Steve on 7/5/2016.
 */
public class PositionOrientationSensor implements SensorEventListener
{
    private final SensorManager sensorManager;

    public PositionOrientationSensor(SensorManager sensorManager)
    {
        this.sensorManager = sensorManager;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        System.out.println("Event");
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                Log.i("sensor", "Acc: (" + event.values[0] + " , " + event.values[1] + " , " + event.values[2] + ")");
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                Log.i("sensor", "Mag: (" + event.values[0] + " , " + event.values[1] + " , " + event.values[2] + ")");
                break;
            case Sensor.TYPE_GYROSCOPE:
                Log.i("sensor", "Gyro: (" + event.values[0] + " , " + event.values[1] + " , " + event.values[2] + ")");
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    public void register()
    {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor compass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);

    }

    public void unregister()
    {
        sensorManager.unregisterListener(this);
    }
}
