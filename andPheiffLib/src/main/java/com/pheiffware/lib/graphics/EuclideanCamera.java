package com.pheiffware.lib.graphics;

import com.pheiffware.lib.geometry.Vec3D;

/**
 * Used for tracking display and orientation.
 * <p/>
 * Note:
 * <p/>
 * In normalized device coordinates, the nearest, visible, vertices are at z=-1 and the furthest are at z=1.
 * <p/>
 * Standard projection matrix operates with the idea that the standard view is sitting at origin and looking in negative z direction.
 * <p/>
 * Created by Steve on 3/7/2016.
 */
public class EuclideanCamera extends Camera
{
    public EuclideanCamera()
    {
        super();
    }

    /**
     * Turns an x,y input vector into forward/backward/left/right motion of camera relative to its current orientation.
     * <p>
     *
     * @param xInput                    sideways movement
     * @param yInput                    forward movement
     * @param distancePerInputMagnitude conversion factor between xInput,yInput vector length and distance to move
     */
    public void forwardStrafeInput(float xInput, float yInput, float distancePerInputMagnitude)
    {
        viewMatrix.translateByLHS(-xInput * distancePerInputMagnitude, 0, -yInput * distancePerInputMagnitude);
    }

    /**
     * Turns an x,y input vector into forward/backward/left/right motion of camera relative to its current orientation.
     * <p>
     *
     * @param xInput                    sideways movement
     * @param yInput                    up/down movement
     * @param distancePerInputMagnitude conversion factor between xInput,yInput vector length and distance to move
     */
    public void upStrafeInput(float xInput, float yInput, float distancePerInputMagnitude)
    {
        viewMatrix.translateByLHS(-xInput * distancePerInputMagnitude, -yInput * distancePerInputMagnitude, 0);
    }

    /**
     * Translate camera position in absolute space.
     *
     * @param x x
     * @param y y
     * @param z z
     */
    public void translateAbsolute(float x, float y, float z)
    {
        viewMatrix.translateBy(-x, -y, -z);
    }

    public void lookAt(float eyeX, float eyeY, float eyeZ, float targetX, float targetY, float targetZ, float upX, float upY, float upZ)
    {
        viewMatrix.setLookAt(eyeX, eyeY, eyeZ, targetX, targetY, targetZ, upX, upY, upZ);
    }

    /**
     * Translate in screen coordinate system. -x is left +x is right -y is down +y is up -z is forward +z is back
     *
     * @param x x
     * @param y y
     * @param z z
     */
    public void translateRelative(float x, float y, float z)
    {
        viewMatrix.translateByLHS(-x, -y, -z);
    }

    /**
     * Rotate camera in absolute space.
     *
     * @param angleDegrees
     * @param x
     * @param y
     * @param z
     */
    public void rotate(float angleDegrees, float x, float y, float z)
    {
        viewMatrix.rotateBy(-angleDegrees, x, y, z);
    }


    /**
     * Move the camera to the given position.
     *
     * @param x x
     * @param y y
     * @param z z
     */
    public void setPosition(float x, float y, float z)
    {
        //Directly modify the translation terms in the matrix
        viewMatrix.modifyTranslation(-x, -y, -z);
    }

    /**
     * Get the camera's position.
     *
     * @return
     */
    public Vec3D getPosition()
    {
        //Extract the camera's position from the matrix
        Vec3D translation = viewMatrix.getTranslation();
        translation.scaleBy(-1.0);
        return translation;
    }


}
