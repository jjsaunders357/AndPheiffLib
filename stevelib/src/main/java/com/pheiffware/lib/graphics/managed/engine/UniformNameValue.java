package com.pheiffware.lib.graphics.managed.engine;

/**
 * Holds the name and value for a single uniform.  Used as a convenience when specifying lists of these, inline, as opposed populating a map.
 * <p/>
 * Created by Steve on 4/20/2016.
 */
public class UniformNameValue
{
    public final String name;
    public final Object value;

    public UniformNameValue(String name, Object value)
    {
        this.name = name;
        this.value = value;
    }
}
