package com.pheiffware.lib.geometry;

import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;

/**
 * Holds a decomposed transformation formed by translation * rotation * scale matrix.
 * Created by Steve on 3/3/2016.
 */
public class DecomposedTransform3D
{
    private final Matrix4 translation, rotation, scale;

    public DecomposedTransform3D(Matrix4 translation, Matrix4 rotation, Matrix4 scale)
    {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Matrix4 getTranslation()
    {
        return translation;
    }

    public Matrix4 getRotation()
    {
        return rotation;
    }

    public Matrix4 getScale()
    {
        return scale;
    }

    /**
     * Compose new single 4x4 matrix representing transform
     *
     * @return
     */
    public Matrix4 compose()
    {
        return Matrix4.multiply(translation, rotation, scale);
    }

    /**
     * Create a normal transformation matrix from decomposed matrix
     *
     * @return
     */
    public Matrix3 getNormalTransform()
    {
        Matrix4 normal = new Matrix4(rotation);
        normal.scaleBy(1 / scale.m[0], 1 / scale.m[5], 1 / scale.m[10]);
        return Matrix3.newFromUpperLeft(normal);
    }
}
