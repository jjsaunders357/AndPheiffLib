package com.pheiffware.lib.graphics.managed.vertexBuffer;

import com.pheiffware.lib.graphics.managed.program.VertexAttribute;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * Describes the collective group of vertex attributes used for one type of mesh.  For example: POSITION4, NORMAL, TEXTCOORD.
 * This calculates various things such as vertex size, offsets of each attribute, etc.
 * Created by Steve on 6/14/2017.
 */

public class VertexAttributeGroup
{

    //The vertexAttributes being managed by a buffer.  EnumSet maintains attribute order based on natural Enum ordering
    private final EnumSet<VertexAttribute> vertexAttributes;

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
            attributeByteOffset = nextMachineBoundary(attributeByteOffset);
        }
        vertexByteSize = attributeByteOffset;
    }

    private int nextMachineBoundary(int attributeByteOffset)
    {
        return attributeByteOffset;
//    In tests this didn't appear to have any affect on performance.  Might matter if geometry vs. fill limited.
//    private static final int evenWordBoundary = 4;
//        return MathUtils.calcNextEvenBoundary(attributeByteOffset, evenWordBoundary);
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
