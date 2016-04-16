package com.pheiffware.lib.graphics;

/**
 * Naming conventions used in shaders for standard attributes/uniforms.
 * <p/>
 * Created by Steve on 4/15/2016.
 */
public class ShadConst
{
    //TODO: Replace existing strings
    public static final String VERTEX_POSITION_ATTRIBUTE = "vertexPosition";
    public static final String VERTEX_NORMAL_ATTRIBUTE = "vertexNormal";
    public static final String VERTEX_TEXCOORD_ATTRIBUTE = "vertexTexCoord";

    public static final String EYE_PROJECTION_MATRIX_UNIFORM = "eyeProjectionMatrix";
    public static final String EYE_TRANSFORM_MATRIX_UNIFORM = "eyeTransformMatrix";
    public static final String EYE_NORMAL_MATRIX_UNIFORM = "eyeNormalMatrix";

    public static final String AMBIENT_LIGHTMAT_COLOR_UNIFORM = "ambientLightMaterialColor";
    public static final String DIFF_LIGHTMAT_COLOR_UNIFORM = "diffuseLightMaterialColor";
    public static final String SPEC_LIGHTMAT_COLOR_UNIFORM = "specLightMaterialColor";
    public static final String AMBIENT_LIGHT_COLOR_UNIFORM = "ambientLightColor";
    public static final String DIFF_LIGHT_COLOR_UNIFORM = "diffuseLightColor";

    public static final String LIGHT_POS_EYE_UNIFORM = "lightPositionEyeSpace";
    public static final String SHININESS_UNIFORM = "shininess";
    public static final String DIFFUSE_MATERIAL_TEXTURE_UNIFORM = "diffuseMaterialTexture";
}
