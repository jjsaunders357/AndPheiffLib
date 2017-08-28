package com.pheiffware.lib.graphics;

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
     * @param posX camera position
     * @param posY camera position
     * @param posZ camera position
     * @param dirX look direction
     * @param dirY look direction
     * @param dirZ look direction
     * @param upX  camera up direction
     * @param upY  camera up direction
     * @param upZ  camera up direction
     */
    public final void reset(float posX, float posY, float posZ, float dirX, float dirY, float dirZ, float upX, float upY, float upZ)
    {
        viewMatrix.setLookAt(posX, posY, posZ, posX + dirX, posY + dirY, posZ + dirZ, upX, upY, upZ);
    }

    /**
     * @param posX    camera position
     * @param posY    camera position
     * @param posZ    camera position
     * @param targetX point to look at
     * @param targetY point to look at
     * @param targetZ point to look at
     * @param upX     camera up direction
     * @param upY     camera up direction
     * @param upZ     camera up direction
     */
    public final void resetLookAt(float posX, float posY, float posZ, float targetX, float targetY, float targetZ, float upX, float upY, float upZ)
    {
        viewMatrix.setLookAt(posX, posY, posZ, targetX, targetY, targetZ, upX, upY, upZ);
    }

    /**
     * Causes the camera to look in the given direction (without moving).
     *
     * @param dirX look direction
     * @param dirY look direction
     * @param dirZ look direction
     * @param upX  camera up direction
     * @param upY  camera up direction
     * @param upZ  camera up direction
     */
    public void setOrientation(float dirX, float dirY, float dirZ, float upX, float upY, float upZ)
    {
        float x = -viewMatrix.m[12];
        float y = -viewMatrix.m[13];
        float z = -viewMatrix.m[14];
        resetLookAt(x, y, z, dirX, dirY, dirZ, upX, upY, upZ);
    }

    /**
     * Causes the camera to look at the given point (without moving).
     *
     * @param targetX point to look at
     * @param targetY point to look at
     * @param targetZ point to look at
     * @param upX     camera up direction
     * @param upY     camera up direction
     * @param upZ     camera up direction
     */
    public void lookAt(float targetX, float targetY, float targetZ, float upX, float upY, float upZ)
    {
        float x = -viewMatrix.m[12];
        float y = -viewMatrix.m[13];
        float z = -viewMatrix.m[14];
        reset(x, y, z, targetX, targetY, targetZ, upX, upY, upZ);
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
     * Move the camera to the given position.
     *
     * @param x x
     * @param y y
     * @param z z
     */
    public void setPosition(float x, float y, float z)
    {
        viewMatrix.modifyTranslation(0, 0, 0);
        translateAbsolute(x, y, z);
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


}
