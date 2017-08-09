package com.pheiffware.lib.graphics.managed.program;

import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.program.shader.ShaderBuilder;
import com.pheiffware.lib.graphics.managed.vertexBuffer.VertexAttributeHandle;

import java.util.Collections;
import java.util.Map;

/**
 * A technique which wraps a single OpenGL program.  All RenderProperties map to that program's uniform settings in some way.
 * Created by Steve on 4/17/2016.
 */
public abstract class ProgramTechnique extends BaseTechnique
{
    //Paths to shaders comprising the program
    private final String[] shaderPaths;

    //Program being wrapped
    private Program program;

    //Used internally to compute values to apply to uniforms
    private final Matrix4 projectionViewModelMatrix = Matrix4.newIdentity();

    public ProgramTechnique(ShaderBuilder shaderBuilder, Map<String, Object> localConfig, RenderProperty[] properties, String... shaderPaths) throws GraphicsException
    {
        super(shaderBuilder, localConfig);
        this.shaderPaths = shaderPaths;
        Collections.addAll(this.properties, properties);
    }

    @Override
    protected void onConfigChanged(ShaderBuilder shaderBuilder, Map<String, Object> config) throws GraphicsException
    {
        if (program != null)
        {
            program.destroy();
        }
        program = new Program(shaderBuilder, config, shaderPaths);
    }

    protected final void setUniformValue(UniformName name, Object value)
    {
        program.setUniformValue(name, value);
    }

    @Override
    public final void applyConstantProperties()
    {
        program.bind();
        applyConstantPropertiesImplement();
    }

    protected abstract void applyConstantPropertiesImplement();

    public abstract void applyInstanceProperties();

    @Override
    public void bind()
    {
        program.bind();
    }

    @Override
    public final void attachAndBindBuffer(VertexAttributeHandle handle)
    {
        handle.bindToProgram(program);
    }

    protected final void setProjectionViewModel()
    {
        Matrix4 projectionMatrix = (Matrix4) getPropertyValue(RenderProperty.PROJECTION_MATRIX);
        Matrix4 viewMatrix = (Matrix4) getPropertyValue(RenderProperty.VIEW_MATRIX);
        Matrix4 modelMatrix = (Matrix4) getPropertyValue(RenderProperty.MODEL_MATRIX);
        projectionViewModelMatrix.set(projectionMatrix);
        projectionViewModelMatrix.multiplyBy(viewMatrix);
        projectionViewModelMatrix.multiplyBy(modelMatrix);
        setUniformValue(UniformName.PROJECTION_VIEW_MODEL_MATRIX, projectionViewModelMatrix.m);
    }
}
