package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.UniformName;
import com.pheiffware.lib.graphics.managed.program.shader.ShaderBuilder;
import com.pheiffware.lib.graphics.managed.texture.Texture;

import java.util.Map;

/**
 * Shades mesh with a constant surface color and given lights' settings.  Handles, ambient, diffuse and specular lighting.
 * <p>
 * Omni-directional shadows - diffuse/specular light, is blocked based on given cube depth map.
 * Created by Steve on 4/23/2016.
 */
public class ColorShadowMaterialTechnique extends Technique3D
{
    public ColorShadowMaterialTechnique(ShaderBuilder shaderBuilder, Map<String, Object> localConfig) throws GraphicsException
    {
        super(shaderBuilder, localConfig, new RenderProperty[]{
                RenderProperty.PROJECTION_LINEAR_DEPTH,
                RenderProperty.VIEW_MATRIX,
                RenderProperty.MODEL_MATRIX,
                RenderProperty.LIGHTING,
                RenderProperty.CUBE_DEPTH_TEXTURE,
                RenderProperty.MAT_COLOR,
                RenderProperty.SPEC_MAT_COLOR,
                RenderProperty.SHININESS
        }, "vert_mncl_cube_shadow.glsl", "frag_mncl_cube_shadow.glsl");
    }

    public void applyConstantPropertiesImplement()
    {
        setProjectionLinearDepth();
        setLightingConstants();
    }

    @Override
    public void applyInstanceProperties()
    {
        Matrix4 modelMatrix = (Matrix4) getPropertyValue(RenderProperty.MODEL_MATRIX);
        setUniformValue(UniformName.MODEL_MATRIX, modelMatrix.m);
        setViewModelNormal();
        setLightingColors();

        setUniformValue(UniformName.SHININESS, getPropertyValue(RenderProperty.SHININESS));
        Texture cubeDepthTexture = (Texture) getPropertyValue(RenderProperty.CUBE_DEPTH_TEXTURE);
        setUniformValue(UniformName.DEPTH_CUBE_SAMPLER, cubeDepthTexture.autoBind());
    }
}
