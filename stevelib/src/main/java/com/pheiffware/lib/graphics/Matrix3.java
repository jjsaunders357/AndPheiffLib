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
    public static Matrix3 newMatrixFromFloats(float[] floats)
    {
        return new Matrix3(Arrays.copyOf(floats, 9));
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
     * @param matrix4 4x4 transform to compute normal transform from
     */
    public void setNormalTransformFromMatrix4(Matrix4 matrix4)
    {
        setMatrix4UpperLeft(matrix4);
        invert();
        transpose();
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
     * @param row row
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

}
