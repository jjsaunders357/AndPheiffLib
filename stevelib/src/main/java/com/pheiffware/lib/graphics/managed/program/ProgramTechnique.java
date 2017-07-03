package com.pheiffware.lib.graphics.managed.program;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.vertexBuffer.VertexAttributeHandle;

import java.util.Collections;

/**
 * A technique which wraps a single OpenGL program.  All RenderProperties map to that program's uniform settings in some way.
 * Created by Steve on 4/17/2016.
 */
public abstract class ProgramTechnique extends BaseTechnique
{
    //Program being wrapped
    private final Program program;

    public ProgramTechnique(AssetLoader al, String vertexShaderAsset, String fragmentShaderAsset, RenderProperty[] properties) throws GraphicsException
    {
        Collections.addAll(this.properties, properties);
        this.program = new Program(al, vertexShaderAsset, fragmentShaderAsset);
    }

    protected final void setUniformValue(UniformName name, Object value)
    {
        program.setUniformValue(name, value);
    }

    //TODO: Make abstract
    @Override
    public void applyConstantProperties()
    {

    }

    public abstract void applyInstanceProperties();

    @Override
    public void bind()
    {
        program.bind();
    }

    @Override
    public final void bindToVertexBuffer(VertexAttributeHandle handle)
    {
        handle.bindToProgram(program);
    }
}
