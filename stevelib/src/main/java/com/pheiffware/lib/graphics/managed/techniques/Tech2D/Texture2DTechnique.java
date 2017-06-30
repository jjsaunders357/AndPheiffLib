package com.pheiffware.lib.graphics.managed.techniques.Tech2D;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.program.Uniform;
import com.pheiffware.lib.graphics.managed.program.UniformNames;
import com.pheiffware.lib.graphics.managed.texture.Texture;

/**
 * Draws 2D geometry with color+texture.  x values occupy the range [-1,1].  y values occupy a smaller/larger range based on aspect ratio.
 * Project matrix scales y values properly to match aspect ratio.
 * View matrix does the rest.
 * <p>
 * Required Properties:
 * <p/>
 * RenderProperty.PROJECTION_MATRIX - Matrix4
 * <p/>
 * RenderProperty.VIEW_MATRIX - Matrix4
 * <p>
 * RenderProperty.MAT_COLOR_TEXTURE - Texture
 * <p>
 * Created by Steve on 6/19/2017.
 */

public class Texture2DTechnique extends Technique
{
    private final Uniform projectionViewModelUniform;
    private final Uniform matSamplerUniform;

    public Texture2DTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/2d/vert_texture_pos4_2d.glsl", "shaders/2d/frag_texture_pos4_2d.glsl", new RenderProperty[]{RenderProperty.PROJECTION_MATRIX, RenderProperty.VIEW_MATRIX});
        projectionViewModelUniform = getUniform(UniformNames.PROJECTION_VIEW_MODEL_MATRIX_UNIFORM);
        matSamplerUniform = getUniform(UniformNames.MATERIAL_SAMPLER_UNIFORM);
    }

    @Override
    protected void applyPropertiesToUniforms()
    {
        Matrix4 projMatrix = (Matrix4) getPropertyValue(RenderProperty.PROJECTION_MATRIX);
        Matrix4 viewMatrix = (Matrix4) getPropertyValue(RenderProperty.VIEW_MATRIX);
        projectionViewModelUniform.setValue(Matrix4.multiply(projMatrix, viewMatrix).m);
        Texture texture = (Texture) getPropertyValue(RenderProperty.MAT_COLOR_TEXTURE);
        matSamplerUniform.setValue(texture.autoBind());
    }
}
