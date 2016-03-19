package com.pheiffware.lib.graphics.utils;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.util.Arrays;

/**
 * Created by Steve on 2/13/2016.
 */
public class MathUtils
{
    //TODO: Remove after replacing with Matrix class
    public static final float[] IDENTITY_MATRIX4 = new float[]{1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};

    public static float[] createTranslationMatrix(float x, float y, float z)
    {
        float[] result = new float[16];
        result[0] = 1;
        result[4] = 0;
        result[8] = 0;
        result[12] = x;
        result[1] = 0;
        result[5] = 1;
        result[9] = 0;
        result[13] = y;
        result[2] = 0;
        result[6] = 0;
        result[10] = 1;
        result[14] = z;
        result[3] = 0;
        result[7] = 0;
        result[11] = 0;
        result[15] = 1;
        return result;
    }

    public static float[] createScaleMatrix(float x, float y, float z)
    {
        float[] result = new float[16];
        result[0] = x;
        result[4] = 0;
        result[8] = 0;
        result[12] = 0;
        result[1] = 0;
        result[5] = y;
        result[9] = 0;
        result[13] = 0;
        result[2] = 0;
        result[6] = 0;
        result[10] = z;
        result[14] = 0;
        result[3] = 0;
        result[7] = 0;
        result[11] = 0;
        result[15] = 1;

        return result;
    }
    public static float[] createRotationMatrix(float angle, float x, float y, float z)
    {
        float[] result = new float[16];
        Matrix.setRotateM(result, 0, angle, x, y, z);
        return result;
    }

    //TODO: Create camera class
    //TODO: Create matrix class

    public static float[] createMatrix3from4(float[] matrix4)
    {
        float[] matrix3 = new float[9];
        int srcIndex = 0;
        int destIndex = 0;

        while (destIndex < 9)
        {
            matrix3[destIndex++] = matrix4[srcIndex++];
            matrix3[destIndex++] = matrix4[srcIndex++];
            matrix3[destIndex++] = matrix4[srcIndex++];
            srcIndex++;
        }
        return matrix3;
    }
    public static float[] createInverseMatrix(float[] transformMatrix)
    {
        float[] inverse = new float[16];
        Matrix.invertM(inverse, 0, transformMatrix, 0);
        return inverse;
    }

    public static float[] createTransposeMatrix(float[] transformMatrix)
    {
        float[] transpose = new float[16];
        int destIndex = 0;
        for (int srcRowIndex = 0; srcRowIndex < 4; srcRowIndex++)
        {
            transpose[destIndex++] = transformMatrix[srcRowIndex + 0];
            transpose[destIndex++] = transformMatrix[srcRowIndex + 4];
            transpose[destIndex++] = transformMatrix[srcRowIndex + 8];
            transpose[destIndex++] = transformMatrix[srcRowIndex + 12];
        }
        return transpose;
    }
    public static float[] createNormalTransformMatrix(float[] transformMatrix)
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

    public static String matrixAsString(float[] matrix)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 16; j += 4)
            {
                builder.append(matrix[i + j]);
                builder.append(",");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * Creates a projection matrix. You generally want to set flipVertical to true when using this to render to a texture as texture coordinates are
     * backward.
     *
     * @param fieldOfViewY
     *            The field of view in the y direction (in degrees)
     *
     * @param aspect
     * @param zNear
     * @param zFar
     * @param flipVertical
     * @return
     */
    public static float[] createProjectionMatrix(float fieldOfViewY, float aspect, float zNear, float zFar, boolean flipVertical)
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

    /**
     * Given an array of non-homogeneous 3d vectors, create a new array homogeneous vectors, append the given element4 to all vectors and create a new array.
     *
     * @param nonHomogeneousVectors float data of the form [x1 y1 z1 x2 y2 z2 ...]
     * @param element4              the value for "w" coordinate to add
     * @return float data of the form [x1 y1 z1 w x2 y2 z2 w ...]
     */
    public static float[] homogenizeVec3Array(float[] nonHomogeneousVectors, float element4)
    {
        float[] homogeneousVectors = new float[(nonHomogeneousVectors.length / 3) * 4];
        int homogeneousIndex = 0, nonHomogeneousIndex = 0;
        while (homogeneousIndex < homogeneousVectors.length)
        {
            homogeneousVectors[homogeneousIndex++] = nonHomogeneousVectors[nonHomogeneousIndex++];
            homogeneousVectors[homogeneousIndex++] = nonHomogeneousVectors[nonHomogeneousIndex++];
            homogeneousVectors[homogeneousIndex++] = nonHomogeneousVectors[nonHomogeneousIndex++];
            homogeneousVectors[homogeneousIndex++] = element4;
        }
        return homogeneousVectors;
    }

    public static float[] multiplyMatrix(float[] lhs, float[] rhs)
    {
        float[] result = new float[16];
        Matrix.multiplyMM(result, 0, lhs, 0, rhs, 0);
        return result;
    }

    public static float[] multiplyMatrices(float[] transform1, float[]... transforms)
    {
        float[] result = transform1;
        for (float[] transform : transforms)
        {
            result = multiplyMatrix(result, transform);
        }
        return result;
    }

    public static float[] copyMatrix(float[] matrix)
    {
        float[] copy = Arrays.copyOf(matrix, 16);
        return copy;
    }
}
