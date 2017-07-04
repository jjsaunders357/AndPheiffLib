package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.UniformName;
import com.pheiffware.lib.graphics.managed.texture.Texture;

/**
 * Shades mesh with a textured color and with given lights' settings.  Handles, ambient, diffuse and specular lighting.
 * Created by Steve on 4/23/2016.
 */
public class TextureMaterialTechnique extends Technique3D
{
    //Used internally to compute values to apply to uniforms
    private final Matrix4 viewModelMatrix = Matrix4.newIdentity();
    private final Matrix3 normalTransform = Matrix3.newIdentity();

    public TextureMaterialTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/vert_mntl.glsl", "shaders/frag_mntl.glsl", new RenderProperty[]{
                RenderProperty.PROJECTION_MATRIX,
                RenderProperty.VIEW_MATRIX,
                RenderProperty.MODEL_MATRIX,
                RenderProperty.LIGHTING,
                RenderProperty.MAT_COLOR_TEXTURE,
                RenderProperty.SPEC_MAT_COLOR,
                RenderProperty.SHININESS
        });
    }

    @Override
    public void applyInstanceProperties()
    {
        setProjection();
        setViewModelNormal();
        setModulatedLightSpecMaterial();

        Lighting lighting = (Lighting) getPropertyValue(RenderProperty.LIGHTING);
        Texture texture = (Texture) getPropertyValue(RenderProperty.MAT_COLOR_TEXTURE);

        setUniformValue(UniformName.AMBIENT_LIGHT_COLOR, lighting.getAmbientLightColor());
        setUniformValue(UniformName.LIGHT_COLOR, lighting.getColors());
        setUniformValue(UniformName.LIGHT_POS_EYE, lighting.getLightPositionsInEyeSpace());
        setUniformValue(UniformName.ON_STATE, lighting.getOnStates());
        setUniformValue(UniformName.MATERIAL_SAMPLER, texture.autoBind());
        setUniformValue(UniformName.SHININESS, getPropertyValue(RenderProperty.SHININESS));
    }

}
