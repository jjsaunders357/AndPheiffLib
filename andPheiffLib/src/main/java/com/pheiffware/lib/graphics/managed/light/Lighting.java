package com.pheiffware.lib.graphics.managed.light;

import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Vec4F;

/**
 * Stores information associated with all light sources.  This can be passed to various techniques which support lighting as a parameter.  Also contains machinery to do basic
 * lighting uniform calculations.
 * <p/>
 * Created by Steve on 6/1/2016.
 */
public class Lighting
{
    //TODO: Make configurable
    //The number of lights actually supported by underlying shaders.
    public static final int numLightsSupported = 4;

    //General ambient lighting
    private final Vec4F ambientLightColor = new Vec4F(1);

    //Boolean on/off values for each light.  Represented as an int for underlying openGL's benefit.
    private final int[] onStates = new int[numLightsSupported];

    //Boolean on/off values for each light specifying whether it casts a cube-map shadow
    private final int[] castsCubeShadow = new int[numLightsSupported];

    //The light's positions
    private final Vec4F positions = new Vec4F(numLightsSupported);

    //The light's colors
    private final Vec4F colors = new Vec4F(numLightsSupported);

    public Lighting()
    {
    }

    /**
     * Creates a Lighting object representing the set of lights to use for rendering.  Each light's position and color is encoded as a 4 element block in the corresponding array.
     * Any additional lights supported by the implementation will be turned off.
     *
     * @param ambientLightColor the general ambient light
     * @param positions         the positions of the lights
     * @param colors            the colors of the lights
     */
    public Lighting(float[] ambientLightColor, float[] positions, float[] colors)
    {
        this.ambientLightColor.copy(ambientLightColor);
        this.positions.copyAll(positions);
        this.colors.copyAll(colors);

        //Turn on all lights with values passed in.  All others are left off.
        for (int i = 0; i < positions.length / 4; i++)
        {
            onStates[i] = 1;
        }
    }


    /**
     * Turns the given light on/off
     *
     * @param lightIndex the index of the light to turn on/off
     * @param onOff      is the light on?
     */
    public final void setOnState(int lightIndex, boolean onOff)
    {
        if (onOff)
        {
            setOnState(lightIndex, 1);
        }
        else
        {
            setOnState(lightIndex, 0);
        }
    }

    /**
     * Turns the given light on/off
     *
     * @param lightIndex the index of the light to turn on/off
     * @param onOff      0 = off, anything else = on
     */
    public final void setOnState(int lightIndex, int onOff)
    {
        onStates[lightIndex] = onOff;
    }

    /**
     * Sets whether the given light casts omni directional shadows
     *
     * @param lightIndex
     * @param onOff
     */
    public final void setCastsCubeShadow(int lightIndex, int onOff)
    {
        castsCubeShadow[lightIndex] = onOff;
    }


    /**
     * Applies the given transform to all lights, which are on.
     *
     * @param transformedPositions transformed light positions are put here
     * @param matrix               transform to apply to lights
     */
    public void transformLightPositions(Vec4F transformedPositions, Matrix4 matrix)
    {
        positions.setIndex(0);
        transformedPositions.setIndex(0);
        for (int i = 0; i < Lighting.numLightsSupported; i++)
        {
            if (onStates[i] == 1)
            {
                transformedPositions.copy(positions);
                transformedPositions.transformBy(matrix);
            }
            positions.next();
            transformedPositions.next();
        }
    }

    public boolean isLightOn(int lightIndex)
    {
        return onStates[lightIndex] == 1;
    }

    public Vec4F getAmbientLightColor()
    {
        return ambientLightColor;
    }

    public Vec4F getPositions()
    {
        return positions;
    }

    public Vec4F getColors()
    {
        return colors;
    }

    public final int[] getOnStates()
    {
        return onStates;
    }


    public int[] getCastsCubeShadow()
    {
        return castsCubeShadow;
    }

}
