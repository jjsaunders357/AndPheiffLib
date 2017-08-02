package com.pheiffware.lib.geometry;

/**
 * Created by Steve on 8/1/2017.
 */

public enum Axis
{
    X(0, 1), Y(1, 1), Z(2, 1), W(3, 1), negX(0, -1), negY(1, -1), negZ(2, -1), negW(3, -1);

    public final int axisIndex;
    public final int sign;

    Axis(int axisIndex, int sign)
    {
        this.axisIndex = axisIndex;
        this.sign = sign;
    }
}
