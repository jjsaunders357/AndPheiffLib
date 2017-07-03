package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.program.ProgramTechnique;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.UniformName;

/**
 * Renders the depth of geometry and nothing else.
 * Created by Steve on 6/21/2017.
 */

public class DepthSpotTechnique extends ProgramTechnique
{
    private final Matrix4 projectionViewModelMatrix = Matrix4.newIdentity();

    public DepthSpotTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/vert_depth.glsl", "shaders/frag_depth.glsl", new RenderProperty[]{
                RenderProperty.PROJECTION_MATRIX,
                RenderProperty.VIEW_MATRIX,
                RenderProperty.MODEL_MATRIX
        });
    }

    @Override
    public void applyInstanceProperties()
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
