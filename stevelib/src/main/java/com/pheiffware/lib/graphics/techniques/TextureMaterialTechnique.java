package com.pheiffware.lib.graphics.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.Texture;
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
 * StdUniforms.MAT_COLOR_SAMPLER - float[4]
 * <p/>
 * StdUniforms.SPEC_MAT_COLOR - float[4]
 * <p/>
 * StdUniforms.SHININESS - float
 * <p/>
 * Created by Steve on 4/23/2016.
 */
public class TextureMaterialTechnique extends Technique
{
    private final Uniform shininessUniform;
    private final Uniform projectionUniform;
    private final Uniform viewModelUniform;
    private final Uniform normalUniform;
    private final Uniform ambientLightColorUniform;
    private final Uniform lightColorUniform;
    private final Uniform specLightMatUniform;
    private final Uniform lightEyePosUniform;
    private final Uniform matSamplerUniform;

    //Used internally to compute values to apply to uniforms
    private final Matrix4 viewModelMatrix = Matrix4.newIdentity();
    private final Matrix3 normalTransform = Matrix3.newIdentity();
    private final float[] lightMatColor = new float[4];
    private final float[] lightEyeSpace = new float[4];

    public TextureMaterialTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/vert_mntl.glsl", "shaders/frag_mntl.glsl", new RenderProperty[]{
                RenderProperty.PROJECTION_MATRIX,
                RenderProperty.VIEW_MATRIX,
                RenderProperty.MODEL_MATRIX,
                RenderProperty.AMBIENT_LIGHT_COLOR,
                RenderProperty.MAT_COLOR,
                RenderProperty.LIGHT_COLOR,
                RenderProperty.SPEC_MAT_COLOR,
                RenderProperty.LIGHT_POS,
                RenderProperty.SHININESS,
                RenderProperty.MAT_COLOR_TEXTURE
        });
        projectionUniform = getUniform(StdUniforms.PROJECTION_MATRIX_UNIFORM);
        viewModelUniform = getUniform(StdUniforms.VIEW_MODEL_MATRIX_UNIFORM);
        normalUniform = getUniform(StdUniforms.NORMAL_MATRIX_UNIFORM);
        ambientLightColorUniform = getUniform(StdUniforms.AMBIENT_LIGHT_COLOR_UNIFORM);
        lightColorUniform = getUniform(StdUniforms.LIGHT_COLOR_UNIFORM);
        specLightMatUniform = getUniform(StdUniforms.SPEC_LIGHTMAT_COLOR_UNIFORM);
        lightEyePosUniform = getUniform(StdUniforms.LIGHT_POS_EYE_UNIFORM);
        shininessUniform = getUniform(StdUniforms.SHININESS_UNIFORM);
        matSamplerUniform = getUniform(StdUniforms.MATERIAL_SAMPLER_UNIFORM);
    }

    @Override
    public void applyPropertiesToUniforms()
    {
        Matrix4 projMatrix = (Matrix4) getPropertyValue(RenderProperty.PROJECTION_MATRIX);
        projectionUniform.setValue(projMatrix.m);

        Matrix4 viewMatrix = (Matrix4) getPropertyValue(RenderProperty.VIEW_MATRIX);
        Matrix4 modelMatrix = (Matrix4) getPropertyValue(RenderProperty.MODEL_MATRIX);
        viewModelMatrix.set(viewMatrix);
        viewModelMatrix.multiplyBy(modelMatrix);

        viewModelUniform.setValue(viewModelMatrix.m);
        normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
        normalUniform.setValue(normalTransform.m);

        ambientLightColorUniform.setValue(getPropertyValue(RenderProperty.AMBIENT_LIGHT_COLOR));
        float[] lightColor = (float[]) getPropertyValue(RenderProperty.LIGHT_COLOR);
        lightColorUniform.setValue(lightColor);

        Texture texture = (Texture) getPropertyValue(RenderProperty.MAT_COLOR_TEXTURE);
        matSamplerUniform.setValue(texture.getSampler());

        float[] matColor = (float[]) getPropertyValue(RenderProperty.SPEC_MAT_COLOR);

        GraphicsUtils.vecMultiply(4, lightMatColor, lightColor, matColor);
        specLightMatUniform.setValue(lightMatColor);

        float[] lightPosition = (float[]) getPropertyValue(RenderProperty.LIGHT_POS);
        viewMatrix.transformFloatVector(lightEyeSpace, 0, lightPosition, 0);
        lightEyePosUniform.setValue(lightEyeSpace);

        shininessUniform.setValue(getPropertyValue(RenderProperty.SHININESS));
    }
}
