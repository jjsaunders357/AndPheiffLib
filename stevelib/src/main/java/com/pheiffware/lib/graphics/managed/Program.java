package com.pheiffware.lib.graphics.managed;

import android.opengl.GLES20;

import java.util.Map;
import java.util.TreeMap;

/**
 * Wraps the concept of an opengl program into a convenient object.
 * Created by Steve on 2/13/2016.
 */
public class Program {
    private final int handle;
    private final Map<String, Uniform> uniforms;
    private final Map<String, Attribute> attributes;

    public Program(int handle) {
        this.handle = handle;

        int[] numUniformsArray = new int[1];
        GLES20.glGetProgramiv(handle, GLES20.GL_ACTIVE_UNIFORMS, numUniformsArray, 0);

        int numActiveUniforms = numUniformsArray[0];
        uniforms = new TreeMap<>();

        for (int i = 0; i < numActiveUniforms; i++) {
            Uniform uniform = new Uniform(handle, i);
            uniforms.put(uniform.name, uniform);
        }

        int[] numAttributesArray = new int[1];
        GLES20.glGetProgramiv(handle, GLES20.GL_ACTIVE_ATTRIBUTES, numAttributesArray, 0);
        int numActiveAttributes = numAttributesArray[0];
        attributes = new TreeMap<>();
        for (int i = 0; i < numActiveAttributes; i++) {
            Attribute attribute = new Attribute(handle, i);
            attributes.put(attribute.name, attribute);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Program " + handle + ":\n");
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
}
