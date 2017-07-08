package com.pheiffware.lib.and.gui.graphics.openGL;

/**
 * Passed into BaseGameRenderer on surface creation.
 * <p/>
 * Created by Steve on 8/12/2016.
 */
public class SystemInfo
{
    public final double xdpi, ydpi;

    public SystemInfo(double xdpi, double ydpi)
    {
        this.xdpi = xdpi;
        this.ydpi = ydpi;
    }
}
