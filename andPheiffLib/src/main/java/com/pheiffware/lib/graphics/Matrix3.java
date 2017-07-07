package com.pheiffware.lib.graphics;

import android.opengl.Matrix;
import android.renderscript.Matrix3f;

import java.util.Arrays;

/**
 * Stores and manipulates a 3x3 matrix.  Stored as a 9 element float array in column major order.
 * Note: The built in Matrix class for Android might be a better choice, but this integrates better with my existing Matrix4 class.
 * Created by Steve on 3/9/2016.
 */
public class Matrix3
{
    /**
     * Creates an empty matrix
     *
     * @return new zero matrix
     */
    public static Matrix3 newZeroMatrix()
    {
        return new Matrix3();
    }

    /**
     * Creates new identity matrix
     *
     * @return new identity matrix
     */
    public static Matrix3 newIdentity()
    {
        Matrix3 matrix = new Matrix3();
        matrix.setIdentity();
        return matrix;
    }

    /**
     * Creates new 3x3 matrix from given floats.
     *
     * @param floats 9 floats in column major order
     * @return new 3x3 matrix from given floats
     */
    public static Matrix3 newFromFloats(float[] floats)
    {
        return new Matrix3(Arrays.copyOf(floats, 9));
    }

    public static Matrix3 newFromUpperLeft(Matrix4 matrix4)
    {
        Matrix3 matrix3 = new Matrix3();
        matrix3.setMatrix4UpperLeft(matrix4);
        return matrix3;
    }

    //Stored matrix data in column major order
    public final float[] m;

    /**
     * Constructs a new blank matrix
     */
    private Matrix3()
    {
        m = new float[9];
    }

    /**
     * Internal constructor which does NOT copy data
     *
     * @param m
     */
    private Matrix3(float[] m)
    {
        this.m = m;
    }

    /**
     * Creates a new copy of the given matrix.
     *
     * @param matrix matrix to copy
     */
    public Matrix3(Matrix3 matrix)
    {
        m = Arrays.copyOf(matrix.m, 9);
    }

    /**
     * Resets matrix data to identity matrix
     */
    public void setIdentity()
    {
        //@formatter:off
        m[0] = 1;m[3] = 0;m[6] = 0;
        m[1] = 0;m[4] = 1;m[7] = 0;
        m[2] = 0;m[5] = 0;m[8] = 1;
        //@formatter:on
    }

    /**
     * Create a 3x3 matrix from the upper left corner of a 4x4 matrix.
     *
     * @param m 4x4 matrix data in column major order
     * @return upper left 3x3 matrix
     */
    public final void setFloatMatrix4UpperLeft(float[] m)
    {
        //@formatter:off
        this.m[0] = m[0];this.m[3] = m[4];this.m[6] = m[8];
        this.m[1] = m[1];this.m[4] = m[5];this.m[7] = m[9];
        this.m[2] = m[2];this.m[5] = m[6];this.m[8] = m[10];
        //@formatter:on
    }

    /**
     * Create a 3x3 matrix from the upper left corner of a 4x4 matrix.
     *
     * @param matrix4 matrix to extract from
     */
    public final void setMatrix4UpperLeft(Matrix4 matrix4)
    {
        setFloatMatrix4UpperLeft(matrix4.m);
    }

    /**
     * Given a Matrix4, compute the inverse/transpose of the upper left 3x3 matrix.
     *
     * @param transform 4x4 transform to compute normal transform from
     */
    public void setNormalTransformFromMatrix4Standard(Matrix4 transform)
    {
        setMatrix4UpperLeft(transform);
        invert();
        transpose();
    }

    /**
     * Given a Matrix4, compute the inverse/transpose of the upper left 3x3 matrix.
     * This is over 10 times as fast as the standard inverse/transpose calc!
     * Probably less numerically stable (not sure if it will handle skew).
     * Totally gratuitous, but fun!
     *
     * @param transform 4x4 transform to compute normal transform from
     */
    public void setNormalTransformFromMatrix4Fast(Matrix4 transform)
    {
        //Extract scale^2 factors from upper left 3x3
        float[] t4m = transform.m;
        float xScaleSquared = t4m[0] * t4m[0] + t4m[1] * t4m[1] + t4m[2] * t4m[2];
        float yScaleSquared = t4m[4] * t4m[4] + t4m[5] * t4m[5] + t4m[6] * t4m[6];
        float zScaleSquared = t4m[8] * t4m[8] + t4m[9] * t4m[9] + t4m[10] * t4m[10];

        //Find inverse^2 scale factors
        float invXScaleSq = 1 / xScaleSquared;
        float invYScaleSq = 1 / yScaleSquared;
        float invZScaleSq = 1 / zScaleSquared;

        //Take upper left 3x3 matrix and inverse scale it once to get rotation matrix
        //and inverse scale again to apply inverse of scale operation to the rotation matrix.
        m[0] = t4m[0] * invXScaleSq;
        m[1] = t4m[1] * invXScaleSq;
        m[2] = t4m[2] * invXScaleSq;
        m[3] = t4m[4] * invYScaleSq;
        m[4] = t4m[5] * invYScaleSq;
        m[5] = t4m[6] * invYScaleSq;
        m[6] = t4m[8] * invZScaleSq;
        m[7] = t4m[9] * invZScaleSq;
        m[8] = t4m[10] * invZScaleSq;
    }

