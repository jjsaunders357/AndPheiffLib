package com.pheiffware.lib.graphics;

import android.opengl.Matrix;

import com.pheiffware.lib.graphics.utils.MathUtils;

import java.util.Arrays;

/**
 * Created by Steve on 3/9/2016.
 */
public class Matrix4
{
    public static final Matrix4 IDENTITY_MATRIX4 = new Matrix4(new float[]{1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1});

    public static final Matrix4 multiply(Matrix4 lhs, Matrix4 rhs)
    {
        float[] product = new float[16];
        Matrix.multiplyMM(product, 0, lhs.m, 0, rhs.m, 0);
        return new Matrix4(product);
    }

    public static Matrix4 multiply(Matrix4 lhs, Matrix4... matrices)
    {
        Matrix4 product = new Matrix4(lhs);
        for (Matrix4 matrix : matrices)
        {
            product = multiply(product, matrix);
        }
        return product;
    }

    public static final Matrix4 newTrans(float x, float y, float z)
    {
        float[] t = new float[16];
        //@formatter:off
        t[0] = 1;t[4] = 0;t[8] =  0;t[12] = x;
        t[1] = 0;t[5] = 1;t[9] =  0;t[13] = y;
        t[2] = 0;t[6] = 0;t[10] = 1;t[14] = z;
        t[3] = 0;t[7] = 0;t[11] = 0;t[15] = 1;
        //@formatter:on
        return new Matrix4(t);
    }

    public static final Matrix4 newScale(float x, float y, float z)
    {
        float[] s = new float[16];
        //@formatter:off
        s[0] = x;s[4] = 0;s[8] =  0;s[12] = 0;
        s[1] = 0;s[5] = y;s[9] =  0;s[13] = 0;
        s[2] = 0;s[6] = 0;s[10] = z;s[14] = 0;
        s[3] = 0;s[7] = 0;s[11] = 0;s[15] = 1;
        //@formatter:on
        return new Matrix4(s);
    }

    public static Matrix4 newRotate(float angle, float x, float y, float z)
    {
        float[] result = new float[16];
        Matrix.setRotateM(result, 0, angle, x, y, z);
        return new Matrix4(result);
    }

    public static Matrix3 createMatrix3from4(Matrix4 matrix4)
    {
        return Matrix3.newMatrix3From4Floats(matrix4.m);
    }

    public static Matrix4 newInverse(Matrix4 matrix)
    {
        float[] inverse = new float[16];
        Matrix.invertM(inverse, 0, matrix.m, 0);
        return new Matrix4(inverse);
    }

    public static Matrix4 newTranspose(Matrix4 transformMatrix)
    {
        float[] transpose = new float[16];
        float[] mData = transformMatrix.m;

        int destIndex = 0;
        for (int srcRowIndex = 0; srcRowIndex < 4; srcRowIndex++)
        {
            transpose[destIndex++] = mData[srcRowIndex + 0];
            transpose[destIndex++] = mData[srcRowIndex + 4];
            transpose[destIndex++] = mData[srcRowIndex + 8];
            transpose[destIndex++] = mData[srcRowIndex + 12];
        }
        return new Matrix4(transpose);
    }

    public static float[] newNormalTransform(float[] transformMatrix)
    {
        float[] matrix = Arrays.copyOf(transformMatrix, 16);
        matrix[12] = 0;
        matrix[13] = 0;
        matrix[14] = 0;
        float[] inverse = new float[16];
        Matrix.invertM(inverse, 0, matrix, 0);
        Matrix.transposeM(matrix, 0, inverse, 0);
        return MathUtils.createMatrix3from4(matrix);
    }


    /**
     * Creates a projection matrix. You generally want to set flipVertical to true when using this to render to a texture as texture coordinates are
     * backward.
     *
     * @param fieldOfViewY The field of view in the y direction (in degrees)
     * @param aspect
     * @param zNear
     * @param zFar
     * @param flipVertical
     * @return
     */
    public static float[] newProjection(float fieldOfViewY, float aspect, float zNear, float zFar, boolean flipVertical)
    {
        float[] matrix = new float[16];

        float top = (float) (zNear * Math.tan(Math.PI / 180.0 * fieldOfViewY / 2));
        float right = top * aspect;
        if (flipVertical)
        {
            top *= -1;
        }
        float bottom = -top;
        float left = -right;
        Matrix.frustumM(matrix, 0, left, right, bottom, top, zNear, zFar);
        return matrix;
    }

    public final float[] m;

    //Doesn't make copy of data
    private Matrix4(float[] m)
    {
        this.m = m;
    }

    public Matrix4()
    {
        this.m = new float[16];
    }

    public Matrix4(Matrix4 matrix)
    {
        m = Arrays.copyOf(matrix.m, 16);
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 16; j += 4)
            {
                builder.append(m[i + j]);
                builder.append(",");
            }
            builder.append("\n");
        }
        return builder.toString();

    }
}
