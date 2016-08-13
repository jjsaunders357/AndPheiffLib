package com.pheiffware.lib.graphics.managed.program;

/**
 * Naming conventions used in shaders for uniforms.
 * <p/>
 * Created by Steve on 4/15/2016.
 */
public class UniformNames
{
    public static final String PROJECTION_VIEW_MODEL_MATRIX_UNIFORM = "projectionViewModelMatrix";
    public static final String PROJECTION_MATRIX_UNIFORM = "projectionMatrix";
    public static final String VIEW_MODEL_MATRIX_UNIFORM = "viewModelMatrix";
    public static final String NORMAL_MATRIX_UNIFORM = "normalMatrix";
    public static final String MODEL_MATRIX_UNIFORM = "modelMatrix";

    public static final String AMBIENT_LIGHT_COLOR_UNIFORM = "ambientLightColor";

    public static final String LIGHT_COLOR_UNIFORM = "lightColor";

    public static final String AMBIENT_LIGHTMAT_COLOR_UNIFORM = "ambientLightMaterialColor";
    public static final String DIFF_LIGHTMAT_COLOR_UNIFORM = "diffuseLightMaterialColor";
    public static final String SPEC_LIGHTMAT_COLOR_UNIFORM = "specLightMaterialColor";

    public static final String LIGHT_POS_EYE_UNIFORM = "lightPositionEyeSpace";
    public static final String LIGHT_POS_UNIFORM = "lightPosition";
    public static final String ON_STATE_UNIFORM = "onState";
    public static final String MATERIAL_SAMPLER_UNIFORM = "materialColorSampler";
    public static final String SHININESS_UNIFORM = "shininess";

    public static final String EYE_POSITION_UNIFORM = "eyePosition";
    public static final String ZNEAR_UNIFORM = "zNear";
    public static final String ZFAR_UNIFORM = "zFar";
    public static final String ASPECT_RATIO_UNIFORM = "aspectRatio";
    public static final String SCREEN_COLOR_UNIFORM = "screenColor";
}
