package com.pheiffware.lib.graphics.managed.light;

import com.pheiffware.lib.graphics.Matrix4;

/**
 * A version of the Lighting class which is customized for holographic lighting.  This allows lights to either track with the eye or with the screen.
 * <p/>
 * Created by Steve on 7/25/2016.
 */
public class HoloLighting extends Lighting
{
    //Does this light exist in eye space (fixed in the viewer's frame of reference) or in screen space (fixed in the screen's frame of reference).
    private final boolean[] eyeSpace;

    /**
     * Creates a Lighting object representing the set of lights to use for rendering.  Each light's position and color is encoded as a 4 element block in the corresponding array.
     * Any additional lights supported by the implementation will be turned off.  Each light can be in eye space OR tethered to screen space.
     *
     * @param ambientLightColor the general ambient light
     * @param positions         the positions of the lights
     * @param colors            the colors of the lights
     * @param eyeSpace          is the light fixed in eye space or part of the screen space itself
     */
    public HoloLighting(float[] ambientLightColor, float[] positions, float[] colors, boolean eyeSpace[])
    {
        super(ambientLightColor, positions, colors);
        this.eyeSpace = new boolean[eyeSpace.length];
        System.arraycopy(eyeSpace, 0, this.eyeSpace, 0, eyeSpace.length);
    }

    /**
     * Don't transform lights which are attached to eye space.
     *
     * @param lightIndex            the index of the light
     * @param lightToEyeSpaceMatrix the transform from light to eye space
     */
    @Override
    protected void transformLightPositionToEyeSpace(int lightIndex, Matrix4 lightToEyeSpaceMatrix)
    {
        if (eyeSpace[lightIndex])
        {
            //If light exists in eye-space, perform standard transformation
            super.transformLightPositionToEyeSpace(lightIndex, lightToEyeSpaceMatrix);
        }
        else
        {
            //Otherwise, use the raw light position (no transform)
            System.arraycopy(getPositions(), lightIndex * 4, getLightPositionsInEyeSpace(), lightIndex * 4, 4);
        }
    }
}
