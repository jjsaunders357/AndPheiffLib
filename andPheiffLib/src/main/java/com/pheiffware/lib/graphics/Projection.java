package com.pheiffware.lib.graphics;

import com.pheiffware.lib.graphics.managed.techniques.ProjectionLinearDepth;

/**
 * Created by Steve on 8/23/2017.
 */

public class Projection
{
    private float FOV, aspect;
    private float nearZ, farZ;
    private boolean flipVertical;

    //Used to extract depth values from non-linear depth buffer
    private float depthZConst;
    //Used to extract depth values from non-linear depth buffer
    private float depthZFactor;

    private final Matrix4 projectionMatrix = Matrix4.newIdentity();
    private ProjectionLinearDepth linearDepth;

    public Projection()
    {

    }

    public Projection(float FOV, float aspect, float nearZ, float farZ, boolean flipVertical)
    {
        this.FOV = FOV;
        this.aspect = aspect;
        this.nearZ = nearZ;
        this.farZ = farZ;
        this.flipVertical = flipVertical;
        updateProjection();
    }


    public Matrix4 getProjectionMatrix()
    {
        return projectionMatrix;
    }

    private void updateProjection()
    {
        projectionMatrix.setProjection(FOV, aspect, nearZ, farZ, flipVertical);
        depthZConst = 0.5f - 0.5f * projectionMatrix.m[10];
        depthZFactor = 0.5f * projectionMatrix.m[14];
        linearDepth = new ProjectionLinearDepth(FOV, aspect, farZ, flipVertical);
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
     * Scale FOV by given amount
     *
     * @param scaleFOV
     */
    public void zoom(float scaleFOV)
    {
        setFOV(getFOV() * scaleFOV);
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

    public float getDepthZConst()
    {
        return depthZConst;
    }

    public float getDepthZFactor()
    {
        return depthZFactor;
    }

    public ProjectionLinearDepth getLinearDepth()
    {
        return linearDepth;
    }
}
