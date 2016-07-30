package com.pheiffware.lib.and.input;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.pheiffware.lib.graphics.Matrix4;

/**
 * Tracks the orientation of the device relative to a zero orientation.  The zero orientation can be set any time.
 * <p/>
 * Created by Steve on 7/5/2016.
 */
public class OrientationTracker implements SensorEventListener
{
    //This tracks whether a sensor event has come in yet.  If not, then calling getCurrentOrientation, will return null.
    private boolean initialSensorStateRead = false;

    //The last raw values read from the sensor.  Nothing is done with these until getCurrentOrientation is called.
    private float[] lastReadSensorValues = new float[4];

    //Storage location for raw orientation matrix calculated from last hardware readings
    private final Matrix4 rawOrientationMatrix = Matrix4.newIdentity();

    //Storage location for the orientation matrix which is appropriately offset by the zero matrix
    private final Matrix4 orientationMatrix = Matrix4.newIdentity();

    //Holds inverse of the zero orientation which should be considered identity.
    private final Matrix4 invZeroOrientationMatrix = Matrix4.newIdentity();

    //A convenience which causes the orientation to zero out on 1st successful (non-null) reading.  All future readings are relative to the initial state.
    private boolean zeroOnFirstReading;


    public OrientationTracker(boolean zeroOnFirstReading)
    {
        this.zeroOnFirstReading = zeroOnFirstReading;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        lastReadSensorValues[0] = event.values[0];
        lastReadSensorValues[1] = event.values[1];
        lastReadSensorValues[2] = event.values[2];
        lastReadSensorValues[3] = event.values[3];
        initialSensorStateRead = true;
    }


    /**
     * Calculates and returns the latest orientation matrix.  If there is no orientation matrix available then null is returned.
     *
     * @return
     */
    public Matrix4 getCurrentOrientation()
    {
        if (!initialSensorStateRead)
        {
            return null;
        }
        SensorManager.getRotationMatrixFromVector(rawOrientationMatrix.m, lastReadSensorValues);
        if (zeroOnFirstReading)
        {
            zeroOnFirstReading = false;
            setZeroOrientationMatrix(rawOrientationMatrix);
        }
        orientationMatrix.set(rawOrientationMatrix);
        orientationMatrix.multiplyBy(invZeroOrientationMatrix);
        return orientationMatrix;
    }


    /**
     * Sets the orientation which should be regarded as identity.  This is typically used to set the current orientation as identity so that all further orientation changes will be
     * relative to the current orientation.
     *
     * @param matrix
     */
    public void setZeroOrientationMatrix(Matrix4 matrix)
    {
        invZeroOrientationMatrix.set(matrix);
        invZeroOrientationMatrix.transpose();
    }

    /**
     * Sets the identity orientation to the current orientation.
     */
    public void zeroOrientationMatrix()
    {
        SensorManager.getRotationMatrixFromVector(rawOrientationMatrix.m, lastReadSensorValues);
        setZeroOrientationMatrix(rawOrientationMatrix);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
