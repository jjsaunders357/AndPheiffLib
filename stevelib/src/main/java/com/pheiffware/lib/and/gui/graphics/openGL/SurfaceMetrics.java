package com.pheiffware.lib.and.gui.graphics.openGL;

/**
 * Passed into GameRenderer on surface creation.
 * <p/>
 * Created by Steve on 8/12/2016.
 */
public class SurfaceMetrics
{
    public final double xdpi, ydpi;

    public SurfaceMetrics(double xdpi, double ydpi)
    {
        this.xdpi = xdpi;
        this.ydpi = ydpi;
    }
}
