package com.pheiffware.lib.graphics.managed;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.FatalGraphicsException;
import com.pheiffware.lib.graphics.utils.ProgramUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * Wraps the concept of an opengl program into a convenient object.
 * Created by Steve on 2/13/2016.
 */
public class Program
{
    private final int handle;
    private final Map<String, Uniform> uniforms;
    private final Map<String, Attribute> attributes;

    public Program(int vertexShaderHandle, int fragmentShaderHandle) throws FatalGraphicsException
    {
        this(ProgramUtils.createProgram(vertexShaderHandle, fragmentShaderHandle));
    }

    public Program(int programHandle)
    {
        this.handle = programHandle;

        int[] numUniformsArray = new int[1];
        GLES20.glGetProgramiv(programHandle, GLES20.GL_ACTIVE_UNIFORMS, numUniformsArray, 0);

        int numActiveUniforms = numUniformsArray[0];
        uniforms = new TreeMap<>();

        for (int i = 0; i < numActiveUniforms; i++) {
            Uniform uniform = new Uniform(programHandle, i);
            uniforms.put(uniform.name, uniform);
        }

        int[] numAttributesArray = new int[1];
        GLES20.glGetProgramiv(programHandle, GLES20.GL_ACTIVE_ATTRIBUTES, numAttributesArray, 0);
        int numActiveAttributes = numAttributesArray[0];
        attributes = new TreeMap<>();
        for (int i = 0; i < numActiveAttributes; i++) {
            Attribute attribute = new Attribute(programHandle, i);
            attributes.put(attribute.name, attribute);
        }
    }

    public Attribute getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Program handle=" + handle + ":\n");
        builder.append("Uniforms:\n");
        for (Uniform uniform : uniforms.values()) {
            builder.append(uniform + "\n");
        }
        builder.append("Attributes:\n");
        for (Attribute attribute : attributes.values()) {
            builder.append(attribute + "\n");
        }
        return builder.toString();
    }


    public int getHandle() {
        return handle;
    }
}
