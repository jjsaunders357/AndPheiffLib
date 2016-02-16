package com.pheiffware.lib.graphics.managed.mesh;

import java.util.Map;

/**
 * Created by Steve on 2/14/2016.
 */
public class Mesh
{
    public final Map<String, float[]> data;
    public final short[] primitiveIndices;

    public Mesh(short[] primitiveIndices, Map<String, float[]> data)
    {
        this.primitiveIndices = primitiveIndices;
        this.data = data;
    }
}
