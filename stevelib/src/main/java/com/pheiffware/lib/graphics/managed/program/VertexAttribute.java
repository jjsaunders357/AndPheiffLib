package com.pheiffware.lib.graphics.managed.program;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.utils.PheiffGLUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * An enum defining standard attributes for ALL programs.  This includes the naming convention for the variable in the program along with information about how the attribute will
 * be stored in a vertex buffer.  As a side benefit this also allows efficient lookup (as compared to String) and will be cleaner once EnumSet is moved to Android.
 * <p/>
 * Created by Steve on 5/13/2016.
 */
public enum VertexAttribute
{
    POSITION("vertexPosition", GLES20.GL_FLOAT, 4, 1),
    NORMAL("vertexNormal", GLES20.GL_FLOAT, 3, 1),
    TEXCOORD("vertexTexCoord", GLES20.GL_FLOAT, 2, 1),
    COLOR("vertexColor", GLES20.GL_FLOAT, 4, 1);

    private static final Map<String, VertexAttribute> nameLookup;

    static
    {
        nameLookup = new HashMap<>();
        for (VertexAttribute vertexAttribute : values())
        {
            nameLookup.put(vertexAttribute.getName(), vertexAttribute);
        }
    }

    public static VertexAttribute lookupByName(String name)
    {
        return nameLookup.get(name);
    }

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

    VertexAttribute(String name, int baseType, int dims, int arrayLength)
    {
        this.name = name;
        this.baseType = baseType;
        this.dims = dims;
        this.arrayLength = arrayLength;
        numBaseTypeElements = dims * arrayLength;
        byteSize = PheiffGLUtils.getGLBaseTypeByteSize(baseType) * numBaseTypeElements;
    }

    public final String getName()
    {
        return name;
    }

    public final int getBaseType()
    {
        return baseType;
    }

    public final int getDims()
    {
        return dims;
    }

    public final int getArrayLength()
    {
        return arrayLength;
    }

    public final int getNumBaseTypeElements()
    {
        return numBaseTypeElements;
    }

    public final int getByteSize()
    {
        return byteSize;
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
