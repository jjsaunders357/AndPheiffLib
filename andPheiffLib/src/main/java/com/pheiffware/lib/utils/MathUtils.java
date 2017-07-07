package com.pheiffware.lib.utils;

/**
 * Created by Steve on 3/18/2016.
 */
public class MathUtils
{
    /**
     * Returns an angular difference between 2 angles.  The result will always be in interval [-pi,pi)
     *
     * @param angle1
     * @param angle2
     * @return
     */
    public static double angleDiff(double angle1, double angle2)
    {
        double diff = angle1 - angle2;
        if (diff < -Math.PI)
        {
            diff += Math.PI * 2;
        }
        else if (diff >= Math.PI)
        {
            diff -= Math.PI * 2;
        }
        return diff;
    }

    /**
     * For a given position find the next even block boundary.
     * Example: position = 13, blockSize = 8 : returns 16
     * <p>
     * Example: position = 8, blockSize = 8 : returns 8
     *
     * @param position  the position
     * @param blockSize the block size to use for finding the boundary
     * @return current position or next closest position at even block boundary
     */
    public static int calcNextEvenBoundary(int position, int blockSize)
    {
        int mod = position % blockSize;
        return position + ((blockSize - mod) % blockSize);
    }

}
