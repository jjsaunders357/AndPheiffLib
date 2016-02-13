package com.pheiffware.lib.graphics.managed;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.utils.PheiffGLUtils;

/**
 * Holds stats for one uniform of a program
 * Created by Steve on 2/13/2016.
 */
public class Uniform {
    public final String name;
    public final int location;
    public final int dims;
    public final int baseType;

    public Uniform(int programHandle, int uniformIndex) {
        int[] arraySizeArray = new int[1];
        int[] typeArray = new int[1];
        name = GLES20.glGetActiveAttrib(programHandle, uniformIndex, arraySizeArray, 0, typeArray, 0);

        //Type may be something like GL_FLOAT_VEC4
        int attributeType = typeArray[0];

        //This will be 1 unless this is an actual array declaration
        int attributeArraySize = arraySizeArray[0];

        baseType = PheiffGLUtils.getGLBaseType(attributeType);
        dims = attributeArraySize * PheiffGLUtils.getGLTypeDims(attributeType);
        location = GLES20.glGetAttribLocation(programHandle, name);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(", dims=");
        builder.append(dims);
        builder.append(", type=");
        builder.append(baseType);
        builder.append(", location=");
        builder.append(location);
        builder.append("\n");
        return builder.toString();
    }
}
