package com.pheiffware.lib.graphics.projection;

import com.pheiffware.lib.graphics.Matrix4;

/**
 * Created by Steve on 8/23/2017.
 */

public abstract class Projection
{
    //Project as if negative z is in front of the camera and positive behind
    private final float zSign;

    //Used to extract depth values from non-linear depth buffer
    private float depthZConst;
    //Used to extract depth values from non-linear depth buffer
    private float depthZFactor;

    private final Matrix4 projectionMatrix = Matrix4.newZeroMatrix();

    protected Projection()
    {
        this(false);
    }

    private Projection(boolean zPositive)
    {
        if (zPositive)
        {
            this.zSign = 1;
        }
        else
        {
            this.zSign = -1;
        }
        //Identity matrix
        setProjection(1, 1, 0, 0, 0, 1);
    }

    public void setProjection(float xScale, float yScale, float xOffset, float yOffset, float near, float far)
    {
        float[] m = new float[16];
        //@formatter:off
                m[0] = xScale;      m[4] = 0;       m[8] =  zSign * xOffset;                        m[12] = 0;
                m[1] = 0;           m[5] = yScale;  m[9] =  zSign * yOffset;                        m[13] = 0;
                m[2] = 0;           m[6] = 0;       m[10] = zSign * (far + near) / (far - near);    m[14] = -2 * far * near / (far - near);
                m[3] = 0;           m[7] = 0;       m[11] = zSign;                                  m[15] = 0;
        //@formatter:on
        projectionMatrix.set(m);
        depthZConst = 0.5f - 0.5f * projectionMatrix.m[10];
        depthZFactor = 0.5f * projectionMatrix.m[14];
    }

    public float getDepthZConst()
    {
        return depthZConst;
    }

    public float getDepthZFactor()
    {
        return depthZFactor;
    }

    public Matrix4 getProjectionMatrix()
    {
        return projectionMatrix;
    }

}
