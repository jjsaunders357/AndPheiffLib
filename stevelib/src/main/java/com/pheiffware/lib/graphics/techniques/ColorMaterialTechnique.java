package com.pheiffware.lib.graphics.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.program.StdUniforms;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.program.Uniform;
import com.pheiffware.lib.graphics.utils.GraphicsUtils;

/**
 * Shades mesh with a constant surface color and one light.  Handles, ambient, diffuse and specular lighting.
 * <p/>
 * Required Properties:
 * <p/>
 * StdUniforms.PROJECTION_MATRIX - Matrix4
 * <p/>
 * StdUniforms.VIEW_MATRIX - Matrix4
 * <p/>
 * StdUniforms.MODEL_MATRIX) - Matrix4
 * <p/>
 * StdUniforms.AMBIENT_LIGHT_COLOR - float[4]
 * <p/>
 * StdUniforms.LIGHT_COLOR - float[4]
 * <p/>
 * StdUniforms.LIGHT_POS - float[4]
 * <p/>
 * StdUniforms.MAT_COLOR - float[4]
 * <p/>
 * StdUniforms.SPEC_MAT_COLOR - float[4]
 * <p/>
 * StdUniforms.SHININESS - float
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
    private final Matrix4 viewModelMatrix = Matrix4.newIdentity();
    private final Matrix3 normalTransform = Matrix3.newIdentity();
    private final float[] lightMatColor = new float[4];
    private final float[] lightEyeSpace = new float[4];

    public ColorMaterialTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/vert_mncl.glsl", "shaders/frag_mncl.glsl", new RenderProperty[]{
                RenderProperty.PROJECTION_MATRIX,
                RenderProperty.VIEW_MATRIX,
                RenderProperty.MODEL_MATRIX,
                RenderProperty.AMBIENT_LIGHT_COLOR,
                RenderProperty.MAT_COLOR,
                RenderProperty.LIGHT_COLOR,
                RenderProperty.SPEC_MAT_COLOR,
                RenderProperty.LIGHT_POS,
                RenderProperty.SHININESS
        });
        eyeProjUniform = getUniform(StdUniforms.PROJECTION_MATRIX_UNIFORM);
        eyeTransUniform = getUniform(StdUniforms.VIEW_MODEL_MATRIX_UNIFORM);
        eyeNormUniform = getUniform(StdUniforms.NORMAL_MATRIX_UNIFORM);
        ambLMUniform = getUniform(StdUniforms.AMBIENT_LIGHTMAT_COLOR_UNIFORM);
        diffLMUniform = getUniform(StdUniforms.DIFF_LIGHTMAT_COLOR_UNIFORM);
        specLMUniform = getUniform(StdUniforms.SPEC_LIGHTMAT_COLOR_UNIFORM);
        lightEyePosUniform = getUniform(StdUniforms.LIGHT_POS_EYE_UNIFORM);
        shininessUniform = getUniform(StdUniforms.SHININESS_UNIFORM);
    }


    @Override
    public void applyPropertiesToUniforms()
    {
        Matrix4 projMatrix = (Matrix4) getPropertyValue(RenderProperty.PROJECTION_MATRIX);
        eyeProjUniform.setValue(projMatrix.m);

        Matrix4 viewMatrix = (Matrix4) getPropertyValue(RenderProperty.VIEW_MATRIX);
        Matrix4 modelMatrix = (Matrix4) getPropertyValue(RenderProperty.MODEL_MATRIX);
        viewModelMatrix.set(viewMatrix);
        viewModelMatrix.multiplyBy(modelMatrix);

        eyeTransUniform.setValue(viewModelMatrix.m);
        normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
        eyeNormUniform.setValue(normalTransform.m);

        float[] lightColor = (float[]) getPropertyValue(RenderProperty.AMBIENT_LIGHT_COLOR);
        float[] matColor = (float[]) getPropertyValue(RenderProperty.MAT_COLOR);
        GraphicsUtils.vecMultiply(4, lightMatColor, lightColor, matColor);
        ambLMUniform.setValue(lightMatColor);

        lightColor = (float[]) getPropertyValue(RenderProperty.LIGHT_COLOR);
        GraphicsUtils.vecMultiply(4, lightMatColor, lightColor, matColor);
        diffLMUniform.setValue(lightMatColor);

        matColor = (float[]) getPropertyValue(RenderProperty.SPEC_MAT_COLOR);
        GraphicsUtils.vecMultiply(4, lightMatColor, lightColor, matColor);
        specLMUniform.setValue(lightMatColor);

        float[] lightPosition = (float[]) getPropertyValue(RenderProperty.LIGHT_POS);
        viewMatrix.transformFloatVector(lightEyeSpace, 0, lightPosition, 0);
        lightEyePosUniform.setValue(lightEyeSpace);

        shininessUniform.setValue(getPropertyValue(RenderProperty.SHININESS));
    }
}
