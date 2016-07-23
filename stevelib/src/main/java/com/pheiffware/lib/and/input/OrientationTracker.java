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
    private final SensorManager sensorManager;
    private float[] lastReadAcc = new float[3];
    private float[] lastReadMagnet = new float[3];
    boolean readAcc = false;
    boolean readMag = false;

    //Temp storage location for reading raw orientation matrix and possibly filtering out if it has not changed significantly.
    private Matrix4 newRawOrientationMatrix = Matrix4.newIdentity();

    //Storage location for raw orientation matrix calculated from last hardware readings
    private Matrix4 rawOrientationMatrix = Matrix4.newIdentity();

    //Storage location for the orientation matrix which is appropriately offset by the zero matrix
    private Matrix4 orientationMatrix = Matrix4.newIdentity();

    //Holds inverse of the zero orientation which should be considered identity.
    private Matrix4 invZeroOrientationMatrix = Matrix4.newIdentity();

    //A convenience which causes the orientation to zero out on 1st successful reading
    private boolean zeroOnFirstReading;
    private float sensitivity;

    public OrientationTracker(SensorManager sensorManager, boolean zeroOnFirstReading, float sensitivity)
    {
        this.sensorManager = sensorManager;
        this.zeroOnFirstReading = zeroOnFirstReading;
        this.sensitivity = sensitivity;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                //Log.i("sensor", "Acc: (" + event.values[0] + " , " + event.values[1] + " , " + event.values[2] + ")");
                lastReadAcc[0] = event.values[0];
                lastReadAcc[1] = event.values[1];
                lastReadAcc[2] = event.values[2];
                readAcc = true;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                lastReadMagnet[0] = event.values[0];
                lastReadMagnet[1] = event.values[1];
                lastReadMagnet[2] = event.values[2];
                readMag = true;
                break;
        }
    }


    /**
     * Calculates and returns the latest orientation matrix.  If one is not ready yet (not enough sensor events) this will return null. If an error is encountered, such as the
     * device dropping (making orientation impossible to calculate) the last calculated orientation (possibly null) will be returned.
     *
     * @return
     */
    public Matrix4 calcOrientation()
    {
        if (calcRawOrientation() == null)
        {
            return null;
        }
        else
        {
            if (zeroOnFirstReading)
            {
                zeroOnFirstReading = false;
                setZeroOrientationMatrix(rawOrientationMatrix);
            }
            orientationMatrix.set(rawOrientationMatrix);
            orientationMatrix.multiplyBy(invZeroOrientationMatrix);
            return orientationMatrix;
        }
    }

    /**
     * Returns the raw orientation matrix (not modified by the zero)
     *
     * @return
     */
    public Matrix4 calcRawOrientation()
    {
        if (readAcc && readMag)
        {
            float[] newAngles = new float[3];
            float[] oldAngles = new float[3];
            SensorManager.getRotationMatrix(newRawOrientationMatrix.m, null, lastReadAcc, lastReadMagnet);
            SensorManager.getOrientation(newRawOrientationMatrix.m, newAngles);
            SensorManager.getOrientation(rawOrientationMatrix.m, oldAngles);
            if ((newAngles[0] - oldAngles[0]) * (newAngles[0] - oldAngles[0]) + (newAngles[1] - oldAngles[1]) * (newAngles[1] - oldAngles[1]) + (newAngles[2] - oldAngles[2]) * (newAngles[2] - oldAngles[2]) > sensitivity * sensitivity)
            {
                rawOrientationMatrix.set(newRawOrientationMatrix);
            }
            return rawOrientationMatrix;
        }
        else
        {
            return null;
        }
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
        Matrix4 currentRawOrientation = calcRawOrientation();
        setZeroOrientationMatrix(currentRawOrientation);
    }

    public void register()
    {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor compass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void unregister()
    {
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
