package com.pheiffware.lib.graphics.managed.program;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * Describes the collective group of vertex attributes used for one type of mesh.  For example: POSITION4, NORMAL, TEXTCOORD.
 * This calculates various things such as vertex size, offsets of each attribute, etc.
 * Created by Steve on 6/14/2017.
 */

public class VertexAttributeGroup
{
    //The vertexAttributes being managed by this buffer.  EnumSet maintains attribute order based on natural Enum ordering
    private final EnumSet<VertexAttribute> vertexAttributes;

    //TODO: Must be an even multiple of machine word size.  Check OpenGL ES spec.
    //Total size of each vertex in this buffer
    private int vertexByteSize;

    //Maps standard vertexAttributes to their corresponding byte offsets within each vertex data block
    private EnumMap<VertexAttribute, Integer> attributeVertexByteOffset = new EnumMap<>(VertexAttribute.class);

    public VertexAttributeGroup(EnumSet<VertexAttribute> vertexAttributes)
    {
        this.vertexAttributes = vertexAttributes;
        int attributeByteOffset = 0;
        for (VertexAttribute vertexAttribute : vertexAttributes)
        {
            setAttributeByteOffset(vertexAttribute, attributeByteOffset);
            attributeByteOffset += vertexAttribute.getByteSize();
        }
        vertexByteSize = roundVertexSizeToMachineWord(attributeByteOffset);
    }

    /**
     * Given a vertex size, round to the nearest machine word size.
     *
     * @param vertexByteSize
     * @return
     */
    private int roundVertexSizeToMachineWord(int vertexByteSize)
    {
        return vertexByteSize;
    }

    private void setAttributeByteOffset(VertexAttribute vertexAttribute, int byteOffset)
    {
        attributeVertexByteOffset.put(vertexAttribute, byteOffset);
    }

    public final int getAttributeByteOffset(VertexAttribute vertexAttribute)
    {
        return attributeVertexByteOffset.get(vertexAttribute);
    }

    public final int getVertexByteSize()
    {
        return vertexByteSize;
    }

    public final EnumSet<VertexAttribute> getAttributes()
    {
        return vertexAttributes;
    }

    public final boolean contains(VertexAttribute vertexAttribute)
    {
        return vertexAttributes.contains(vertexAttribute);
    }
}
