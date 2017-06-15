package com.pheiffware.lib.graphics.managed.program;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.utils.PheiffGLUtils;

import java.nio.ByteBuffer;
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
    //Custom put method added to make this not quite as horribly inefficient.
    POSITION("vertexPosition", GLES20.GL_FLOAT, 4, 1)
            {
                @Override
                public void put(ByteBuffer byteBuffer, int offset, float[] data)
                {
                    // @formatter:off
                    byteBuffer.putFloat(data[offset]);offset++;byteBuffer.putFloat(data[offset]);offset++;
                    byteBuffer.putFloat(data[offset]);offset++;byteBuffer.putFloat(data[offset]);offset++;
                    // @formatter:on
                }
            },
    NORMAL("vertexNormal", GLES20.GL_FLOAT, 3, 1)
            {
                @Override
                public void put(ByteBuffer byteBuffer, int offset, float[] data)
                {
                    // @formatter:off
                    byteBuffer.putFloat(data[offset]);offset++;byteBuffer.putFloat(data[offset]);offset++;
                    byteBuffer.putFloat(data[offset]);offset++;
                    // @formatter:on
                }
            },

    TEXCOORD("vertexTexCoord", GLES20.GL_FLOAT, 2, 1)
            {
                @Override
                public void put(ByteBuffer byteBuffer, int offset, float[] data)
                {
                    // @formatter:off
                    byteBuffer.putFloat(data[offset]);offset++;byteBuffer.putFloat(data[offset]);offset++;
                    byteBuffer.putFloat(data[offset]);offset++;byteBuffer.putFloat(data[offset]);offset++;
                    // @formatter:on
                }
            },
    COLOR("vertexColor", GLES20.GL_FLOAT, 4, 1)
            {
                @Override
                public void put(ByteBuffer byteBuffer, int offset, float[] data)
                {
                    // @formatter:off
                    byteBuffer.putFloat(data[offset]);offset++;byteBuffer.putFloat(data[offset]);offset++;
                    // @formatter:on
                }
            };

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

    /**
     * Transfer one attribute worth of data from the given attribute data array to the given byteBuffer.
     *
     * @param byteBuffer buffer to transfer into
     * @param offset     offset into array where attribute data is located
     * @param data       attribute data array
     */
    public void put(ByteBuffer byteBuffer, int offset, float[] data)
    {
        for (int i = 0; i < numBaseTypeElements; i++)
        {
            byteBuffer.putFloat(data[i]);
        }
    }

    /**
     * Transfers the contents of an entire array of attribute data into the given vertex buffer
     *
     * @param byteBuffer   the buffer to transfer into
     * @param vertexStride attribute data will likely be interlaced with other attribute data and must be spaced apart in buffer following the given vertexStride
     * @param data         attribute data array
     */
    public void putDataInBuffer(ByteBuffer byteBuffer, int vertexStride, float[] data)
    {
        int position = byteBuffer.position();
        for (int i = 0; i < data.length; i += byteSize)
        {
            put(byteBuffer, i, data);
            position += vertexStride;
            byteBuffer.position(position);
        }
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
