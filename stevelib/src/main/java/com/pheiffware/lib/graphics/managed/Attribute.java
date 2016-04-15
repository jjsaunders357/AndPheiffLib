package com.pheiffware.lib.graphics.managed;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.utils.PheiffGLUtils;

/**
 * Holds stats for one attribute of a program Created by Steve on 2/13/2016.
 */
public class Attribute
{

    //Name of the attribute (as declared)
    public final String name;

    //The GL location, used in vertex buffers.  This is not the same as index, which is a meaningless enumeration assigned to each attribute.
    public final int location;

    //The base semantic of the attribute.  If the attribute is a vec4, this will be a GL_FLOAT.
    public final int baseType;

    //The number of baseType elements.  If the attribute is a vec4, this would be 4.  If the attribute were an array of vec4s this would be 4*arraySize.
    public final int numBaseTypeElements;

    //The total size in bytes of this attribute, given its semantic and array size.
    public final int byteSize;

    public Attribute(int programHandle, int attributeIndex)
    {
        int[] arraySizeArray = new int[1];
        int[] typeArray = new int[1];
        name = GLES20.glGetActiveAttrib(programHandle, attributeIndex, arraySizeArray, 0, typeArray, 0);

        //Type may be something like GL_FLOAT_VEC4
        int attributeType = typeArray[0];

        //This will be 1 unless this is an actual array declaration
        int attributeArraySize = arraySizeArray[0];

        baseType = PheiffGLUtils.getGLBaseType(attributeType);
        numBaseTypeElements = attributeArraySize * PheiffGLUtils.getGLTypeDims(attributeType);
        byteSize = attributeArraySize * PheiffGLUtils.getGLTypeByteSize(attributeType);
        location = GLES20.glGetAttribLocation(programHandle, name);
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
        builder.append(", location=");
        builder.append(location);
        builder.append("\n");
        return builder.toString();
    }
}
