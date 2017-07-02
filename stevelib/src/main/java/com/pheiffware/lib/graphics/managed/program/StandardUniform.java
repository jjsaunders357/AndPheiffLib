package com.pheiffware.lib.graphics.managed.program;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steve on 7/2/2017.
 */

public enum StandardUniform
{
    temp("test");
    private static final Map<String, StandardUniform> nameLookup;

    static
    {
        nameLookup = new HashMap<>();
        for (StandardUniform uniform : values())
        {
            nameLookup.put(uniform.getName(), uniform);
        }
    }

    //Name of the attribute (as declared)
    public final String name;

    StandardUniform(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
