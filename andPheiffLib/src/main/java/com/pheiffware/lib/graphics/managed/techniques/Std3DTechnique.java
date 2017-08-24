package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.GraphicsConfig;
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
public class Std3DTechnique extends Technique3D
{
    private boolean shadows;
    private boolean textured;

    public Std3DTechnique(ShaderBuilder shaderBuilder, Map<String, Object> localConfig) throws GraphicsException
    {
        super(shaderBuilder, localConfig, "vert_3d.glsl", "frag_3d.glsl");
    }

    public void applyConstantPropertiesImplement()
    {
        setProjectionLinearDepth();
        setLightingConstants();
        if (shadows)
        {
            Lighting lighting = (Lighting) getPropertyValue(RenderProperty.LIGHTING);
            setUniformValue(UniformName.LIGHT_POS, lighting.getPositions());
            setUniformValue(UniformName.SHADOW_PROJECTION_MAX_DEPTH, getPropertyValue(RenderProperty.SHADOW_PROJECTION_MAX_DEPTH));
            //setUniformValue(UniformName.DEPTH_Z_CONST, getPropertyValue(RenderProperty.DEPTH_Z_CONST));
            //setUniformValue(UniformName.DEPTH_Z_FACTOR, getPropertyValue(RenderProperty.DEPTH_Z_FACTOR));

        }
    }

    @Override
    public void applyInstanceProperties()
    {
        setViewModelNormal();

        setUniformValue(UniformName.SHININESS, getPropertyValue(RenderProperty.SHININESS));

        if (textured)
        {
            setSpecLightingColor();
            Lighting lighting = (Lighting) getPropertyValue(RenderProperty.LIGHTING);
            Texture texture = (Texture) getPropertyValue(RenderProperty.MAT_COLOR_TEXTURE);

            setUniformValue(UniformName.AMBIENT_LIGHT_COLOR, lighting.getAmbientLightColor());
            setUniformValue(UniformName.LIGHT_COLOR, lighting.getColors());
            setUniformValue(UniformName.DIFFUSE_MATERIAL_SAMPLER, texture.autoBind());
        }
        else
        {
            setLightingColors();
        }
        if (shadows)
        {
            Texture cubeDepthTexture = (Texture) getPropertyValue(RenderProperty.CUBE_DEPTH_TEXTURE);
            setUniformValue(UniformName.DEPTH_CUBE_SAMPLER, cubeDepthTexture.autoBind());
            Matrix4 modelMatrix = (Matrix4) getPropertyValue(RenderProperty.MODEL_MATRIX);
            setUniformValue(UniformName.MODEL_MATRIX, modelMatrix.m);
        }
    }

    @Override
    protected void onConfigChanged(ShaderBuilder shaderBuilder, Map<String, Object> config) throws GraphicsException
    {
        super.onConfigChanged(shaderBuilder, config);
        shadows = (Boolean) config.get(GraphicsConfig.ENABLE_SHADOWS);
        textured = (Boolean) config.get(GraphicsConfig.TEXTURED_MATERIAL);
    }
}
