package com.pheiffware.lib.graphics.managed.program;

import android.opengl.GLES20;

import com.pheiffware.lib.ParseException;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.program.shader.ShaderBuilder;
import com.pheiffware.lib.graphics.managed.program.shader.ShaderCode;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steve on 8/7/2017.
 */

public class ConfigurableProgram implements Program
{
    //Reference to the shader handles used to build this program
    private final int[] shaderHandles;
    private final Map<String, Object> versionConfig;
    private final String[] shaderPaths;
    private BaseProgram program;

    public ConfigurableProgram(Map<String, Object> versionConfig, String[] shaderPaths)
    {
        this.versionConfig = versionConfig;
        this.shaderPaths = shaderPaths;
        shaderHandles = new int[shaderPaths.length];
    }

    public void configure(ShaderBuilder shaderBuilder, Map<String, Object> systemConfig) throws ParseException, GraphicsException, IOException
    {
        Map<String, Object> config = new HashMap<>();
        config.putAll(systemConfig);
        config.putAll(versionConfig);
        for (int i = 0; i < shaderPaths.length; i++)
        {
            ShaderCode shaderCode = shaderBuilder.build(shaderPaths[i], config);
            if (shaderHandles[i] != 0)
            {
                GLES20.glDeleteShader(shaderHandles[i]);
            }
            shaderHandles[i] = shaderCode.compile();
        }
        buildProgram();
    }

    //TODO: Move logic into BaseProgram
    private void buildProgram() throws GraphicsException
    {
        int handle = GLES20.glCreateProgram();
        for (int i = 0; i < shaderHandles.length; i++)
        {
            GLES20.glAttachShader(handle, shaderHandles[i]);
        }
        GLES20.glLinkProgram(handle);
        assertProgramStatus(handle);
        if (program != null)
        {
            program.destroy();
        }
        program = new BaseProgram(handle);
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

    @Override
    public int getAttributeLocation(VertexAttribute vertexAttribute)
    {
        return program.getAttributeLocation(vertexAttribute);
    }

    @Override
    public void setUniformValue(UniformName name, Object value)
    {
        program.setUniformValue(name, value);
    }

    @Override
    public void bind()
    {
        program.bind();
    }

    @Override
    public EnumSet<VertexAttribute> getAttributes()
    {
        return program.getAttributes();
    }
}
