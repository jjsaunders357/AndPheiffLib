package com.pheiffware.lib.graphics;

import android.opengl.Matrix;

import com.pheiffware.lib.geometry.DecomposedTransform3D;
import com.pheiffware.lib.geometry.Vec3D;

import java.util.Arrays;

/**
 * Stores and manipulates a 4x4 matrix.  Stored as a 16 element float array in column major order. This is an enhanced version of the built in Matrix4f. 1. Provides more
 * convenience methods 2. Doesn't have a bugged rotation method (Matrix4f's method does not normalize axis properly). 3. Backed by the native Matrix library, so it is slightly
 * faster
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

    /**
     * Create new rotation matrix around specified axis by given angle (in degrees).
     *
     * @param angle in degrees
     * @param x     rotation axis x
     * @param y     rotation axis y
     * @param z     rotation axis z
     */
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
     * Create new matrix which is the transpose of the given matrix.
     *
     * @param matrix the matrix to transpose
     * @return a new transposed matrix
     */
    public static Matrix4 newTranspose(Matrix4 matrix)
    {
        Matrix4 transpose = new Matrix4(matrix);
        transpose.transpose();
        return transpose;
    }

    /**
     * Creates a projection matrix. You generally want to set flipVertical to true when using this to render to a texture as texture coordinates are backward.
     *
     * @param fieldOfViewY The field of view in the y direction (in degrees)
     * @param aspect       ratio of width/height
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
     * Creates a matrix appropriate for scaling 2D points to Normalized device coordinates (x and y in range [-1,1].
     * Details:
     * It is assumed when working in 2D that the x-coordinates, of visible points, will be in the range [-1,1].
     * For a given aspect ratio (width/height), y-coordinates, of visible points, will be in the range [-1/aspectRatio,1/aspectRatio].
     * Therefore, to convert y-coordinates to the normalized range of [-1,1], we need to scale y by aspectRatio.
     *
     * @param aspect ratio of width/height
     * @return the scaling matrix
     */
    public static Matrix4 newOrtho2D(float aspect)
    {
        return newScale(1f, aspect, 1f);
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
    public static Matrix4 newFromFloats(float[] floats)
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
    public Matrix4(float[] m)
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

    /**
     * Sets matrix to a copy of the given matrix
     *
     * @param matrix4
     */
    public void set(Matrix4 matrix4)
    {
        System.arraycopy(matrix4.m, 0, m, 0, 16);
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
     *
     * @param left   left
     * @param right  right
     * @param bottom bottom
     * @param top    top
     * @param near   near
     * @param far    far
     */
    public final void setOrthographic(float left, float right, float bottom, float top,
                                      float near, float far)
    {
        Matrix.orthoM(m, 0, left, right, bottom, top, near, far);
    }

    public final void setProjection(float fieldOfViewY, float aspect, float nearZ, float farZ, boolean flipVertical)
    {
        float top = (float) (nearZ * Math.tan(Math.PI / 180.0 * fieldOfViewY / 2));
        float right = top * aspect;
        if (flipVertical)
        {
            top *= -1;
        }
        float bottom = -top;
        float left = -right;
        setFrustum(left, right, bottom, top, nearZ, farZ);
    }

    /**
     * @param leftNear   left at the nearZ plane position
     * @param rightNear  right at the nearZ plane position
     * @param bottomNear bottom at the nearZ plane position
     * @param topNear    top at the nearZ plane position
     * @param nearZ      nearZ plane position
     * @param farZ       farZ plane position
     */
    public final void setFrustum(float leftNear, float rightNear, float bottomNear, float topNear,
                                 float nearZ, float farZ)
    {
        Matrix.frustumM(m, 0, leftNear, rightNear, bottomNear, topNear, nearZ, farZ);
    }

    /**
     * Multiply by a translation matrix on the right hand side.
     *
     * @param x x translation
     * @param y y translation
     * @param z z translation
     */
    public final void translateBy(float x, float y, float z)
    {
        Matrix.translateM(m, 0, x, y, z);
    }

    /**
     * Multiply by translation matrix on the left side
     *
     * @param x
     * @param y
     * @param z
     */
    public void translateByLHS(float x, float y, float z)
    {
        m[12] += x;
        m[13] += y;
        m[14] += z;
    }

    /**
     * Changes the matrix translation terms, leaving everything else intact.
     *
     * @param x
     * @param y
     * @param z
     */
    public void modifyTranslation(float x, float y, float z)
    {
        m[12] = x;
        m[13] = y;
        m[14] = z;
    }

    /**
     * Extracts the matrix translation terms as 3d vector.
     *
     * @return
     */
    public Vec3D getTranslation()
    {
        return new Vec3D(m[12], m[13], m[14]);
    }

    /**
     * Multiply by a rotation matrix on the right hand side.
     *
     * @param angle degrees to rotate
     * @param x     x axis component
     * @param y     y axis component
     * @param z     z axis component
     */
    public final void rotateBy(float angle, float x, float y, float z)
    {
        Matrix.rotateM(m, 0, angle, x, y, z);
    }

    /**
     * Multiply by a scale matrix on the right hand side.
     *
     * @param x x scale
     * @param y y scale
     * @param z z scale
     */
    public final void scaleBy(float x, float y, float z)
    {
        Matrix.scaleM(m, 0, x, y, z);
    }

    public final void multiplyBy(Matrix4 rhs)
    {
        float[] lhs = Arrays.copyOf(m, 16);
        Matrix.multiplyMM(this.m, 0, lhs, 0, rhs.m, 0);
    }

    public final void multiplyByLHS(Matrix4 lhs)
    {
        float[] rhs = Arrays.copyOf(m, 16);
        Matrix.multiplyMM(this.m, 0, lhs.m, 0, rhs, 0);
    }

    /**
     * Transpose the matrix in place.
     */
    public final void transpose()
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
    public final void invert()
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

    /**
     * Create a 3x3 normal transformation matrix from this matrix.
     *
     * @return normal transform matrix
     */
    public Matrix3 newNormalTransformMatrix3()
    {
        Matrix3 normalTransform = Matrix3.newZeroMatrix();
        normalTransform.setNormalTransformFromMatrix4Fast(this);
        return normalTransform;
    }

    /**
     * Transforms the given 3D point by the matrix.  The point is assumed to be homogeneous with a value of 1 for "w".
     *
     * @param point
     * @return transformed point
     */
    public final Vec3D transformHomogeneousPoint(Vec3D point)
    {
        double x = point.x * m[0] + point.y * m[4] + point.z * m[8] + 1 * m[12];
        double y = point.x * m[1] + point.y * m[5] + point.z * m[9] + 1 * m[13];
        double z = point.x * m[2] + point.y * m[6] + point.z * m[10] + 1 * m[14];
        return new Vec3D(x, y, z);
    }

    /**
     * Transforms the given 3D vector by the matrix.  The vector has an assumed assumed "w" value of 0.
     *
     * @param vector
     * @return transformed point
     */
    public Vec3D transformVector(Vec3D vector)
    {
        double x = vector.x * m[0] + vector.y * m[4] + vector.z * m[8];
        double y = vector.x * m[1] + vector.y * m[5] + vector.z * m[9];
        double z = vector.x * m[2] + vector.y * m[6] + vector.z * m[10];
        return new Vec3D(x, y, z);
    }

    public final float[] transform4DFloatVector(float[] inVectorData)
    {
        float[] outVectorData = new float[4];
        transform4DFloatVector(outVectorData, 0, inVectorData, 0);
        return outVectorData;
    }

    /**
     * Given a 4d coordinate at the specified offset in the inVectorData array, apply this transform in the outVectorData at the offset. inVectorData and outVectorData CAN be the
     * same array and read/write position can overlap.
     *
     * @param inVectorData array vectors are read from
     * @param inVectorData array transformed vectors are written to
     * @param inOffset     offset in the in array to read at
     * @param outOffset    offset in the out array to write to
     */
    public final void transform4DFloatVector(float[] outVectorData, int outOffset, float[] inVectorData, int inOffset)
    {
        float x = inVectorData[inOffset] * m[0] + inVectorData[inOffset + 1] * m[4] + inVectorData[inOffset + 2] * m[8] + inVectorData[inOffset + 3] * m[12];
        float y = inVectorData[inOffset] * m[1] + inVectorData[inOffset + 1] * m[5] + inVectorData[inOffset + 2] * m[9] + inVectorData[inOffset + 3] * m[13];
        float z = inVectorData[inOffset] * m[2] + inVectorData[inOffset + 1] * m[6] + inVectorData[inOffset + 2] * m[10] + inVectorData[inOffset + 3] * m[14];
        float w = inVectorData[inOffset] * m[3] + inVectorData[inOffset + 1] * m[7] + inVectorData[inOffset + 2] * m[11] + inVectorData[inOffset + 3] * m[15];
        outVectorData[outOffset + 0] = x;
        outVectorData[outOffset + 1] = y;
        outVectorData[outOffset + 2] = z;
        outVectorData[outOffset + 3] = w;
    }

    /**
     * Apply this transform a series of 4d coordinates in place.
     *
     * @param vectorData array where vectors are stored
     */
    public void transformFloatVectors(float[] vectorData)
    {
        for (int i = 0; i < vectorData.length; i += 4)
        {
            transform4DFloatVector(vectorData, i, vectorData, i);
        }
    }

    /**
     * Create new array containing 4d coordinates with this tranform applied to each
     *
     * @param vectorData array where vectors are read
     * @return the new transformed vector array
     */
    public float[] newTransformedVectors(float[] vectorData)
    {
        float[] transformedVectorData = new float[vectorData.length];
        for (int i = 0; i < vectorData.length; i += 4)
        {
            transform4DFloatVector(transformedVectorData, i, vectorData, i);
        }
        return transformedVectorData;
    }

    //Used for testing if a matrix is identity.
    private static float[] identity = new float[]{1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};

    /**
     * Checks if the matrix is the identity matrix within the given tolerance.  Tolerance is applied per element of the matrix.
     *
     * @return
     */
    public boolean isIdentity(float tolerance)
    {
        for (int index = 0; index < 16; index++)
        {
            float diff = Math.abs(identity[index] - m[index]);
            if (diff > tolerance)
            {
                return false;
            }
        }
        return true;
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
