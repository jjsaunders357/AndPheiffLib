package com.pheiffware.lib.geometry;

import com.pheiffware.lib.geometry.Vec3D;
import com.pheiffware.lib.graphics.utils.MathUtils;

/**
 * Decomposes any given 4x4 transform matrix into a translation * rotation * scale matrix.
 * Created by Steve on 3/3/2016.
 */
public class Transform3D
{
    //Three core matrices used to describe a transform.  These, when multiplied in order (translation*rotation*scale) they will produce the original transform.
    private float[] translation, rotation, scale;

    public Transform3D(float[] transform)
    {
        //Just copy 3 transform values from matrix
        translation = MathUtils.createTranslationMatrix(transform[12], transform[13], transform[14]);

        float xScale = (float) Math.sqrt(transform[0] * transform[0] + transform[1] * transform[1] + transform[2] * transform[2]);
        float yScale = (float) Math.sqrt(transform[4] * transform[4] + transform[5] * transform[5] + transform[6] * transform[6]);
        float zScale = (float) Math.sqrt(transform[8] * transform[8] + transform[9] * transform[9] + transform[10] * transform[10]);
        scale = MathUtils.createScaleMatrix(xScale, yScale, zScale);

        rotation = new float[]{
                transform[0] / xScale, transform[1] / xScale, transform[2] / xScale, 0,
                transform[4] / yScale, transform[5] / yScale, transform[6] / yScale, 0,
                transform[8] / zScale, transform[9] / zScale, transform[10] / zScale, 0,
                0, 0, 0, 1
        };
    }

    /**
     * Produces a transform appropriate for use on normal vectors.
     *
     * @return
     */
    public final float[] getNormalTransform()
    {
        float[] invScale = new float[]{
                1 / scale[0], 0, 0, 0,
                0, 1 / scale[5], 0, 0,
                0, 0, 1 / scale[10], 0,
                0, 0, 0, 1
        };
        return MathUtils.multiplyMatrix(rotation, invScale);
    }

    public final float[] getTranslation()
    {
        return translation;
    }

    public final float[] getRotation()
    {
        return rotation;
    }

    public final float[] getScale()
    {
        return scale;
    }
}
