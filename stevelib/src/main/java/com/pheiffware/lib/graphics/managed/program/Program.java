package com.pheiffware.lib.graphics.managed.program;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.Texture;
import com.pheiffware.lib.graphics.utils.ProgramUtils;
import com.pheiffware.lib.graphics.utils.TextureUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Wraps the concept of an opengl program into a convenient object.
 * Created by Steve on 2/13/2016.
 */
public class Program
{
    private final int handle;
    private final Map<String, Uniform> uniforms;
    private final Map<String, Attribute> attributes;

    public Program(int vertexShaderHandle, int fragmentShaderHandle) throws GraphicsException
    {
        this(ProgramUtils.createProgram(vertexShaderHandle, fragmentShaderHandle));
    }

    public Program(int programHandle)
    {
        this.handle = programHandle;

        int[] numUniformsArray = new int[1];
        GLES20.glGetProgramiv(programHandle, GLES20.GL_ACTIVE_UNIFORMS, numUniformsArray, 0);
        int numActiveUniforms = numUniformsArray[0];
        uniforms = new HashMap<>();

        for (int i = 0; i < numActiveUniforms; i++)
        {
            Uniform uniform = Uniform.createUniform(programHandle, i);
            uniforms.put(uniform.name, uniform);
        }

        int[] numAttributesArray = new int[1];
        GLES20.glGetProgramiv(programHandle, GLES20.GL_ACTIVE_ATTRIBUTES, numAttributesArray, 0);
        int numActiveAttributes = numAttributesArray[0];
        attributes = new HashMap<>();
        for (int i = 0; i < numActiveAttributes; i++)
        {
            Attribute attribute = new Attribute(programHandle, i);
            attributes.put(attribute.name, attribute);
        }
    }

    public final Uniform getUniform(String uniformName)
    {
        return uniforms.get(uniformName);
    }

    public final void setUniformValue(String uniformName, Object value)
    {
        getUniform(uniformName).setValue(value);
    }

    public final void setUniformValueIfExists(String uniformName, Object uniformValue)
    {
        Uniform uniform = getUniform(uniformName);
        if (uniform != null)
        {
            uniform.setValue(uniformValue);
        }
    }

    public final Attribute getAttribute(String attributeName)
    {
        return attributes.get(attributeName);
    }

    public final Collection<String> getUniformNames()
    {
        return uniforms.keySet();
    }

    public final Collection<String> getAttributeNames()
    {
        return attributes.keySet();
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Program handle=" + handle + ":\n");
        builder.append("Uniforms:\n");
        for (Uniform uniform : uniforms.values())
        {
            builder.append(uniform + "\n");
        }
        builder.append("Attributes:\n");
        for (Attribute attribute : attributes.values())
        {
            builder.append(attribute + "\n");
        }
        return builder.toString();
    }

    public final void bind()
    {
        GLES20.glUseProgram(handle);
    }

    public final int getHandle()
    {
        return handle;
    }


    public final void setUniformMatrix4(String uniformName, float[] matrix)
    {
        setUniformMatrix4(uniformName, matrix, false);
    }

    public final void setUniformMatrix4(String uniformName, float[] matrix, boolean transpose)
    {
        GLES20.glUniformMatrix4fv(getUniform(uniformName).location, 1, transpose, matrix, 0);
    }

    public final void setUniformMatrix3(String uniformName, float[] matrix, boolean transpose)
    {
        GLES20.glUniformMatrix3fv(getUniform(uniformName).location, 1, transpose, matrix, 0);
    }

    public final void setUniformTexture2D(String uniformName, Texture texture, int textureUnitIndex)
    {
        TextureUtils.uniformTexture2D(handle, uniformName, texture.getHandle(), textureUnitIndex);
    }

    public final void setUniformVec3(String uniformName, float[] floats)
    {
        GLES20.glUniform3fv(getUniform(uniformName).location, 1, floats, 0);
    }

    public final void setUniformVec4(String uniformName, float[] floats)
    {
        GLES20.glUniform4fv(getUniform(uniformName).location, 1, floats, 0);
    }

    public final void setUniformFloat(String uniformName, float value)
    {
        GLES20.glUniform1f(getUniform(uniformName).location, value);
    }

}
