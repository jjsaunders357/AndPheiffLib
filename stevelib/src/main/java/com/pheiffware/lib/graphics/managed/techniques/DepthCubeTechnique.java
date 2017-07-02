package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.program.UniformName;

/**
 * Renders the depth of geometry and nothing else.
 * <p>
 * Created by Steve on 6/21/2017.
 */

public class DepthCubeTechnique extends Technique
{
    private final Matrix4 viewModelMatrix = Matrix4.newIdentity();
    private final Matrix4 projectionViewModelMatrix = Matrix4.newIdentity();

    public DepthCubeTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/vert_depth_distance_squared.glsl", "shaders/frag_depth_distance_squared.glsl", new RenderProperty[]{
                RenderProperty.PROJECTION_MATRIX,
                RenderProperty.VIEW_MATRIX,
                RenderProperty.MODEL_MATRIX,
                RenderProperty.MAXIMUM_LIGHT_DISTANCE
        });
    }

    @Override
    protected void applyPropertiesToUniforms()
    {
        float maximumLightDistance = (float) getPropertyValue(RenderProperty.MAXIMUM_LIGHT_DISTANCE);
        Matrix4 projectionMatrix = (Matrix4) getPropertyValue(RenderProperty.PROJECTION_MATRIX);
        Matrix4 viewMatrix = (Matrix4) getPropertyValue(RenderProperty.VIEW_MATRIX);
        Matrix4 modelMatrix = (Matrix4) getPropertyValue(RenderProperty.MODEL_MATRIX);

        projectionViewModelMatrix.set(projectionMatrix);
        viewModelMatrix.set(viewMatrix);
        viewModelMatrix.multiplyBy(modelMatrix);
        projectionViewModelMatrix.multiplyBy(viewModelMatrix);

        setUniformValue(UniformName.PROJECTION_VIEW_MODEL_MATRIX, projectionViewModelMatrix.m);
        setUniformValue(UniformName.VIEW_MODEL_MATRIX, viewModelMatrix.m);
        setUniformValue(UniformName.MAXIMUM_LIGHT_DISTANCE_SQUARED, maximumLightDistance * maximumLightDistance);
    }
}
