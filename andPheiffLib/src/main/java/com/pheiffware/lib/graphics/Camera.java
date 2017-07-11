package com.pheiffware.lib.graphics;

import com.pheiffware.lib.geometry.Vec3D;
import com.pheiffware.lib.graphics.managed.techniques.ProjectionLinearDepth;

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
public class Camera
{
    //Conversion factor from a distance on the screen in terms of dp to degrees.  degrees = SCREEN_DP_TO_DEGREES * dp
    private static final float SCREEN_DP_TO_DEGREES = 0.1f;

    private float FOV;
    private float aspect;
    private float nearZ;
    private float farZ;
    private boolean flipVertical;

    //The projection matrix representing the lens
    private final Matrix4 projectionMatrix;

    //The linear depth projection representing the lens
    private ProjectionLinearDepth projectionLinearDepth;

    //Represent the composition of invOrientation * inverseTranslation
    private Matrix4 cameraMatrix;

    /**
     * Create camera with given lens properties
     *
     * @param FOV
     * @param aspect
     * @param nearZ
     * @param farZ
     * @param flipVertical
     */
    public Camera(float FOV, float aspect, float nearZ, float farZ, boolean flipVertical)
    {
        //Looking in -z direction, with positive y axis straight up
        cameraMatrix = Matrix4.newIdentity();
        projectionMatrix = Matrix4.newZeroMatrix();
        setLens(FOV, aspect, nearZ, farZ, flipVertical);
    }

    /**
     * Resets position to 0,0,0
     * Reset orientation back to looking down z-axis with positive y axis straight up.
     */
    public void reset()
    {
        cameraMatrix.setIdentity();
    }


    /**
     * Change the lens characteristics of the camera such as FOV
     *
     * @param FOV          field of view
     * @param aspect       width/height ratio
     * @param nearZ        near viewing plane
     * @param farZ         far viewing plane
     * @param flipVertical should the projection be flipped?  Useful for rending to textures.
     */
    public void setLens(float FOV, float aspect, float nearZ, float farZ, boolean flipVertical)
    {
        this.FOV = FOV;
        this.aspect = aspect;
        this.nearZ = nearZ;
        this.farZ = farZ;
        this.flipVertical = flipVertical;
        updateProjection();
    }


    /**
     * Recomputes the projection matrix from lens attributes.
     */
    private void updateProjection()
    {
        projectionMatrix.setProjection(FOV, aspect, nearZ, farZ, flipVertical);
        projectionLinearDepth = new ProjectionLinearDepth(FOV, aspect, farZ);
    }

    /**
     * Translate camera position in absolute space.
     *
     * @param x x
     * @param y y
     * @param z z
     */
    public void translate(float x, float y, float z)
    {
        cameraMatrix.translateBy(-x, -y, -z);
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
        cameraMatrix.rotateBy(-angleDegrees, x, y, z);
    }

    /**
     * Translate in screen coordinate system. -x is left +x is right -y is down +y is up -z is forward +z is back
     *
     * @param x x
     * @param y y
     * @param z z
     */
    public void translateScreen(float x, float y, float z)
    {
        cameraMatrix.translateByLHS(-x, -y, -z);
    }

    /**
     * Rotate the camera relative to "screen" coordinate system. x and y are in the screen and z is perpendicular to the screen.
     *
     * @param angleDegrees
     * @param rotationAxis
     */
    public final void rotateScreen(float angleDegrees, Vec3D rotationAxis)
    {
        rotateScreen(angleDegrees, (float) rotationAxis.x, (float) rotationAxis.y, (float) rotationAxis.z);
    }

    /**
     * Rotate the camera around the given "screen" axis by the specified amount. x and y are in the screen and z is perpendicular to the screen.
     *
     * @param angleDegrees
     * @param x            left/right (+/-)
     * @param y            up/down (+/-)
     * @param z            roll
     */
    public void rotateScreen(float angleDegrees, float x, float y, float z)
    {
        cameraMatrix.multiplyByLHS(Matrix4.newRotate(-angleDegrees, x, y, z));
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
        cameraMatrix.modifyTranslation(-x, -y, -z);
    }

    /**
     * Get the camera's position.
     *
     * @return
     */
    public Vec3D getPosition()
    {
        //Extract the camera's position from the matrix
        Vec3D translation = cameraMatrix.getTranslation();
        translation.scaleBy(-1.0);
        return translation;
    }

    /**
     * Used to turn screen input (such as a mouse or touch/drag) into a camera rotation. Given the direction the camera is looking and an x,y vector, in screen space, rotate in the
     * plane described by the vectors (x,y,0) and (0,0,z). Rotate by an amount proportional to length.
     * <p/>
     * If x,y magnitude is 0, then nothing happens.
     *
     * @param x                       x screen movement
     * @param y                       y screen movement
     * @param cameraRotationPerLength the vector's length is scaled by this factor to convert it to degrees
     */
    public void rotateScreenInputVector(float x, float y, float cameraRotationPerLength)
    {
        float mag = (float) Math.sqrt(x * x + y * y);
        if (mag != 0)
        {
            Vec3D inScreenVec = new Vec3D(x, y, 0);
            Vec3D perpScreenVec = new Vec3D(0, 0, -1);
            Vec3D rotationAxis = Vec3D.cross(perpScreenVec, inScreenVec);
            float angleDegrees = cameraRotationPerLength * mag;
            rotateScreen(angleDegrees, rotationAxis);
        }
    }

    /**
     * Used to turn screen input (such as a mouse or touch/drag) into a camera rotation. Given the direction the camera is looking and an x,y vector, in screen space, rotate om the
     * plane described by the vectors (x,y,0) and (0,0,z). Rotate by an amount proportional to length (using standard constant).
     *
     * @param x x screen movement (assumed to be in units of dp)
     * @param y y screen movement (assumed to be in units of dp)
     */
    public void rotateScreenInputVector(float x, float y)
    {
        rotateScreenInputVector(x, y, SCREEN_DP_TO_DEGREES);
    }


    public void roll(float angleDegrees)
    {
        rotateScreen(angleDegrees, 0, 0, 1);
    }

    public Matrix4 getViewMatrix()
    {
        return cameraMatrix;
    }

    public Matrix4 getProjectionMatrix()
    {
        return projectionMatrix;
    }

    public ProjectionLinearDepth getProjectionLinearDepth()
    {
        return projectionLinearDepth;
    }

    public void setFOV(float FOV)
    {
        this.FOV = FOV;
        updateProjection();
    }

    public void setAspect(float aspect)
    {
        this.aspect = aspect;
        updateProjection();
    }

    public void setNearZ(float nearZ)
    {
        this.nearZ = nearZ;
        updateProjection();
    }

    public void setFarZ(float farZ)
    {
        this.farZ = farZ;
        updateProjection();
    }

    public void setFlipVertical(boolean flipVertical)
    {
        this.flipVertical = flipVertical;
        updateProjection();
    }


    /**
     * Scale FOV by given amount
     *
     * @param scaleFOV
     */
    public void zoom(float scaleFOV)
    {
        setFOV(getFOV() * scaleFOV);
    }

    public float getFOV()
    {
        return FOV;
    }

    public float getAspect()
    {
        return aspect;
    }

    public float getNearZ()
    {
        return nearZ;
    }

    public float getFarZ()
    {
        return farZ;
    }

    public boolean isFlipVertical()
    {
        return flipVertical;
    }
}
