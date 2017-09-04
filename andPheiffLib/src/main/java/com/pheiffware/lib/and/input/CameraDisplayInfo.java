package com.pheiffware.lib.and.input;

/**
 * Created by Steve on 9/3/2017.
 */

public class CameraDisplayInfo
{
    public final float avgFocalLengthMM;
    public final float widthMM;
    public final float heightMM;
    public final float pixelAspect;
    public final int cameraOrientation;
    public final float fovX;
    public final float fovY;

    public CameraDisplayInfo(float avgFocalLengthMM, float widthMM, float heightMM, float pixelAspect, int cameraOrientation)
    {
        this.avgFocalLengthMM = avgFocalLengthMM;
        this.widthMM = widthMM;
        this.heightMM = heightMM;
        this.pixelAspect = pixelAspect;
        this.cameraOrientation = cameraOrientation;
        fovX = (float) Math.toDegrees(2 * Math.atan(widthMM / (2 * avgFocalLengthMM)));
        fovY = (float) Math.toDegrees(2 * Math.atan(heightMM / (2 * avgFocalLengthMM)));
    }
}
