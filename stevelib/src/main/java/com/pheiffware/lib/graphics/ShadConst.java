package com.pheiffware.lib.graphics;

/**
 * Naming conventions used in shaders for standard attributes/uniforms.
 * <p/>
 * Created by Steve on 4/15/2016.
 */
public class ShadConst
{
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
    public static final String DIFF_MATERIAL_TEXTURE_UNIFORM = "diffuseMaterialTexture";

    //TODO: To avoid confusion, put properties in separate constants file
    public static final String EYE_PROJECTION_MATRIX_PROPERTY = "eyep_p";
    public static final String EYE_VIEW_MODEL_MATRIX_PROPERTY = "eyevm_p";
    public static final String EYE_VIEW_MATRIX_PROPERTY = "eyev_p";
    public static final String EYE_MODEL_MATRIX_PROPERTY = "eyem_p";
    public static final String LIGHT_POS_PROPERTY = "lipos_p";
    public static final String AMBIENT_LIGHT_COLOR_PROPERTY = "amblc_p";
    public static final String LIGHT_COLOR_PROPERTY = "diflc_p";

    public static final String AMBIENT_MAT_COLOR_PROPERTY = "ambmc_p";
    public static final String DIFF_MAT_COLOR_PROPERTY = "difmc_p";
    public static final String SPEC_MAT_COLOR_PROPERTY = "spcmc_p";
    public static final String SHININESS_PROPERTY = "shin_p";
}
