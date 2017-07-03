package com.pheiffware.lib.graphics.managed.techniques.Tech2D;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.program.ProgramTechnique;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.UniformName;
import com.pheiffware.lib.graphics.managed.texture.Texture;

/**
 * Draws 2D geometry with color+texture.  x values occupy the range [-1,1].  y values occupy a smaller/larger range based on aspect ratio.
 * Project matrix scales y values properly to match aspect ratio.
 * View matrix does the rest.
 * <p>
 * Created by Steve on 6/19/2017.
 */

public class ColorTexture2DTechnique extends ProgramTechnique
{
    private final Matrix4 projectionViewModelMatrix = Matrix4.newIdentity();

    public ColorTexture2DTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/2d/vert_2d_color_texture_pos4.glsl", "shaders/2d/frag_2d_color_texture_pos4.glsl", new RenderProperty[]{RenderProperty.PROJECTION_MATRIX, RenderProperty.VIEW_MATRIX});
    }

    @Override
    public void applyInstanceProperties()
    {
        projectionViewModelMatrix.set((Matrix4) getPropertyValue(RenderProperty.PROJECTION_MATRIX));
        projectionViewModelMatrix.multiplyBy((Matrix4) getPropertyValue(RenderProperty.VIEW_MATRIX));
        Texture texture = (Texture) getPropertyValue(RenderProperty.MAT_COLOR_TEXTURE);
        setUniformValue(UniformName.PROJECTION_VIEW_MODEL_MATRIX, projectionViewModelMatrix.m);
        setUniformValue(UniformName.MATERIAL_SAMPLER, texture.autoBind());
    }
}
