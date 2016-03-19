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

    public static Matrix4 multiply(Matrix4 lhs, Matrix4 rhs)
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

    public static Matrix4 newTrans(float x, float y, float z)
    {
        Matrix4 matrix = new Matrix4();
        matrix.setTranslate(x, y, z);
        return matrix;
    }

    public static Matrix4 newScale(float x, float y, float z)
    {
        Matrix4 matrix = new Matrix4();
        matrix.setScale(x, y, z);
        return matrix;
    }

    public static Matrix4 newRotate(float angle, float x, float y, float z)
    {
        Matrix4 matrix = new Matrix4();
        matrix.setRotate(angle, x, y, z);
        return matrix;
    }


    public static Matrix3 newMatrix3from4(Matrix4 matrix4)
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

    public static Matrix3 newNormalTransform(Matrix4 transformMatrix)
    {
        float[] matrix = Arrays.copyOf(transformMatrix.m, 16);
        matrix[12] = 0;
        matrix[13] = 0;
        matrix[14] = 0;
        float[] inverse = new float[16];
        Matrix.invertM(inverse, 0, matrix, 0);
        Matrix.transposeM(matrix, 0, inverse, 0);
        return Matrix3.newMatrix3From4Floats(matrix);
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
    public static Matrix4 newProjection(float fieldOfViewY, float aspect, float zNear, float zFar, boolean flipVertical)
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
        return new Matrix4(matrix);
    }

    public final float[] m;

    public static Matrix4 fromFloats(float[] m)
    {
        return new Matrix4(Arrays.copyOf(m, 16));
    }
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

    public final void setTranslate(float x, float y, float z)
    {
        //@formatter:off
        m[0] = 1;m[4] = 0;m[8] =  0;m[12] = x;
        m[1] = 0;m[5] = 1;m[9] =  0;m[13] = y;
        m[2] = 0;m[6] = 0;m[10] = 1;m[14] = z;
        m[3] = 0;m[7] = 0;m[11] = 0;m[15] = 1;
        //@formatter:on
    }

    public final void setRotate(float angle, float x, float y, float z)
    {
        Matrix.setRotateM(m, 0, angle, x, y, z);
    }

    public final void setScale(float x, float y, float z)
    {
        //@formatter:off
        m[0] = x;m[4] = 0;m[8] =  0;m[12] = 0;
        m[1] = 0;m[5] = y;m[9] =  0;m[13] = 0;
        m[2] = 0;m[6] = 0;m[10] = z;m[14] = 0;
        m[3] = 0;m[7] = 0;m[11] = 0;m[15] = 1;
        //@formatter:on

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
