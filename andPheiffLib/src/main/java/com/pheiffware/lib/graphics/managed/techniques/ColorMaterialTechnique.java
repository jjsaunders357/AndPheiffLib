package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.ParseException;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.UniformName;

import java.io.IOException;
import java.util.HashMap;

/**
 * Shades mesh with a constant surface color and given lights' settings.  Handles, ambient, diffuse and specular lighting.
 * Created by Steve on 4/23/2016.
 */
public class ColorMaterialTechnique extends Technique3D
{

    public ColorMaterialTechnique(GLCache glCache) throws GraphicsException, IOException, ParseException
    {
        super(glCache, new HashMap<String, Object>(), new RenderProperty[]{
                RenderProperty.PROJECTION_LINEAR_DEPTH,
                RenderProperty.VIEW_MATRIX,
                RenderProperty.MODEL_MATRIX,
                RenderProperty.LIGHTING,
                RenderProperty.MAT_COLOR,
                RenderProperty.SPEC_MAT_COLOR,
                RenderProperty.SHININESS
        }, "vert_mncl.glsl", "frag_mncl.glsl");
    }

    public void applyConstantPropertiesImplement()
    {
        setProjectionLinearDepth();
        setLightingConstants();
    }

    @Override
    public void applyInstanceProperties()
    {
        setViewModelNormal();
        setLightingColors();
        setUniformValue(UniformName.SHININESS, getPropertyValue(RenderProperty.SHININESS));
    }

}
