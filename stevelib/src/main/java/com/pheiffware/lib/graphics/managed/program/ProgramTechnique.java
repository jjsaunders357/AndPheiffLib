package com.pheiffware.lib.graphics.managed.program;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
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

    //Used internally to compute values to apply to uniforms
    private final Matrix4 projectionViewModelMatrix = Matrix4.newIdentity();

    public ProgramTechnique(AssetLoader al, String vertexShaderAsset, String fragmentShaderAsset, RenderProperty[] properties) throws GraphicsException
    {
        Collections.addAll(this.properties, properties);
        this.program = new Program(al, vertexShaderAsset, fragmentShaderAsset);
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
    public final void bindToVertexBuffer(VertexAttributeHandle handle)
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
