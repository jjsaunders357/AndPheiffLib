package com.pheiffware.lib.graphics;

/**
 * This class contains all the information required to perform the same function as a projection matrix, but which produces linear depth.  This has a number of advantages/disadvantages:
 * 1. In practice is rare that its possible to generate a projection*view*model matrix, which can just be multiplied per point.  Usually a non-projected version of the point is required in shaders.
 * 2. When the matrices can't just be multiplied together this is faster.
 * 3. This takes far less data than a matrix (usually doesn't matter).
 * 4. This is used to generate linear depth, rather than the standard squashed depth.
 * 5. This is faster/simpler when extracting depth for comparison in fragment shader.
 * 6. This lacks precision in the close range, but gains precision in the long range.
 * 7. No near plane clipping before depth 0.
 * Created by Steve on 7/4/2017.
 */

public class ProjectionLinearDepth
{
    public final float maxDepth;
    public final float scaleX;
    public final float scaleY;

    /**
     * @param FOV      the vertical field of view
     * @param aspect   ratio of width/height
     * @param maxDepth the maximum depth which can be rendered
     */
    public ProjectionLinearDepth(float FOV, float aspect, float maxDepth, boolean flipVertical)
    {
        //If depth (w) is cos(FOV) and x is sin(FOV), then yn = 1.0 (normalized coordinate)
        //1.0 = scaleY*sin(FOV)/cos(FOV)
        //1.0/scaleY = sin(FOV)/cos(FOV)
        //scaleY = cos(FOV)/sin(FOV)
        //scaleY = cot(FOV)
        float tempScaleY = (float) (1.0 / Math.tan(Math.toRadians(FOV / 2.0)));
        scaleX = tempScaleY / aspect;
        if (flipVertical)
        {
            scaleY = -tempScaleY;
        }
        else
        {
            scaleY = tempScaleY;
        }
        this.maxDepth = maxDepth;
    }
}
