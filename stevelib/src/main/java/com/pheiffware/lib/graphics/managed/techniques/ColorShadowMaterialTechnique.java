package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.UniformName;
import com.pheiffware.lib.graphics.managed.texture.Texture;

/**
 * Shades mesh with a constant surface color and given lights' settings.  Handles, ambient, diffuse and specular lighting.
 * <p>
 * Omni-directional shadows - diffuse/specular light, is blocked based on given cube depth map.
 * Created by Steve on 4/23/2016.
 */
public class ColorShadowMaterialTechnique extends Technique3D
{
    public ColorShadowMaterialTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/vert_mncl_cube_shadow.glsl", "shaders/frag_mncl_cube_shadow.glsl", new RenderProperty[]{
                RenderProperty.PROJECTION_MATRIX,
                RenderProperty.VIEW_MATRIX,
                RenderProperty.MODEL_MATRIX,
                RenderProperty.LIGHTING,
                RenderProperty.CUBE_DEPTH_TEXTURE,
                RenderProperty.MAT_COLOR,
                RenderProperty.SPEC_MAT_COLOR,
                RenderProperty.SHININESS
        });
    }

    @Override
    public void applyInstanceProperties()
    {
        setProjection();
        setViewModelNormal();
        Matrix4 modelMatrix = (Matrix4) getPropertyValue(RenderProperty.MODEL_MATRIX);
        setUniformValue(UniformName.MODEL_MATRIX, modelMatrix.m);
        applyConstantColorMaterialLight();

        float maximumLightDistance = (float) getPropertyValue(RenderProperty.MAXIMUM_LIGHT_DISTANCE);

        Lighting lighting = (Lighting) getPropertyValue(RenderProperty.LIGHTING);

        setUniformValue(UniformName.MODEL_MATRIX, modelMatrix.m);
        setUniformValue(UniformName.LIGHT_POS_EYE, lighting.getLightPositionsInEyeSpace());
        setUniformValue(UniformName.ON_STATE, lighting.getOnStates());
        setUniformValue(UniformName.SHININESS, getPropertyValue(RenderProperty.SHININESS));

        Texture cubeDepthTexture = (Texture) getPropertyValue(RenderProperty.CUBE_DEPTH_TEXTURE);
        setUniformValue(UniformName.DEPTH_CUBE_SAMPLER, cubeDepthTexture.autoBind());

        setUniformValue(UniformName.MAXIMUM_LIGHT_DISTANCE_SQUARED, maximumLightDistance * maximumLightDistance);
    }
}
