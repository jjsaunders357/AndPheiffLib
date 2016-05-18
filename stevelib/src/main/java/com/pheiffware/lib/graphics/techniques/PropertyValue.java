package com.pheiffware.lib.graphics.techniques;

/**
 * Holds the name and value for a single uniform.  Used as a convenience when specifying lists of these, inline, as opposed populating a map.
 * <p/>
 * Created by Steve on 4/20/2016.
 */
public class PropertyValue
{
    public final TechniqueProperty property;
    public final Object value;

    public PropertyValue(TechniqueProperty property, Object value)
    {
        this.property = property;
        this.value = value;
    }
}
