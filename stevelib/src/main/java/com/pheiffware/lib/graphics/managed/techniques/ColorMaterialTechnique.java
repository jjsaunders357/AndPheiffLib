package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.program.Uniform;
import com.pheiffware.lib.graphics.managed.program.UniformNames;
import com.pheiffware.lib.utils.GraphicsUtils;

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
 * RenderProperty.MAT_COLOR - float[4]
 * <p/>
 * RenderProperty.SPEC_MAT_COLOR - float[4]
 * <p/>
 * RenderProperty.SHININESS - float
 * <p/>
 * Created by Steve on 4/23/2016.
 */
public class ColorMaterialTechnique extends Technique
{
    private final Uniform projectionUniform;
    private final Uniform viewModelUniform;
    private final Uniform normalUniform;
    private final Uniform ambientLightColorUniform;
    private final Uniform diffLightMaterialUniform;
    private final Uniform specLightMaterialUniform;
    private final Uniform lightEyePosUniform;
    private final Uniform onStateUniform;
    private final Uniform shininessUniform;

    //Used internally to compute values to apply to uniforms
    private final Matrix4 viewModelMatrix = Matrix4.newIdentity();
    private final Matrix3 normalTransform = Matrix3.newIdentity();
    private final float[] ambLightMatColor = new float[4];

    public ColorMaterialTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/vert_mncl.glsl", "shaders/frag_mncl.glsl", new RenderProperty[]{
                RenderProperty.PROJECTION_MATRIX,
                RenderProperty.VIEW_MATRIX,
                RenderProperty.MODEL_MATRIX,
                RenderProperty.AMBIENT_LIGHT_COLOR,
                RenderProperty.LIGHTING,
                RenderProperty.MAT_COLOR,
                RenderProperty.SPEC_MAT_COLOR,
                RenderProperty.SHININESS
        });
        projectionUniform = getUniform(UniformNames.PROJECTION_MATRIX_UNIFORM);
        viewModelUniform = getUniform(UniformNames.VIEW_MODEL_MATRIX_UNIFORM);
        normalUniform = getUniform(UniformNames.NORMAL_MATRIX_UNIFORM);
        ambientLightColorUniform = getUniform(UniformNames.AMBIENT_LIGHTMAT_COLOR_UNIFORM);
        diffLightMaterialUniform = getUniform(UniformNames.DIFF_LIGHTMAT_COLOR_UNIFORM);
        specLightMaterialUniform = getUniform(UniformNames.SPEC_LIGHTMAT_COLOR_UNIFORM);
        lightEyePosUniform = getUniform(UniformNames.LIGHT_POS_EYE_UNIFORM);
        onStateUniform = getUniform(UniformNames.ON_STATE_UNIFORM);
        shininessUniform = getUniform(UniformNames.SHININESS_UNIFORM);
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

        float[] ambLightColor = (float[]) getPropertyValue(RenderProperty.AMBIENT_LIGHT_COLOR);
        float[] diffMatColor = (float[]) getPropertyValue(RenderProperty.MAT_COLOR);
        float[] specMatColor = (float[]) getPropertyValue(RenderProperty.SPEC_MAT_COLOR);
        GraphicsUtils.vecMultiply(4, ambLightMatColor, ambLightColor, diffMatColor);
        ambientLightColorUniform.setValue(ambLightMatColor);

        Lighting lighting = (Lighting) getPropertyValue(RenderProperty.LIGHTING);
        lightEyePosUniform.setValue(lighting.getLightPositionsInEyeSpace());
        diffLightMaterialUniform.setValue(lighting.calcLightMatColors(diffMatColor));
        specLightMaterialUniform.setValue(lighting.calcLightMatColors(specMatColor));
        onStateUniform.setValue(lighting.getOnStates());
        shininessUniform.setValue(getPropertyValue(RenderProperty.SHININESS));
    }
}
