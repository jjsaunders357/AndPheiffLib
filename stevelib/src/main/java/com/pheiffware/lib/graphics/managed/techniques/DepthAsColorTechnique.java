package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.program.Uniform;
import com.pheiffware.lib.graphics.managed.program.UniformNames;

/**
 * Renders depth as color into a texture.
 * <p>
 * Required Properties:
 * <p/>
 * RenderProperty.PROJECTION_MATRIX - Matrix4
 * <p/>
 * RenderProperty.VIEW_MATRIX - Matrix4
 * <p/>
 * RenderProperty.MODEL_MATRIX - Matrix4
 * <p>
 * Created by Steve on 6/21/2017.
 */

public class DepthAsColorTechnique extends Technique
{
    private final Uniform projectionViewModelUniform;
    private final Matrix4 projectionViewModelMatrix = Matrix4.newIdentity();

    public DepthAsColorTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/vert_depth_as_color.glsl", "shaders/frag_depth_as_color.glsl", new RenderProperty[]{
                RenderProperty.PROJECTION_MATRIX,
                RenderProperty.VIEW_MATRIX,
                RenderProperty.MODEL_MATRIX
        });

        projectionViewModelUniform = getUniform(UniformNames.PROJECTION_VIEW_MODEL_MATRIX_UNIFORM);
    }

    @Override
    protected void applyPropertiesToUniforms()
    {
        Matrix4 projectionMatrix = (Matrix4) getPropertyValue(RenderProperty.PROJECTION_MATRIX);
        Matrix4 viewMatrix = (Matrix4) getPropertyValue(RenderProperty.VIEW_MATRIX);
        Matrix4 modelMatrix = (Matrix4) getPropertyValue(RenderProperty.MODEL_MATRIX);
        projectionViewModelMatrix.set(projectionMatrix);
        projectionViewModelMatrix.multiplyBy(viewMatrix);
        projectionViewModelMatrix.multiplyBy(modelMatrix);
        projectionViewModelUniform.setValue(projectionViewModelMatrix.m);
    }
}