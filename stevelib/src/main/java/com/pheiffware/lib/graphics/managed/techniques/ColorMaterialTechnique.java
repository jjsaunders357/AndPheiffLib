package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.UniformName;

/**
 * Shades mesh with a constant surface color and given lights' settings.  Handles, ambient, diffuse and specular lighting.
 * Created by Steve on 4/23/2016.
 */
public class ColorMaterialTechnique extends Technique3D
{
    public ColorMaterialTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/vert_mncl.glsl", "shaders/frag_mncl.glsl", new RenderProperty[]{
                RenderProperty.PROJECTION_MATRIX,
                RenderProperty.VIEW_MATRIX,
                RenderProperty.MODEL_MATRIX,
                RenderProperty.LIGHTING,
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
        applyConstantColorMaterialLight();

        Lighting lighting = (Lighting) getPropertyValue(RenderProperty.LIGHTING);

        //TODO: Figure out holo lighting
        //TODO: Make part of constant setup and remove calcOnLightPositionsInEyeSpace()
        setUniformValue(UniformName.LIGHT_POS_EYE, lighting.getLightPositionsInEyeSpace());
        setUniformValue(UniformName.ON_STATE, lighting.getOnStates());
        setUniformValue(UniformName.SHININESS, getPropertyValue(RenderProperty.SHININESS));
    }

}
