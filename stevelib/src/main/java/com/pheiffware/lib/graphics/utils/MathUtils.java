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

}
