package com.pheiffware.lib.graphics;

import com.pheiffware.lib.geometry.Angle;

/**
 * For manipulating 1 or more vectors 4D vectors stored in float arrays.
 * Created by Steve on 7/8/2017.
 */

public class Vec4F
{
    //Data array holding all vectors
    private float[] data;

    //4 separate offsets are maintained
    private int offset;

    public Vec4F(int size)
    {
        this(new float[size * 4], 0);
    }

    public Vec4F(float[] data)
    {
        this(data, 0);
    }

    public Vec4F(float[] data, int offset)
    {
        this.data = data;
        this.offset = offset;
    }

    public Vec4F(float x, float y, float z, float w)
    {
        data = new float[4];
        offset = 0;
        set(x, y, z, w);
    }

    public Vec4F copy()
    {
        Vec4F copy = new Vec4F(size());
        copy.copyAll(this);
        return copy;
    }

    public final void set(float x, float y, float z, float w)
    {
        data[0] = x;
        data[1] = y;
        data[2] = z;
        data[3] = w;
    }

    public final void set(float[] data, int offset)
    {
        this.data = data;
        this.offset = offset;
    }

    public void copy(float[] data)
    {
        setX(data[0]);
        setY(data[1]);
        setZ(data[2]);
        setW(data[3]);
    }

    public void copy(Vec4F vec)
    {
        setX(vec.x());
        setY(vec.y());
        setZ(vec.z());
        setW(vec.w());
    }

    public void copyMulti(Vec4F vec, int source, int dest, int num)
    {
        copyMulti(vec.data, source, dest, num);
    }

    public void copyMulti(float[] data, int source, int dest, int num)
    {
        copyData(data, source * 4, dest * 4, num * 4);
    }

    public void copyAll(Vec4F vec)
    {
        copyAll(vec.data);
    }

    public void copyAll(float[] data)
    {
        copyData(data, 0, 0, Math.min(size() * 4, data.length));
    }

    private void copyData(float[] data, int sourceOffset, int destOffset, int length)
    {
        System.arraycopy(data, sourceOffset, this.data, destOffset, length);
    }

    public final void setIndex(int vectorIndex)
    {
        this.offset = vectorIndex * 4;
    }

    public final boolean next()
    {
        offset += 4;
        return offset < data.length;
    }

    public final float magnitudeSquared()
    {
        return data[offset] * data[offset] + data[offset + 1] * data[offset + 1] + data[offset + 2] * data[offset + 2] + data[offset + 3] * data[offset + 3];
    }

    public final float magnitude()
    {
        return (float) Math.sqrt(magnitudeSquared());
    }

    public static float dot(Vec4F v1, Vec4F v2)
    {
        return v1.x() * v2.x() + v1.y() * v2.y() + v1.z() * v2.z() + v1.w() * v2.w();
    }

    public final void multiplyBy(float[] vec)
    {
        setX(x() * vec[0]);
        setY(y() * vec[1]);
        setZ(z() * vec[2]);
        setW(w() * vec[3]);
    }

    public final void multiplyBy(Vec4F vec)
    {
        setX(x() * vec.x());
        setY(y() * vec.y());
        setZ(z() * vec.z());
        setW(w() * vec.w());
    }

    public final void multiplyEachBy(Vec4F vec)
    {
        setIndex(0);
        for (int i = 0; i < size(); i++)
        {
            multiplyBy(vec);
            next();
        }
    }

    public final void multiplyEachByEach(Vec4F vec)
    {
        setIndex(0);
        vec.setIndex(0);
        for (int i = 0; i < size(); i++)
        {
            multiplyBy(vec);
            next();
            vec.next();
        }
    }

    public final void transformBy(Matrix4 matrix)
    {
        matrix.transform4DFloatVector(data, offset, data, offset);
    }

    public void transformByAll(Matrix4 matrix)
    {
        transformByMulti(matrix, 0, size());
    }

    public final void transformByMulti(Matrix4 matrix, int index, int num)
    {
        setIndex(index);
        for (int i = 0; i < num; i++)
        {
            transformBy(matrix);
            next();
        }
    }

    /**
     * Rotate this vector away from the given vector until perpendicular and normalize the result.
     *
     * @param perpendicular
     */
    public final void setPerpendicularUnit(Vec4F perpendicular)
    {
        float dot = dot(this, perpendicular);
        setX(x() - perpendicular.x() * dot);
        setY(y() - perpendicular.y() * dot);
        setZ(z() - perpendicular.z() * dot);
        setW(w() - perpendicular.w() * dot);
        normalize();
    }

    private void normalize()
    {
        float magnitude = magnitude();
        scale(1f / magnitude);
    }

    public void scale(float scale)
    {
        setX(x() / scale);
        setY(y() / scale);
        setZ(z() / scale);
        setW(w() / scale);
    }

    /**
     * Rotate this towards another vector which is perpendicular, by a given amount.
     *
     * @param towardsPerp
     * @param angle
     */
    public final void rotateTowardsPerp(Vec4F towardsPerp, Angle angle)
    {
        setValues(
                x() * angle.cos + towardsPerp.x() * angle.sin,
                y() * angle.cos + towardsPerp.y() * angle.sin,
                z() * angle.cos + towardsPerp.z() * angle.sin,
                w() * angle.cos + towardsPerp.w() * angle.sin
        );
    }

    public final void setValues(float x, float y, float z, float w)
    {
        data[offset] = x;
        data[offset + 1] = y;
        data[offset + 2] = z;
        data[offset + 3] = w;
    }

    public final float setX(float x)
    {
        return data[offset] = x;
    }

    public final float setY(float y)
    {
        return data[offset + 1] = y;
    }

    public final float setZ(float z)
    {
        return data[offset + 2] = z;
    }

    public final float setW(float w)
    {
        return data[offset + 3] = w;
    }

    public final float x()
    {
        return data[offset];
    }

    public final float y()
    {
        return data[offset + 1];
    }

    public final float z()
    {
        return data[offset + 2];
    }

    public final float w()
    {
        return data[offset + 3];
    }

    public int size()
    {
        return data.length / 4;
    }

    public float[] getData()
    {
        return data;
    }

}
