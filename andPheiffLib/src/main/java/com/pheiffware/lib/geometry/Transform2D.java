package com.pheiffware.lib.geometry;

/**
 * Represents a transform gesture indicated by a multi-touch event
 * Created by Steve on 3/18/2016.
 */
public class Transform2D
{
    public final Vec2D translation;
    public double rotation;
    public final Vec2D scale;

    public Transform2D()
    {
        this(new Vec2D(0, 0), 0, new Vec2D(1, 1));
    }

    public Transform2D(Vec2D translation, double rotation, Vec2D scale)
    {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    /**
     * @param transform2D
     */
    public void apply(Transform2D transform2D)
    {
        this.translation.addTo(transform2D.translation);
        this.rotation += transform2D.rotation;
        this.scale.scaleBy(transform2D.scale);
    }

    public String toString()
    {
        return "T: " + translation.toString() + "  R: " + 180 * rotation / Math.PI + "  S:" + scale;
    }
}
