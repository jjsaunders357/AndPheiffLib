package com.pheiffware.lib.graphics;

import com.pheiffware.lib.geometry.Angle;
import com.pheiffware.lib.geometry.Axis;

/**
 * Created by Steve on 8/2/2017.
 */

public abstract class Camera
{
    //Represent the composition of invOrientation * inverseTranslation
    protected Matrix4 viewMatrix;

    public Camera()
    {
        viewMatrix = Matrix4.newIdentity();
    }

    /**
     * Get the matrix, to apply to geometry, to simulate the camera's position and orientation.
     *
     * @return
     */
    public Matrix4 getViewMatrix()
    {
        return viewMatrix;
    }

    /**
     * Resets position to 0,0,0
     * Reset orientation back to looking down z-axis with positive y axis straight up.
     */
    public void reset()
    {
        viewMatrix.setIdentity();
    }

    public abstract void forwardStrafeInput(float xInput, float yInput, float distancePerInputMagnitude);

    public abstract void upStrafeInput(float xInput, float yInput, float degreesPerLength);


    /**
     * Turns an x,y input vector into a rotation of the camera, relative to its current orientation.
     * <p>
     *
     * @param xInput           rotation in the xInputz plane (more or less)
     * @param yInput           rotation in the yz plane (more or less)
     * @param degreesPerLength conversion factor between xInput,yInput length and angle to rotate
     */
    public void rotateInput(float xInput, float yInput, float degreesPerLength)
    {
        rotateInput(xInput, yInput, Axis.X, Axis.Y, Axis.Z, degreesPerLength);
    }

    /**
     * Rolls the camera by the given angle (rotation in the xy plane).
     *
     * @param angleDegrees
     */
    public void roll(float angleDegrees)
    {
        Angle xyAngle = Angle.newDegrees(-angleDegrees);
        viewMatrix.rotatePlaneLHS(Axis.X, Axis.Y, xyAngle);
    }

    /**
     * Given an x,y input vector, interpret it as a rotation of the camera.  The direction of the input vector will determine a vector in space to rotate from.
     * The camera is rotated from this vector to the rotateToAxis.
     * All vectors are in camera space (x-axis is left, y-axis is up, z-axis is in/out of the screen).
     *
     * @param xInput           the x-input received
     * @param yInput           the y-input received
     * @param inputXAxis       the axis to which x-input corresponds
     * @param inputYAxis       the axis to which y-input corresponds
     * @param rotateToAxis     the axis towards which to rotate
     * @param degreesPerLength how many degrees to rotate, per length of the input vector
     */
    protected void rotateInput(float xInput, float yInput, Axis inputXAxis, Axis inputYAxis, Axis rotateToAxis, float degreesPerLength)
    {
        float mag = (float) Math.sqrt(xInput * xInput + yInput * yInput);
        if (mag != 0)
        {
            Angle xzAngle = Angle.newAtan(yInput, xInput);
            xzAngle.negate();
            Angle moveAngle = Angle.newDegrees(-degreesPerLength * mag);

            viewMatrix.rotatePlaneLHS(inputXAxis, inputYAxis, xzAngle);
            viewMatrix.rotatePlaneLHS(inputXAxis, rotateToAxis, moveAngle);
            xzAngle.negate();
            viewMatrix.rotatePlaneLHS(inputXAxis, inputYAxis, xzAngle);
        }
    }
}
