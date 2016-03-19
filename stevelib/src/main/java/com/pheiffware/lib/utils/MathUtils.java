package com.pheiffware.lib.utils;

/**
 * Created by Steve on 3/18/2016.
 */
public class MathUtils
{
    /**
     * Returns an angular difference between 2 angles.  The result will always be in [-pi,pi]
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
}
