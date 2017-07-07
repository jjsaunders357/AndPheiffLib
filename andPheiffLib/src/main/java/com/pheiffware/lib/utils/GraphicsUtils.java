package com.pheiffware.lib.utils;

import android.graphics.RectF;

/**
 * Created by Steve on 2/13/2016.
 */
public class GraphicsUtils
{
    /**
     * Multiply 2 vectors component by component.  Store in out vector.
     *
     * @param length how many components to go through
     * @param out    where result is written
     * @param vec1   input vector
     * @param vec2   input vector
     */
    public static void vecMultiply(int length, float[] out, float[] vec1, float[] vec2)
    {
        for (int i = 0; i < length; i++)
        {
            out[i] = vec1[i] * vec2[i];
        }
    }

    /**
     * Multiply 2 vectors component by component.  Store at given offset in out vector.
     *
     * @param length    how many components to go through
     * @param outOffset output vector write offset
     * @param out       where result is written
     * @param vec1      input vector
     * @param vec2      input vector
     */
    public static void vecMultiply(int length, int outOffset, float[] out, float[] vec1, float[] vec2)
    {
        for (int i = 0; i < length; i++)
        {
            out[outOffset] = vec1[i] * vec2[i];
            outOffset++;
        }
    }

    /**
     * Multiply 2 vectors component by component.  Store at given offset in out vector.  Read from given offsets of input vectors
     *
     * @param length    how many components to go through
     * @param outOffset output vector write offset
     * @param out       where result is written
     * @param v1Offset  input vector read offset
     * @param vec1      input vector
     * @param v2Offset  input vector read offset
     * @param vec2      input vector
     */
    public static void vecMultiply(int length, int outOffset, float[] out, int v1Offset, float[] vec1, int v2Offset, float[] vec2)
    {
        for (int i = 0; i < length; i++)
        {
            out[outOffset] = vec1[v1Offset] * vec2[v2Offset];
            outOffset++;
            v1Offset++;
            v2Offset++;
        }
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

    /**
     * Given a viewing area with a given width/height and something to render with a given aspect ratio, calculate the largest rectangle with the renderAspectRatio that fits in the
     * view.  Rectangle will be centered.
     *
     * @param viewWidth         available width to display in
     * @param viewHeight        available height to display in
     * @param renderAspectRatio the aspect ratio of what should be displayed in the view (width/height).  Sign is ignored.
     * @return a rectangle with maximum width/height with appropriate offset to center it
     */
    public static RectF calcRenderViewRectangle(int viewWidth, int viewHeight, float renderAspectRatio)
    {
        boolean widthConstrained;
        if (viewHeight == 0)
        {
            widthConstrained = true;
        }
        else
        {
            float viewAspect = viewWidth / (float) viewHeight;
            widthConstrained = Math.abs(renderAspectRatio) > Math.abs(viewAspect);
        }
        float width, height;
        float x, y;
        if (widthConstrained)
        {
            //We are width constrained (we need to use all of the width, but only some of the height)
            width = viewWidth;
            x = 0;
            height = viewWidth / renderAspectRatio;
            y = (viewHeight - height) / 2;
        }
        else
        {
            //We are height constrained (we need to use all of the height, but only some of the width)
            height = viewHeight;
            y = 0;
            width = viewHeight * renderAspectRatio;
            x = (viewWidth - width) / 2;
        }
        return new RectF(x, y, x + width, y + height);
    }
}
