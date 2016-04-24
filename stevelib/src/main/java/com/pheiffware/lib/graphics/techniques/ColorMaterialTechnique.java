package com.pheiffware.lib.graphics.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.ShadConst;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.program.Uniform;
import com.pheiffware.lib.graphics.utils.GraphicsUtils;

/**
 * Shades mesh with a constant surface color and one light.  Handles, ambient, diffuse and specular lighting.
 * <p/>
 * Required Properties:
 * <p/>
 * ShadConst.EYE_PROJECTION_MATRIX_PROPERTY
 * <p/>
 * ShadConst.EYE_VIEW_MODEL_MATRIX_PROPERTY | (ShadConst.EYE_VIEW_MATRIX_PROPERTY & ShadConst.EYE_MODEL_MATRIX_PROPERTY)
 * <p/>
 * ShadConst.AMBIENT_LIGHT_COLOR_PROPERTY
 * <p/>
 * ShadConst.AMBIENT_MAT_COLOR_PROPERTY
 * <p/>
 * ShadConst.DIFF_LIGHT_COLOR_PROPERTY
 * <p/>
 * ShadConst.DIFF_MAT_COLOR_PROPERTY
 * <p/>
 * ShadConst.SPEC_LIGHT_COLOR_PROPERTY
 * <p/>
 * ShadConst.SPEC_MAT_COLOR_PROPERTY
 * <p/>
 * ShadConst.SHININESS_PROPERTY
 * <p/>
 * ShadConst.LIGHT_POS_PROPERTY
 * <p/>
 * Created by Steve on 4/23/2016.
 */
public class ColorMaterialTechnique extends Technique
{
    private final Uniform shininessUniform;
    private final Uniform eyeProjUniform;
    private final Uniform eyeTransUniform;
    private final Uniform eyeNormUniform;
    private final Uniform ambLMUniform;
    private final Uniform diffLMUniform;
    private final Uniform specLMUniform;
    private final Uniform lightEyePosUniform;

    //Used internally to compute values to apply to uniforms
    private Matrix4 viewModelMatrixProduct = Matrix4.newIdentity();
    private Matrix3 normalTransform = Matrix3.newIdentity();
    private float[] lightMatColor = new float[4];
    private float[] lightEyeSpace = new float[4];

    public ColorMaterialTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/vert_mncl.glsl", "shaders/frag_mncl.glsl");
        eyeProjUniform = getUniform(ShadConst.EYE_PROJECTION_MATRIX_UNIFORM);
        eyeTransUniform = getUniform(ShadConst.EYE_TRANSFORM_MATRIX_UNIFORM);
        eyeNormUniform = getUniform(ShadConst.EYE_NORMAL_MATRIX_UNIFORM);
        ambLMUniform = getUniform(ShadConst.AMBIENT_LIGHTMAT_COLOR_UNIFORM);
        diffLMUniform = getUniform(ShadConst.DIFF_LIGHTMAT_COLOR_UNIFORM);
        specLMUniform = getUniform(ShadConst.SPEC_LIGHTMAT_COLOR_UNIFORM);
        lightEyePosUniform = getUniform(ShadConst.LIGHT_POS_EYE_UNIFORM);
        shininessUniform = getUniform(ShadConst.SHININESS_UNIFORM);
    }


    @Override
    public void applyProperties()
    {
        eyeProjUniform.setValue(getPropertyValue(ShadConst.EYE_PROJECTION_MATRIX_PROPERTY));

        Matrix4 viewModelMatrix = (Matrix4) getPropertyValueSinceApply(ShadConst.EYE_VIEW_MODEL_MATRIX_PROPERTY);
        if (viewModelMatrix == null)
        {
            Matrix4 viewMatrix = (Matrix4) getPropertyValue(ShadConst.VIEW_MATRIX_PROPERTY);
            Matrix4 modelMatrix = (Matrix4) getPropertyValue(ShadConst.MODEL_MATRIX_PROPERTY);
            viewModelMatrixProduct.set(viewMatrix);
            viewModelMatrixProduct.multiplyBy(modelMatrix);
            viewModelMatrix = viewModelMatrixProduct;
        }

        eyeTransUniform.setValue(viewModelMatrix);
        normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
        eyeNormUniform.setValue(normalTransform);

        float[] lightColor = (float[]) getPropertyValue(ShadConst.AMBIENT_LIGHT_COLOR_PROPERTY);
        float[] matColor = (float[]) getPropertyValue(ShadConst.AMBIENT_MAT_COLOR_PROPERTY);
        GraphicsUtils.vecMultiply(4, lightMatColor, lightColor, matColor);
        ambLMUniform.setValue(lightMatColor);

        lightColor = (float[]) getPropertyValue(ShadConst.DIFF_LIGHT_COLOR_PROPERTY);
        matColor = (float[]) getPropertyValue(ShadConst.DIFF_MAT_COLOR_PROPERTY);
        GraphicsUtils.vecMultiply(4, lightMatColor, lightColor, matColor);
        diffLMUniform.setValue(lightMatColor);

        lightColor = (float[]) getPropertyValue(ShadConst.SPEC_LIGHT_COLOR_PROPERTY);
        matColor = (float[]) getPropertyValue(ShadConst.SPEC_MAT_COLOR_PROPERTY);
        GraphicsUtils.vecMultiply(4, lightMatColor, lightColor, matColor);
        specLMUniform.setValue(lightMatColor);

        float[] lightPosition = (float[]) getPropertyValue(ShadConst.LIGHT_POS_PROPERTY);
        lightEyeSpace = viewModelMatrix.transformFloatVector(lightPosition);
        lightEyePosUniform.setValue(lightEyeSpace);

        shininessUniform.setValue(getPropertyValue(ShadConst.SHININESS_PROPERTY));
    }

}
