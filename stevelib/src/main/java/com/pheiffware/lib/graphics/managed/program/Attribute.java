package com.pheiffware.lib.graphics.managed.program;

import com.pheiffware.lib.graphics.utils.PheiffGLUtils;

/**
 * Holds all data related to a single attribute which will be stored in a vertex buffer.
 * <p/>
 * Created by Steve on 5/13/2016.
 */
public class Attribute
{
    //Name of the attribute (as declared)
    public final String name;
    //The base type which is being stored "client-side".  For vec4, the would either be GL_FLOAT or GL_HALF, depending on how this was being stored.
    public final int baseType;
    //The number of elements in this type.  For vec4 this would be 4.  For a single float attribute, this would be 1.
    public final int dims;
    //The number of array elements in this attribute.  For vec4, this would be 1.  For vec4[10], this would be 10.
    public final int arrayLength;
    //The number of baseType elements.  For vec4, this would be 4.  For vec4[10], this would be 40.
    public final int numBaseTypeElements;
    //The total size in bytes of this attribute.  For vec4, this would be 8 if using GL_HALF or 16 if using GL_FLOAT.  If this is an array, the size will be multiplied by the array dimension.
    public final int byteSize;

    public Attribute(String name, int baseType, int dims, int arrayLength)
    {
        this.name = name;
        this.baseType = baseType;
        this.dims = dims;
        this.arrayLength = arrayLength;
        numBaseTypeElements = dims * arrayLength;
        byteSize = PheiffGLUtils.getGLBaseTypeByteSize(baseType) * numBaseTypeElements;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(", numBaseTypeElements=");
        builder.append(numBaseTypeElements);
        builder.append(", baseType=");
        builder.append(baseType);
        builder.append(", size=");
        builder.append(byteSize);
        return builder.toString();
    }
}