    /**
     * Transposes the matrix in place.
     */
    public void transpose()
    {
        float temp;
        temp = m[3];
        m[3] = m[1];
        m[1] = temp;
        temp = m[2];
        m[2] = m[6];
        m[6] = temp;
        temp = m[7];
        m[7] = m[5];
        m[5] = temp;
    }

    /**
     * Inverts the matrix in place, returning true if there is an inverse.
     *
     * @return Is this matrix invertible?
     */
    public boolean invert()
    {
        float[] orig = Arrays.copyOf(m, 9);
        m[0] = get(1, 1, orig) * get(2, 2, orig) - get(1, 2, orig) * get(2, 1, orig);
        m[1] = get(1, 2, orig) * get(2, 0, orig) - get(1, 0, orig) * get(2, 2, orig);
        m[2] = get(1, 0, orig) * get(2, 1, orig) - get(2, 0, orig) * get(1, 1, orig);

        m[3] = get(0, 2, orig) * get(2, 1, orig) - get(2, 2, orig) * get(0, 1, orig);
        m[4] = get(0, 0, orig) * get(2, 2, orig) - get(0, 2, orig) * get(2, 0, orig);
        m[5] = get(0, 1, orig) * get(2, 0, orig) - get(2, 1, orig) * get(0, 0, orig);

        m[6] = get(0, 1, orig) * get(1, 2, orig) - get(1, 1, orig) * get(0, 2, orig);
        m[7] = get(0, 2, orig) * get(1, 0, orig) - get(1, 2, orig) * get(0, 0, orig);
        m[8] = get(0, 0, orig) * get(1, 1, orig) - get(1, 0, orig) * get(0, 1, orig);
        float det = orig[0] * m[0] + orig[3] * m[1] + orig[6] * m[2];
        if (det != 0)
        {
            float invDet = 1 / det;
            for (int i = 0; i < 9; i++)
            {
                m[i] *= invDet;
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Used during determinant calculation to lookup values
     *
     * @param row    row
     * @param column column
     * @param floats array to lookup in
     * @return value in array at row/column
     */
    private static float get(int row, int column, float[] floats)
    {
        return floats[row + column * 3];
    }


    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j += 3)
            {
                builder.append(m[i + j]);
                builder.append(",");
            }
            builder.append("\n");
        }
        return builder.toString();

    }

    public void set(float[] floats)
    {
        System.arraycopy(floats, 0, m, 0, 9);
    }


    /**
     * Given a 3d coordinate at the specified offset apply this transform in place.
     *
     * @param inVectorData array vectors are read from
     * @param inVectorData array transformed vectors are written to
     * @param inOffset     offset in the in array to read at
     * @param outOffset    offset in the out array to write to
     */
    public final void transformFloatVector(float[] outVectorData, int outOffset, float[] inVectorData, int inOffset)
    {
        float x = inVectorData[inOffset] * m[0] + inVectorData[inOffset + 1] * m[3] + inVectorData[inOffset + 2] * m[6];
        float y = inVectorData[inOffset] * m[1] + inVectorData[inOffset + 1] * m[4] + inVectorData[inOffset + 2] * m[7];
        float z = inVectorData[inOffset] * m[2] + inVectorData[inOffset + 1] * m[5] + inVectorData[inOffset + 2] * m[8];
        outVectorData[outOffset + 0] = x;
        outVectorData[outOffset + 1] = y;
        outVectorData[outOffset + 2] = z;
    }

    /**
     * Apply this transform a series of 3d coordinates in place.
     *
     * @param vectorData array where vectors are stored
     */
    public void applyToFloatVectors(float[] vectorData)
    {
        for (int i = 0; i < vectorData.length; i += 3)
        {
            transformFloatVector(vectorData, i, vectorData, i);
        }
    }

    /**
     * Create new array containing 3d vectors with this matrix transform applied to each
     *
     * @param vectorData array where vectors are read
     * @return the new transformed vector array
     */
    public float[] newTransformedVectors(float[] vectorData)
    {
        float[] transformedVectorData = new float[vectorData.length];
        for (int i = 0; i < vectorData.length; i += 3)
        {
            transformFloatVector(transformedVectorData, i, vectorData, i);
        }
        return transformedVectorData;
    }

}
