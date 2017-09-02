package com.pheiffware.lib.and.input;

import android.hardware.SensorManager;

import com.pheiffware.lib.graphics.Matrix4;

/**
 * Tracks the orientation of the device relative to a zero (indentity) orientation.  The zero orientation can be set any time.
 * <p/>
 * Created by Steve on 7/5/2016.
 */
public class OrientationTracker
{
    //A convenience which causes the orientation to start at identity
    private final boolean startAtIdentityOrientation;

    //Storage location for raw orientation matrix calculated from last hardware readings
    private final Matrix4 rawOrientationMatrix = Matrix4.newIdentity();

    //Holds inverse of the "zero" orientation.  In other words invZeroOrientationMatrix * zeroOrientationMatrix = IdentityMatrix
    private final Matrix4 invZeroOrientationMatrix = Matrix4.newIdentity();

    //Storage location for the tracked orientation matrix which is appropriately offset by the zero matrix
    private final Matrix4 orientationMatrix = Matrix4.newIdentity();

    //This tracks whether a sensor event has come in yet.  If not, then calling getCurrentOrientation, will return null.
    private boolean initialSensorStateRead = false;

    public OrientationTracker(boolean startAtIdentityOrientation)
    {
        this.startAtIdentityOrientation = startAtIdentityOrientation;
    }

    public void onSensorChanged(float[] sensorEventValues)
    {
        SensorManager.getRotationMatrixFromVector(rawOrientationMatrix.m, sensorEventValues);
        if (initialSensorStateRead)
        {
            initialSensorStateRead = true;
            if (startAtIdentityOrientation)
            {
                zeroOrientationMatrix();
            }
        }
        orientationMatrix.set(rawOrientationMatrix);
        orientationMatrix.multiplyBy(invZeroOrientationMatrix);
    }


    /**
     * Gets the latest orientation matrix if one is available
     *
     * @return the latest orientation matrix or null, if onSensorChanged() has not been called and startAtIdentityOrientation was set to false
     */
    public Matrix4 getCurrentOrientation()
    {
        if (!initialSensorStateRead && !startAtIdentityOrientation)
        {
            return null;
        }
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
        setZeroOrientationMatrix(rawOrientationMatrix);
    }
}
