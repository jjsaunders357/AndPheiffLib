package com.pheiffware.lib.graphics.managed.program;

import android.opengl.GLES20;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.utils.ProgramUtils;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Wraps the concept of an opengl program into a convenient object.
 * <p/>
 * Created by Steve on 2/13/2016.
 */
public class Program
{
    //Handle to the GL program
    private final int handle;

    //Map of all uniforms used by the program
    private final Map<String, Uniform> uniforms = new HashMap<>();

    //Set of all program attributes
    private final EnumSet<VertexAttribute> vertexAttributes = EnumSet.noneOf(VertexAttribute.class);

    //Map of all program attribute locations (location is essentially a GL handle to the attribute itself)
    private final EnumMap<VertexAttribute, Integer> vertexAttributeLocations = new EnumMap<>(VertexAttribute.class);

    public Program(AssetLoader al, String vertexShaderAsset, String fragmentShaderAsset) throws GraphicsException
    {
        this(ProgramUtils.loadProgram(al, vertexShaderAsset, fragmentShaderAsset));
    }

    private Program(int handle)
    {
        this.handle = handle;

        int[] numUniformsArray = new int[1];
        GLES20.glGetProgramiv(handle, GLES20.GL_ACTIVE_UNIFORMS, numUniformsArray, 0);
        int numActiveUniforms = numUniformsArray[0];
        for (int i = 0; i < numActiveUniforms; i++)
        {
            Uniform uniform = Uniform.createUniform(handle, i);
            uniforms.put(uniform.name, uniform);
        }

        int[] numAttributesArray = new int[1];
        GLES20.glGetProgramiv(handle, GLES20.GL_ACTIVE_ATTRIBUTES, numAttributesArray, 0);
        int numActiveAttributes = numAttributesArray[0];
        for (int i = 0; i < numActiveAttributes; i++)
        {
            registerAttributeLocation(i);
        }
    }

    private void registerAttributeLocation(int attributeIndex)
    {
        int[] arraySizeArray = new int[1];
        int[] typeArray = new int[1];
        String name = GLES20.glGetActiveAttrib(handle, attributeIndex, arraySizeArray, 0, typeArray, 0);
        int location = GLES20.glGetAttribLocation(handle, name);
        VertexAttribute vertexAttribute = VertexAttribute.lookupByName(name);
        vertexAttributeLocations.put(vertexAttribute, location);
        vertexAttributes.add(vertexAttribute);
    }

    public final int getAttributeLocation(VertexAttribute vertexAttribute)
    {
        return vertexAttributeLocations.get(vertexAttribute);
    }

    public final Uniform getUniform(String uniformName)
    {
        return uniforms.get(uniformName);
    }

    public final void setUniformValues(String[] uniformNames, Object[] uniformValues)
    {
        for (int i = 0; i < uniformNames.length; i++)
        {
            setUniformValue(uniformNames[i], uniformValues[i]);
        }
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

    public final Collection<String> getUniformNames()
    {
        return uniforms.keySet();
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
        builder.append("VertexAttribute locations:\n");
        for (VertexAttribute vertexAttribute : vertexAttributes)
        {
            builder.append(vertexAttribute.getName() + ": " + getAttributeLocation(vertexAttribute) + "\n");
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


    public final void setUniformSampler(String uniformName, int samplerIndex)
    {
        GLES20.glUniform1i(getUniform(uniformName).location, samplerIndex);
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

    public EnumSet<VertexAttribute> getAttributes()
    {
        return vertexAttributes;
    }
}
