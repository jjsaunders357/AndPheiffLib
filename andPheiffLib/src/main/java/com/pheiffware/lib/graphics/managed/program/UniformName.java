package com.pheiffware.lib.graphics.managed.program;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steve on 7/2/2017.
 */

public enum UniformName
{
    PROJECTION_VIEW_MODEL_MATRIX("projectionViewModelMatrix"),
    PROJECTION_MATRIX("projectionMatrix"),
    PROJECTION_SCALE_X("projectionScaleX"),
    PROJECTION_SCALE_Y("projectionScaleY"),
    PROJECTION_MAX_DEPTH("projectionMaxDepth"),
    VIEW_MODEL_MATRIX("viewModelMatrix"),
    VIEW_MATRIX("viewMatrix"),
    MODEL_MATRIX("modelMatrix"),
    NORMAL_MATRIX("normalMatrix"),
    AMBIENT_LIGHT_COLOR("ambientLightColor"),
    LIGHT_COLOR("lightColor"),
    AMBIENT_LIGHTMAT_COLOR("ambientLightMaterialColor"),
    DIFF_LIGHTMAT_COLOR("diffuseLightMaterialColor"),
    SPEC_LIGHTMAT_COLOR("specLightMaterialColor"),
    MAT_ALPHA("materialAlpha"),
    LIGHT_POS_EYE("lightPositionEyeSpace"),
    LIGHT_POS("lightPosition"),
    ON_STATE("onState"),
    SHININESS("shininess"),
    EYE_POSITION("eyePosition"),
    ZNEAR("zNear"),
    ZFAR("zFar"),
    ASPECT_RATIO("aspectRatio"),
    SCREEN_COLOR("screenColor"),
    MATERIAL_SAMPLER("materialColorSampler"),
    DEPTH_SAMPLER("depthSampler"),
    DEPTH_CUBE_SAMPLER("cubeDepthSampler"),
    SPHERE_VIEW_MODEL_MATRIX("sphereViewModelMatrix"),
    SPHERE_NORMAL_MATRIX("sphereNormalMatrix");
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
