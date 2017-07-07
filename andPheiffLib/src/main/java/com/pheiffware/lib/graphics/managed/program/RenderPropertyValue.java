package com.pheiffware.lib.graphics.managed.program;

/**
 * Holds the name and value for a single uniform.  Used as a convenience when specifying lists of these, inline, as opposed populating a map.
 * <p/>
 * Created by Steve on 4/20/2016.
 */
public class RenderPropertyValue
{
    public final RenderProperty property;
    public final Object value;

    public RenderPropertyValue(RenderProperty property, Object value)
    {
        this.property = property;
        this.value = value;
    }
}
