package com.pheiffware.lib.graphics.managed.light;

import com.pheiffware.lib.geometry.Vec3D;
import com.pheiffware.lib.graphics.Color4F;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.utils.GraphicsUtils;

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
    private final float[] ambientLightColor;

    //The light's positions
    private final float[] positions;

    //The light's colors
    private final float[] colors;

    //Boolean on/off values for each light.  Represented as an int for underlying openGL's benefit.
    private final int[] onStates;

    //The maximum distance the given light shines.
    private final float[] maxDistances;

    //Temporary storage for ambientLightColor * matColor.  This result is overwritten every time the calculation is made.
    private final float[] ambLightMatColor = new float[4];

    //Temporary storage for lightColor * matColor, for each light.  This result is overwritten every time the calculation is made.
    private final float[] lightDiffMatColors = new float[numLightsSupported * 4];

    //Temporary storage for lightColor * specMatColor, for each light.  This result is overwritten every time the calculation is made.
    private final float[] lightSpecMatColors = new float[numLightsSupported * 4];

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
        this.ambientLightColor = new float[4];
        this.positions = new float[numLightsSupported * 4];
        this.colors = new float[numLightsSupported * 4];
        onStates = new int[numLightsSupported];
        maxDistances = new float[numLightsSupported];
        System.arraycopy(ambientLightColor, 0, this.ambientLightColor, 0, 4);
        System.arraycopy(positions, 0, this.positions, 0, positions.length);
        System.arraycopy(colors, 0, this.colors, 0, colors.length);

        //Turn on all lights with values passed in.  All others are left off.
        for (int i = 0; i < positions.length / 4; i++)
        {
            onStates[i] = 1;
        }
    }

    /**
     * Moves the given light to the specified position
     *
     * @param lightIndex the index of the light to move
     * @param position   the light's position
     */
    public final void moveTo(int lightIndex, float[] position)
    {
        int offset = lightIndex * 4;
        this.positions[offset + 0] = position[0];
        this.positions[offset + 1] = position[1];
        this.positions[offset + 2] = position[2];
        this.positions[offset + 3] = position[3];
    }

    /**
     * Moves the given light to the specified position
     *
     * @param lightIndex the index of the light to move
     * @param position   the light's position
     */
    public final void moveTo(int lightIndex, Vec3D position)
    {
        int offset = lightIndex * 4;
        this.positions[offset + 0] = (float) position.x;
        this.positions[offset + 1] = (float) position.y;
        this.positions[offset + 2] = (float) position.z;
        this.positions[offset + 3] = 1;
    }

    /**
     * Changes the given light's color
     *
     * @param lightIndex the index of the light to set color for
     * @param color      the light's color
     */
    public final void setColor(int lightIndex, float[] color)
    {
        int offset = lightIndex * 4;
        this.colors[offset + 0] = color[0];
        this.colors[offset + 1] = color[1];
        this.colors[offset + 2] = color[2];
        this.colors[offset + 3] = color[3];
    }

    /**
     * Changes the given light's color
     *
     * @param lightIndex the index of the light to set color for
     * @param color      the light's color
     */
    public final void setColor(int lightIndex, Color4F color)
    {
        int offset = lightIndex * 4;
        this.colors[offset + 0] = color.getRed();
        this.colors[offset + 1] = color.getGreen();
        this.colors[offset + 2] = color.getBlue();
        this.colors[offset + 3] = color.getAlpha();
    }

    /**
     * Turns the given light on/off
     *
     * @param lightIndex the index of the light to turn on/off
     * @param on         is the light on?
     */
    public final void setOnState(int lightIndex, boolean on)
    {
        if (on)
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
     * @param on         0 = off, anything else = on
     */
    public final void setOnState(int lightIndex, int on)
    {
        onStates[lightIndex] = on;
    }

    /**
     * Sets the maximum distance of the given light.
     *
     * @param lightIndex  the index of the light to set distance for
     * @param maxDistance
     */
    public final void setMaximumDistance(int lightIndex, float maxDistance)
    {
        maxDistances[lightIndex] = maxDistance;
    }

    /**
     * Applies the given transform to all lights, which are on.  Returns a newly generated array containing the position of all lights.
     *
     * @param lightTransform transform to apply to lights
     * @return a newly generated array containing transformed light positions
     */
    public float[] transformLightPositions(Matrix4 lightTransform)
    {
        float[] transformedLightPositions = new float[numLightsSupported * 4];

        for (int i = 0; i < numLightsSupported; i++)
        {
            if (onStates[i] == 1)
            {
                transformLight(transformedLightPositions, i, lightTransform);
            }
        }
        return transformedLightPositions;
    }

    /**
     * Calculates and retains the value of a single light position as transformed by a lightToEyeSpaceMatrix matrix.
     *
     * @param transformedLightPositions where to store transformed light positions
     * @param lightIndex                the index of the light
     * @param lightTransform            transform to apply to lights
     */
    protected void transformLight(float[] transformedLightPositions, int lightIndex, Matrix4 lightTransform)
    {
        int offset = lightIndex * 4;
        lightTransform.transform4DFloatVector(transformedLightPositions, offset, positions, offset);
    }

    /**
     * Multiply ambientLightColor * matColor, using internal array.
     *
     * @param matColor the material color
     * @return temporary result of the multiplication.  This array will be overwritten next time it is computed, so put the value somewhere useful!
     */
    public final float[] calcAmbientMatColor(float[] matColor)
    {
        GraphicsUtils.vecMultiply(4, ambLightMatColor, ambientLightColor, matColor);
        return ambLightMatColor;
    }

    /**
     * Multiply lightColor * diffMatColor, for each light, using internal array.
     *
     * @param diffMatColor the material color
     * @return temporary result of the multiplication.  This array will be overwritten next time it is computed, so put the value somewhere useful!
     */
    public final float[] calcDiffMatColor(float[] diffMatColor)
    {
        computeAndStoreLightMatColors(lightDiffMatColors, diffMatColor);
        return lightDiffMatColors;
    }

    /**
     * Multiply lightColor * specMatColor, for each light, using internal array.
     *
     * @param specMatColor the material color
     * @return temporary result of the multiplication.  This array will be overwritten next time it is computed, so put the value somewhere useful!
     */
    public final float[] calcSpecMatColor(float[] specMatColor)
    {
        computeAndStoreLightMatColors(lightSpecMatColors, specMatColor);
        return lightSpecMatColors;
    }

    /**
     * Multiply lightColor * matColor, for each light and store in given lightMatColors array.
     *
     * @param lightMatColors where to store result of multiplication
     * @param matColor       the material color
     */
    private final void computeAndStoreLightMatColors(float[] lightMatColors, float[] matColor)
    {
        int offset = 0;
        for (int i = 0; i < numLightsSupported; i++)
        {
            if (onStates[i] == 1)
            {
                lightMatColors[offset] = colors[offset] * matColor[0];
                offset++;
                lightMatColors[offset] = colors[offset] * matColor[1];
                offset++;
                lightMatColors[offset] = colors[offset] * matColor[2];
                offset++;
                lightMatColors[offset] = colors[offset] * matColor[3];
                offset++;
            }
            else
            {
                offset += 4;
            }
        }
    }

    public final float[] getPositions()
    {
        return positions;
    }

    public final float[] getColors()
    {
        return colors;
    }

    public final int[] getOnStates()
    {
        return onStates;
    }


    public final float[] getAmbientLightColor()
    {
        return ambientLightColor;
    }
}
