package com.pheiffware.lib.graphics;

/**
 * Created by Steve on 3/9/2016.
 */
public class Matrix3
{
    public final float[] m;

    //Doesn't copy
    private Matrix3(float[] m)
    {
        this.m = m;
    }

    /**
     * Create a matrix 3 from a 16 floats representing a 4x4 matrix (upper left of matrix).
     *
     * @param m matrix data
     * @return
     */
    public static Matrix3 newMatrix3From4Floats(float[] m)
    {
        float[] matrix3 = new float[9];
        int srcIndex = 0;
        int destIndex = 0;

        while (destIndex < 9)
        {
            matrix3[destIndex++] = m[srcIndex++];
            matrix3[destIndex++] = m[srcIndex++];
            matrix3[destIndex++] = m[srcIndex++];
            srcIndex++;
        }
        return new Matrix3(matrix3);
    }

}
