package com.pheiffware.lib.graphics.managed.light;

import com.pheiffware.lib.geometry.Vec3D;
import com.pheiffware.lib.graphics.Color4F;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.utils.GraphicsUtils;

/**
 * Stores information associated with all light sources.  This can be passed to various techniques which support lighting as a parameter.  Also contains machinery to do basic
 * lighting uniform calculations.
 * <p/>
 * Created by Steve on 6/1/2016.
 */
public class Lighting
{
    //The number of lights actually supported by underlying shaders.
    private static final int numLightsSupported = 4;

    //The light's positions
    private final float[] positions;

    //The light's colors
    private final float[] colors;

    //Boolean on/off values for each light.  Represented as an int for underlying openGL's benefit.
    private final int[] onStates;

    //Calculation buffer used to hold result of multiplying material colors by light colors
    private final float[] lightMatColors;

    //Calculation buffer used to hold result of transforming light positions to eye-space
    private final float[] lightPositionsInEyeSpace;

    /**
     * Creates a Lighting object representing the set of lights to use for rendering.  Each light's position and color is encoded as a 4 element block in the corresponding array.
     * Any additional lights supported by the implementation will be turned off.
     *
     * @param positions
     * @param colors
     * @return
     */
    public Lighting(float[] positions, float[] colors)
    {
        this.positions = new float[numLightsSupported * 4];
        this.colors = new float[numLightsSupported * 4];
        this.lightMatColors = new float[numLightsSupported * 4];
        this.lightPositionsInEyeSpace = new float[numLightsSupported * 4];
        onStates = new int[numLightsSupported];
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
     * @param lightIndex
     * @param position
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
     * @param lightIndex
     * @param position
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
     * @param lightIndex
     * @param color
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
     * @param lightIndex
     * @param color
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
     * @param lightIndex
     * @param on         0 = off, anything else = on
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
     * @param lightIndex
     * @param on
     */
    public final void setOnState(int lightIndex, int on)
    {
        onStates[lightIndex] = on;
    }

    /**
     * Calculates and retains the value of light positions as transformed by a view matrix.  This is generally done once per frame and the result is obtained many times via the
     * corresponding getter.
     *
     * @param viewMatrix
     */
    public void calcLightPositionsInEyeSpace(Matrix4 viewMatrix)
    {
        int offset = 0;
        for (int i = 0; i < numLightsSupported; i++)
        {
            if (onStates[i] == 1)
            {
                viewMatrix.transform4DFloatVector(lightPositionsInEyeSpace, offset, positions, offset);
            }
            offset += 4;
        }
    }

    /**
     * Obtains the result of previous call to calcLightPositionsInEyeSpace
     *
     * @return
     */
    public float[] getLightPositionsInEyeSpace()
    {
        return lightPositionsInEyeSpace;
    }

    /**
     * Calculates and returns the array containing the multiplication of the given material color (4-element array) with each light individually.  Note, for efficiency, the
     * returned value is reused every time and its value will be changed every time this is called.
     *
     * @param materialColor
     * @return the array containing the result.  This array is reused for all future calculations.
     */
    public float[] calcLightMatColors(float[] materialColor)
    {
        int offset = 0;
        for (int i = 0; i < numLightsSupported; i++)
        {
            if (onStates[i] == 1)
            {
                GraphicsUtils.vecMultiply(4, offset, lightMatColors, offset, colors, 0, materialColor);
            }
            offset += 4;
        }
        return lightMatColors;
    }

    public final float[] getPositions()
    {
        return positions;
    }

    public final float[] getColors()
    {
        return colors;
    }

    public int[] getOnStates()
    {
        return onStates;
    }
}
