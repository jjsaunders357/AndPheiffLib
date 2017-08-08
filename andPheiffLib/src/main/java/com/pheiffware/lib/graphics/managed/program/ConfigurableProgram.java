package com.pheiffware.lib.graphics.managed.program;

import com.pheiffware.lib.ParseException;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.program.shader.ShaderBuilder;
import com.pheiffware.lib.graphics.managed.program.shader.ShaderCode;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * A program which can be reconfigured on the fly.  When created a "version config" is provided.
 * This configuration is used to create a distinct version of the program (such as numLights).
 * When configured as systemConfig is provided.  This is combined with the version config (version overrides system)
 * to generate a new program.  The existing underlying program is deleted/replaced.
 * Created by Steve on 8/7/2017.
 */

public class ConfigurableProgram implements Program
{
    //Reference to the shader handles used to build this program

    private final Map<String, Object> versionConfig;
    private final String[] shaderPaths;
    private BaseProgram program;

    public ConfigurableProgram(Map<String, Object> versionConfig, String[] shaderPaths)
    {
        this.versionConfig = versionConfig;
        this.shaderPaths = shaderPaths;
    }

    public void configure(ShaderBuilder shaderBuilder, Map<String, Object> systemConfig) throws ParseException, GraphicsException, IOException
    {
        int[] shaderHandles = new int[shaderPaths.length];
        Map<String, Object> config = new HashMap<>();
        config.putAll(systemConfig);
        config.putAll(versionConfig);
        for (int i = 0; i < shaderPaths.length; i++)
        {
            ShaderCode shaderCode = shaderBuilder.build(shaderPaths[i], config);
            shaderCode.printCode();
            shaderHandles[i] = shaderCode.compile();
        }
        if (program != null)
        {
            program.destroy();
        }
        program = new BaseProgram(shaderHandles);
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
