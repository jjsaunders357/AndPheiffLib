package com.pheiffware.lib.graphics.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.program.Uniform;
import com.pheiffware.lib.graphics.managed.program.UniformNames;
import com.pheiffware.lib.graphics.managed.texture.Texture;

/**
 * Shades mesh with a constant surface color and one light.  Handles, ambient, diffuse and specular lighting.
 * <p/>
 * Required Properties:
 * <p/>
 * RenderProperty.PROJECTION_MATRIX - Matrix4
 * <p/>
 * RenderProperty.VIEW_MATRIX - Matrix4
 * <p/>
 * RenderProperty.MODEL_MATRIX - Matrix4
 * <p/>
 * RenderProperty.AMBIENT_LIGHT_COLOR - float[4]
 * <p/>
 * RenderProperty.LIGHTING - Lighting
 * <p/>
 * RenderProperty.MAT_COLOR_TEXTURE - float[4]
 * <p/>
 * RenderProperty.SPEC_MAT_COLOR - float[4]
 * <p/>
 * RenderProperty.SHININESS - float
 * <p/>
 * Created by Steve on 4/23/2016.
 */
public class TextureMaterialTechnique extends Technique
{
    private final Uniform projectionUniform;
    private final Uniform viewModelUniform;
    private final Uniform normalUniform;
    private final Uniform ambientLightColorUniform;
    private final Uniform lightColorUniform;
    private final Uniform specLightMaterialUniform;
    private final Uniform lightEyePosUniform;
    private final Uniform onStateUniform;
    private final Uniform shininessUniform;
    private final Uniform matSamplerUniform;

    //Used internally to compute values to apply to uniforms
    private final Matrix4 viewModelMatrix = Matrix4.newIdentity();
    private final Matrix3 normalTransform = Matrix3.newIdentity();
    private final float[] lightMatColor = new float[4];

    public TextureMaterialTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/vert_mntl.glsl", "shaders/frag_mntl.glsl", new RenderProperty[]{
                RenderProperty.PROJECTION_MATRIX,
                RenderProperty.VIEW_MATRIX,
                RenderProperty.MODEL_MATRIX,
                RenderProperty.AMBIENT_LIGHT_COLOR,
                RenderProperty.LIGHTING,
                RenderProperty.MAT_COLOR_TEXTURE,
                RenderProperty.SPEC_MAT_COLOR,
                RenderProperty.SHININESS
        });
        projectionUniform = getUniform(UniformNames.PROJECTION_MATRIX_UNIFORM);
        viewModelUniform = getUniform(UniformNames.VIEW_MODEL_MATRIX_UNIFORM);
        normalUniform = getUniform(UniformNames.NORMAL_MATRIX_UNIFORM);
        ambientLightColorUniform = getUniform(UniformNames.AMBIENT_LIGHT_COLOR_UNIFORM);
        lightColorUniform = getUniform(UniformNames.LIGHT_COLOR_UNIFORM);
        specLightMaterialUniform = getUniform(UniformNames.SPEC_LIGHTMAT_COLOR_UNIFORM);
        lightEyePosUniform = getUniform(UniformNames.LIGHT_POS_EYE_UNIFORM);
        onStateUniform = getUniform(UniformNames.ON_STATE_UNIFORM);
        shininessUniform = getUniform(UniformNames.SHININESS_UNIFORM);
        matSamplerUniform = getUniform(UniformNames.MATERIAL_SAMPLER_UNIFORM);
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
        Texture texture = (Texture) getPropertyValue(RenderProperty.MAT_COLOR_TEXTURE);
        texture.autoBind();
        matSamplerUniform.setValue(texture.getBoundTextureUnitIndex());
        float[] specMatColor = (float[]) getPropertyValue(RenderProperty.SPEC_MAT_COLOR);

        Lighting lighting = (Lighting) getPropertyValue(RenderProperty.LIGHTING);
        lightEyePosUniform.setValue(lighting.getLightPositionsInEyeSpace());
        lightColorUniform.setValue(lighting.getColors());
        specLightMaterialUniform.setValue(lighting.calcLightMatColors(specMatColor));
        onStateUniform.setValue(lighting.getOnStates());
        shininessUniform.setValue(getPropertyValue(RenderProperty.SHININESS));
    }
}
