package com.pheiffware.lib.graphics;

/**
 * Used for tracking display and orientation.
 * Created by Steve on 3/7/2016.
 */
public class Camera
{

    public void translate(float x, float y, float z)
    {

    }

    public void rotateAxis(float angle, float x, float y, float z)
    {

    }

    public void moveTo(float x, float y, float z)
    {

    }

    public void lookAt(float x, float y, float z, float upX, float upY, float upZ)
    {
        //Inverse of a rotation matrix is its transpose

    }

    public void roll(float angle)
    {

    }

    public void pitch(float angle)
    {

    }

    public void yaw(float angle)
    {

    }

    /**
     * Given the direction the camera is looking and an x,y unit vector on the screen, rotate in that direction.
     * In other words rotate in the plane described by the vectors (x,y,0) and (0,0,z) where x,y and z are the camera's current basis vectors.
     *
     * @param unitX x component of screen unit vector describing how the camera should rotate
     * @param unitY y component of screen unit vector describing how the camera should rotate
     * @param angle how much to rotate in direction of vector
     */
    public void pitchYawVector(float unitX, float unitY, float angle)
    {

    }

    /**
     * Generally used to turn input (such as a mouse or touch/drag) into a camera rotation.
     * Given the direction the camera is looking and an x,y vector on the screen, rotate in that direction, by an amount proportional to the length of the vector.
     * In other words rotate in the plane described by the vectors (x,y,0) and (0,0,z) where x,y and z are the camera's current basis vectors.
     *
     * @param x                amount to rotate in the x direction
     * @param y                amount to rotate in the x direction
     * @param degreesPerLength the vector's length is scaled by this much to convert it to degrees
     */
    public void pitchYawScaleVector(float x, float y, float degreesPerLength)
    {
        float mag = (float) Math.sqrt(x * x + y * y);
        pitchYawVector(x / mag, y / mag, mag / degreesPerLength);
    }
}
