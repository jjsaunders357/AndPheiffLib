package com.pheiffware.lib.graphics;

import android.opengl.Matrix;
import android.renderscript.Matrix4f;

import com.pheiffware.lib.geometry.DecomposedTransform3D;

import java.util.Arrays;

/**
 * Stores and manipulates a 4x4 matrix.  Stored as a 16 element float array in column major order.
 * This is an enhanced version of the built in Matrix4f.
 * 1. Provides more convenience methods
 * 2. Doesn't have a bugged rotation method (Matrix4f's method does not normalize axis properly).
 * 3. Backed by the native Matrix library, so it is slightly faster
 * <p/>
 * Created by Steve on 3/9/2016.
 */
public class Matrix4
{
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

    public static Matrix4 newTranslation(float x, float y, float z)
    {
        Matrix4 matrix = new Matrix4(new float[16]);
        matrix.setTranslate(x, y, z);
        return matrix;
    }

    public static Matrix4 newScale(float x, float y, float z)
    {
        Matrix4 matrix = new Matrix4(new float[16]);
        matrix.setScale(x, y, z);
        return matrix;
    }

    public static Matrix4 newRotate(float angle, float x, float y, float z)
    {
        Matrix4 matrix = new Matrix4(new float[16]);
        matrix.setRotate(angle, x, y, z);
        return matrix;
    }

    /**
     * Create new matrix which is the inverse of the given matrix.
     *
     * @param matrix the matrix to invert
     * @return a new inverted matrix
     */
    public static Matrix4 newInverse(Matrix4 matrix)
    {
        Matrix4 inverse = new Matrix4(matrix);
        inverse.invert();
        return inverse;
    }

    /**
     * Creates a projection matrix. You generally want to set flipVertical to true when using this to render to a texture as texture coordinates are
     * backward.
     *
     * @param fieldOfViewY The field of view in the y direction (in degrees)
     * @param aspect       ratio of width to height
     * @param near         distance to near view plane
     * @param far          distance to far view plane
     * @param flipVertical flip vertical when rendering
     * @return the projection matrix
     */
    public static Matrix4 newProjection(float fieldOfViewY, float aspect, float near, float far, boolean flipVertical)
    {
        Matrix4 matrix = new Matrix4();
        matrix.setProjection(fieldOfViewY, aspect, near, far, flipVertical);
        return matrix;
    }


    /**
     * Creates an empty matrix
     *
     * @return new zero matrix
     */
    public static Matrix4 newZeroMatrix()
    {
        return new Matrix4();
    }

    /**
     * Creates new identity matrix
     *
     * @return new identity matrix
     */
    public static Matrix4 newIdentity()
    {
        Matrix4 matrix = new Matrix4();
        matrix.setIdentity();
        return matrix;
    }

    /**
     * Creates new 4x4 matrix from given floats.
     *
     * @param floats 16 floats in column major order
     * @return new 4x4 matrix from given floats
     */
    public static Matrix4 newMatrixFromFloats(float[] floats)
    {
        return new Matrix4(Arrays.copyOf(floats, 16));
    }

    //Stored matrix data in column major order
    public final float[] m;

    /**
     * Constructs a new blank matrix
     */
    private Matrix4()
    {
        m = new float[16];
    }

    /**
     * Internal constructor which does NOT copy data
     *
     * @param m reference to array which should back this matrix.
     */
    private Matrix4(float[] m)
    {
        this.m = m;
    }

    /**
     * Creates a new copy of the given matrix.
     *
     * @param matrix matrix to copy
     */
    public Matrix4(Matrix4 matrix)
    {
        m = Arrays.copyOf(matrix.m, 16);
    }

    public final void setIdentity()
    {
        //@formatter:off
        m[0] = 1;m[4] = 0;m[8] =  0;m[12] = 0;
        m[1] = 0;m[5] = 1;m[9] =  0;m[13] = 0;
        m[2] = 0;m[6] = 0;m[10] = 1;m[14] = 0;
        m[3] = 0;m[7] = 0;m[11] = 0;m[15] = 1;
        //@formatter:on
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

    /**
     * Set matrix state to a rotation around specified axis by given angle (in degrees).
     *
     * @param angle in degrees
     * @param x     rotation axis x
     * @param y     rotation axis y
     * @param z     rotation axis z
     */

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

    /**
     * Create a new orthographic projection matrix.
     * @param left left
     * @param right right
     * @param bottom bottom
     * @param top top
     * @param near near
     * @param far far
     */
    public final void setOrthographic(float left, float right, float bottom, float top,
                                      float near, float far)
    {
        Matrix.orthoM(m, 0, left, right, bottom, top, near, far);
    }

    public final void setProjection(float fieldOfViewY, float aspect, float near, float far, boolean flipVertical)
    {
        float top = (float) (near * Math.tan(Math.PI / 180.0 * fieldOfViewY / 2));
        float right = top * aspect;
        if (flipVertical)
        {
            top *= -1;
        }
        float bottom = -top;
        float left = -right;
        setFrustum(left, right, bottom, top, near, far);
    }

    public final void setFrustum(float left, float right, float bottom, float top,
                                 float near, float far)
    {
        Matrix.frustumM(m, 0, left, right, bottom, top, near, far);
    }

    public void translateRhs(float x, float y, float z)
    {
        Matrix.translateM(m, 0, x, y, z);
    }

    public void rotateRhs(float angle, float x, float y, float z)
    {
        Matrix.rotateM(m, 0, angle, x, y, z);
    }

    public void scaleRhs(float x, float y, float z)
    {
        Matrix.scaleM(m, 0, x, y, z);
    }

    /**
     * Transpose the matrix in place.
     */
    public void transpose()
    {
        float temp;
        temp = m[1];
        m[1] = m[4];
        m[4] = temp;

        temp = m[2];
        m[2] = m[8];
        m[8] = temp;

        temp = m[6];
        m[6] = m[9];
        m[9] = temp;

        temp = m[3];
        m[3] = m[12];
        m[12] = temp;

        temp = m[7];
        m[7] = m[13];
        m[13] = temp;

        temp = m[11];
        m[11] = m[14];
        m[14] = temp;
    }

    /**
     * Invert the matrix in place
     */
    public void invert()
    {
        float[] original = Arrays.copyOf(m, 16);
        Matrix.invertM(m, 0, original, 0);
    }

    /**
     * Decompose transformation matrix into a translation, rotation and scale.
     *
     * @return a decomposed matrix object
     */
    public DecomposedTransform3D decompose()
    {
        //Just copy 3 transform values from matrix
        Matrix4 translation = newTranslation(m[12], m[13], m[14]);

        float xScale = (float) Math.sqrt(m[0] * m[0] + m[1] * m[1] + m[2] * m[2]);
        float yScale = (float) Math.sqrt(m[4] * m[4] + m[5] * m[5] + m[6] * m[6]);
        float zScale = (float) Math.sqrt(m[8] * m[8] + m[9] * m[9] + m[10] * m[10]);
        Matrix4 scale = newScale(xScale, yScale, zScale);

        Matrix4 rotation = new Matrix4(new float[]{
                m[0] / xScale, m[1] / xScale, m[2] / xScale, 0,
                m[4] / yScale, m[5] / yScale, m[6] / yScale, 0,
                m[8] / zScale, m[9] / zScale, m[10] / zScale, 0,
                0, 0, 0, 1
        });
        return new DecomposedTransform3D(translation, rotation, scale);
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
