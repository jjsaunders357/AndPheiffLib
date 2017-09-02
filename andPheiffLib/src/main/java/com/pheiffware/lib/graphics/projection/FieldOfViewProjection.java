package com.pheiffware.lib.graphics.projection;

/**
 * Manages a projection matrix, using field of view concepts.
 * Created by Steve on 9/2/2017.
 */

public class FieldOfViewProjection extends Projection
{
    private float verticalFOV;
    private float aspect;
    private float near;
    private float far;

    /**
     * Converts the given field of view into a scale factor, to multiply a projected coordinate by.
     *
     * @param FOV
     * @return
     */
    public static float scaleFromFOV(float FOV)
    {
        return (float) (1.0 / Math.tan(Math.toRadians(FOV / 2.0)));
    }

    /**
     * Creates a new projection with given properties.
     * Aspect (width/height ratio) assumed to be 1.
     *
     * @param verticalFOV
     * @param nearZ
     * @param farZ
     */
    public FieldOfViewProjection(float verticalFOV, float nearZ, float farZ)
    {
        this(verticalFOV, 1, nearZ, farZ);
    }


    /**
     * Creates a new projection with given properties.
     *
     * @param verticalFOV
     * @param aspect      width/height ratio
     * @param near
     * @param far
     */
    public FieldOfViewProjection(float verticalFOV, float aspect, float near, float far)
    {
        this.verticalFOV = verticalFOV;
        this.aspect = aspect;
        this.near = near;
        this.far = far;
        updateProjection();
    }

    /**
     * Change all aspects of the projection at once.
     *
     * @param verticalFOV
     * @param aspect      width/height ratio
     * @param near
     * @param far
     */
    public void setLens(float verticalFOV, float aspect, float near, float far)
    {
        this.verticalFOV = verticalFOV;
        this.aspect = aspect;
        this.near = near;
        this.far = far;
        updateProjection();
    }

    /**
     * Scale FOV by given amount
     *
     * @param scaleFOV
     */
    public void zoom(float scaleFOV)
    {
        verticalFOV = verticalFOV * scaleFOV;
        updateProjection();
    }

    /**
     * Set the aspect ratio
     *
     * @param aspect width/height ratio
     */
    public void setAspect(float aspect)
    {
        this.aspect = aspect;
        updateProjection();
    }

    private void updateProjection()
    {
        float yScale = scaleFromFOV(verticalFOV);
        float xScale = yScale / aspect;
        setProjection(xScale, yScale, 0, 0, near, far);
    }
}
