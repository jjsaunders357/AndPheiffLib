package com.pheiffware.lib.graphics.managed.program;

import android.opengl.GLES20;

import com.pheiffware.lib.ParseException;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.program.shader.ShaderBuilder;
import com.pheiffware.lib.graphics.managed.program.shader.ShaderCode;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * Wraps the concept of an OpenGL program into a convenient object.  This allows querying details about uniforms and vertexAttributes.
 * Also allows setting uniform values and binding.
 * <p/>
 * Created by Steve on 2/13/2016.
 */
//TODO: Merge into Program and make final
public class Program
{
    //Handle to the GL program
    private final int programHandle;

    //Map of all uniforms used by the program
    private final EnumMap<UniformName, Uniform> uniforms = new EnumMap<>(UniformName.class);

    //Set of all program attributes
    private final EnumSet<VertexAttribute> vertexAttributes = EnumSet.noneOf(VertexAttribute.class);

    //Map of all program attribute locations (location is essentially a GL handle to the attribute itself)
    private final EnumMap<VertexAttribute, Integer> vertexAttributeLocations = new EnumMap<>(VertexAttribute.class);

    public Program(ShaderBuilder shaderBuilder, Map<String, Object> config, String... shaderPaths) throws GraphicsException
    {
        try
        {
            int[] shaderHandles = new int[shaderPaths.length];
            for (int i = 0; i < shaderPaths.length; i++)
            {
                ShaderCode shaderCode = shaderBuilder.build(shaderPaths[i], config);
                shaderHandles[i] = shaderCode.compile();
            }
            programHandle = link(shaderHandles);
            for (int i = 0; i < shaderHandles.length; i++)
            {
                GLES20.glDeleteShader(shaderHandles[i]);
            }
            assertProgramStatus(programHandle);
            GLES20.glUseProgram(programHandle);
            extractUniforms();
            extractAttibutes();
        }
        catch (IOException e)
        {
            throw new GraphicsException(e);
        }
        catch (ParseException e)
        {
            throw new GraphicsException(e);
        }
    }

    private int link(int[] shaderHandles) throws GraphicsException
    {
        int handle = GLES20.glCreateProgram();
        for (int i = 0; i < shaderHandles.length; i++)
        {
            GLES20.glAttachShader(handle, shaderHandles[i]);
        }
        GLES20.glLinkProgram(handle);
        return handle;
    }

    private static void assertProgramStatus(int programHandle) throws GraphicsException
    {
        IntBuffer linkStatus = IntBuffer.allocate(1);
        GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus);
        if (linkStatus.get(0) == 0)
        {
            String infoLog = GLES20.glGetProgramInfoLog(programHandle);
            GLES20.glDeleteShader(programHandle);
            throw new GraphicsException("The program failed to link: " + infoLog);
        }
    }

    /**
     * Extracts all uniforms and their details from the program by querying OpenGL.
     */
    private void extractUniforms() throws GraphicsException
    {
        int[] numUniformsArray = new int[1];
        GLES20.glGetProgramiv(programHandle, GLES20.GL_ACTIVE_UNIFORMS, numUniformsArray, 0);
        int numActiveUniforms = numUniformsArray[0];
        for (int i = 0; i < numActiveUniforms; i++)
        {
            Uniform uniform = Uniform.createUniform(programHandle, i);
            UniformName name = UniformName.lookupByName(uniform.name);
            if (name == null)
            {
                throw new GraphicsException("Unregister uniform: " + uniform.name);
            }
            uniforms.put(name, uniform);
        }
    }

    /**
     * Extracts all vertext and their details from the program by querying OpenGL.
     */
    private void extractAttibutes()
    {
        int[] numAttributesArray = new int[1];
        GLES20.glGetProgramiv(programHandle, GLES20.GL_ACTIVE_ATTRIBUTES, numAttributesArray, 0);
        int numActiveAttributes = numAttributesArray[0];
        for (int i = 0; i < numActiveAttributes; i++)
        {
            extractAttribute(i);
        }
    }

    /**
     * Extracts a specific attribute by index.
     *
     * @param attributeIndex
     */
    private void extractAttribute(int attributeIndex)
    {
        int[] arraySizeArray = new int[1];
        int[] typeArray = new int[1];
        String name = GLES20.glGetActiveAttrib(programHandle, attributeIndex, arraySizeArray, 0, typeArray, 0);
        int location = GLES20.glGetAttribLocation(programHandle, name);
        VertexAttribute vertexAttribute = VertexAttribute.lookupByName(name);
        vertexAttributeLocations.put(vertexAttribute, location);
        vertexAttributes.add(vertexAttribute);
    }


    public final int getAttributeLocation(VertexAttribute vertexAttribute)
    {
        return vertexAttributeLocations.get(vertexAttribute);
    }


    public final void setUniformValue(UniformName name, Object value)
    {
        uniforms.get(name).setValue(value);
    }


    public final void bind()
    {
        GLES20.glUseProgram(programHandle);
    }


    public final EnumSet<VertexAttribute> getAttributes()
    {
        return vertexAttributes;
    }

    public void destroy()
    {
        GLES20.glDeleteProgram(programHandle);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Program handle=");
        builder.append(programHandle);
        builder.append(":\n");

        builder.append("Uniforms:\n");
        for (Uniform uniform : uniforms.values())
        {
            builder.append(uniform);
            builder.append("\n");
        }
        builder.append("VertexAttribute locations:\n");
        for (VertexAttribute vertexAttribute : vertexAttributes)
        {
            builder.append(vertexAttribute.getName());
            builder.append(": ");
            builder.append(getAttributeLocation(vertexAttribute));
            builder.append("\n");
        }
        return builder.toString();
    }
}
