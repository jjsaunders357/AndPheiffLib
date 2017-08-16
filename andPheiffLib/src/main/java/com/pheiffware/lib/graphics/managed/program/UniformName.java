package com.pheiffware.lib.graphics.managed.program;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steve on 7/2/2017.
 */

public enum UniformName
{
    //2D
    //Texture used as an image for 2D rendering
    IMAGE_TEXTURE("imageTexture"),

    PROJECTION_VIEW_MODEL_MATRIX("projectionViewModelMatrix"),
    PROJECTION_MATRIX("projectionMatrix"),
    PROJECTION_SCALE_X("projectionScaleX"),
    PROJECTION_SCALE_Y("projectionScaleY"),
    PROJECTION_MAX_DEPTH("projectionMaxDepth"),
    VIEW_MODEL_MATRIX("viewModelMatrix"),
    VIEW_MATRIX("viewMatrix"),
    MODEL_MATRIX("modelMatrix"),
    NORMAL_MATRIX("normalMatrix"),

    //General Lighting:
    ON_STATE("onState"),
    LIGHT_POS_EYE("lightPositionEyeSpace"),
    SPEC_LIGHTMAT_COLOR("specLightMaterialColor"),

    //Textured material lighting:
    LIGHT_COLOR("lightColor"),
    AMBIENT_LIGHT_COLOR("ambientLightColor"),
    DIFFUSE_MATERIAL_SAMPLER("diffuseMaterialColorSampler"),

    //Solid color material lighting:
    DIFF_LIGHTMAT_COLOR("diffuseLightMaterialColor"),
    AMBIENT_LIGHTMAT_COLOR("ambientLightMaterialColor"),

    //For shadow casting:
    LIGHT_POS("lightPositionAbs"),
    DEPTH_CUBE_SAMPLER("cubeDepthSampler"),


    MAT_ALPHA("materialAlpha"),
    SHININESS("shininess"),
    EYE_POSITION("eyePosition"),
    ZNEAR("zNear"),
    ZFAR("zFar"),
    ASPECT_RATIO("aspectRatio"),
    SCREEN_COLOR("screenColor"),
    DEPTH_SAMPLER("depthSampler");
    private static final Map<String, UniformName> nameLookup;

    static
    {
        nameLookup = new HashMap<>();
        for (UniformName uniform : values())
        {
            nameLookup.put(uniform.getName(), uniform);
        }
    }

    public static UniformName lookupByName(String name)
    {
        return nameLookup.get(name);
    }

    //Name of the attribute (as declared)
    public final String name;

    UniformName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
