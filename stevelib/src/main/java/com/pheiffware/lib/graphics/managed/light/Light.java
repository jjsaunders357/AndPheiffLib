package com.pheiffware.lib.graphics.managed.light;

import com.pheiffware.lib.geometry.Vec3D;
import com.pheiffware.lib.graphics.Color4F;

/**
 * Stores information associated with a single light.  This can be passed to various techniques which support lighting as a parameter.
 * <p/>
 * Created by Steve on 5/26/2016.
 */
public class Light
{
    //The light's position
    private final float[] position = new float[4];
    //The light's color
    private final float[] color = new float[4];

    public Light(float[] position, float[] color)
    {
        moveTo(position);
        setColor(color);
    }

    public final void moveTo(float[] position)
    {
        this.position[0] = position[0];
        this.position[1] = position[1];
        this.position[2] = position[2];
        this.position[3] = position[3];
    }

    public final void moveTo(Vec3D position)
    {
        this.position[0] = (float) position.x;
        this.position[1] = (float) position.y;
        this.position[2] = (float) position.z;
        this.position[3] = 1;
    }

    public final void setColor(float[] color)
    {
        this.color[0] = color[0];
        this.color[1] = color[1];
        this.color[2] = color[2];
        this.color[3] = color[3];
    }

    public final void setColor(Color4F color)
    {
        this.color[0] = color.getRed();
        this.color[1] = color.getGreen();
        this.color[2] = color.getBlue();
        this.color[3] = color.getAlpha();
    }

    public final float[] getPosition()
    {
        return position;
    }

    public final float[] getColor()
    {
        return color;
    }
}
