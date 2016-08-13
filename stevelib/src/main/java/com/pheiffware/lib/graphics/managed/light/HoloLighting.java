package com.pheiffware.lib.graphics.managed.light;

import com.pheiffware.lib.graphics.Matrix4;

/**
 * A version of the Lighting class which is customized for holographic lighting.  This allows lights to either track with the eye or with the screen.
 * <p/>
 * Created by Steve on 7/25/2016.
 */
public class HoloLighting extends Lighting
{
    private final boolean[] eyeSpace;

    /**
     * Creates a Lighting object representing the set of lights to use for rendering.  Each light's position and color is encoded as a 4 element block in the corresponding array.
     * Any additional lights supported by the implementation will be turned off.  Each light can be in eye space OR tethered to screen space.
     *
     * @param positions
     * @param colors
     * @param eyeSpace
     */
    public HoloLighting(float[] positions, float[] colors, boolean eyeSpace[])
    {
        super(positions, colors);
        this.eyeSpace = new boolean[eyeSpace.length];
        System.arraycopy(eyeSpace, 0, this.eyeSpace, 0, eyeSpace.length);
    }

    @Override
    protected void calcLightPositionInEyeSpace(int lightIndex, Matrix4 lightToEyeSpaceMatrix)
    {
        if (eyeSpace[lightIndex])
        {
            //If light exists in eye-space, perform standard transformation
            super.calcLightPositionInEyeSpace(lightIndex, lightToEyeSpaceMatrix);
        }
        else
        {
            //Otherwise, use the raw light position (no transform)
            System.arraycopy(getRawLightPositions(), lightIndex * 4, getLightPositionsInEyeSpace(), lightIndex * 4, 4);
        }
    }
}
