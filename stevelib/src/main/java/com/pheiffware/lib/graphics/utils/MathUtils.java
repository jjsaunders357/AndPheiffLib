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

    //TODO: Create camera class
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

}
