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
import com.pheiffware.lib.graphics.utils.GraphicsUtils;

/**
 * Shades mesh with a constant surface color and one light.  Handles, ambient, diffuse and specular lighting.
 * <p/>
 * Required Properties:
 * <p/>
 * RenderProperty.HOLO_PROJECTION - HoloData
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
public class HoloColorMaterialTechnique extends Technique
{
    private final Uniform eyePositionUniform;
    private final Uniform zNearUniform;
    private final Uniform zFarUniform;
    private final Uniform modelUniform;
    private final Uniform normalUniform;
    private final Uniform ambientLightColorUniform;
    private final Uniform diffLightMaterialUniform;
    private final Uniform specLightMaterialUniform;
    private final Uniform lightPosUniform;
    private final Uniform onStateUniform;
    private final Uniform shininessUniform;

    //Used internally to compute values to apply to uniforms
    private final Matrix3 normalTransform = Matrix3.newIdentity();
    private final float[] ambLightMatColor = new float[4];

    public HoloColorMaterialTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/vert_holo_mncl.glsl", "shaders/frag_holo_mncl.glsl", new RenderProperty[]{
                RenderProperty.HOLO_PROJECTION,
                RenderProperty.MODEL_MATRIX,
                RenderProperty.AMBIENT_LIGHT_COLOR,
                RenderProperty.LIGHTING,
                RenderProperty.MAT_COLOR,
                RenderProperty.SPEC_MAT_COLOR,
                RenderProperty.SHININESS
        });
        eyePositionUniform = getUniform(UniformNames.EYE_POSITION_UNIFORM);
        zNearUniform = getUniform(UniformNames.ZNEAR_UNIFORM);
        zFarUniform = getUniform(UniformNames.ZFAR_UNIFORM);
        modelUniform = getUniform(UniformNames.MODEL_MATRIX_UNIFORM);
        normalUniform = getUniform(UniformNames.NORMAL_MATRIX_UNIFORM);
        ambientLightColorUniform = getUniform(UniformNames.AMBIENT_LIGHTMAT_COLOR_UNIFORM);
        diffLightMaterialUniform = getUniform(UniformNames.DIFF_LIGHTMAT_COLOR_UNIFORM);
        specLightMaterialUniform = getUniform(UniformNames.SPEC_LIGHTMAT_COLOR_UNIFORM);
        lightPosUniform = getUniform(UniformNames.LIGHT_POS_UNIFORM);
        onStateUniform = getUniform(UniformNames.ON_STATE_UNIFORM);
        shininessUniform = getUniform(UniformNames.SHININESS_UNIFORM);
    }


    @Override
    public void applyPropertiesToUniforms()
    {
        Matrix4 modelMatrix = (Matrix4) getPropertyValue(RenderProperty.MODEL_MATRIX);
        modelUniform.setValue(modelMatrix.m);
        normalTransform.setNormalTransformFromMatrix4Fast(modelMatrix);
        normalUniform.setValue(normalTransform.m);

        float[] ambLightColor = (float[]) getPropertyValue(RenderProperty.AMBIENT_LIGHT_COLOR);
        float[] diffMatColor = (float[]) getPropertyValue(RenderProperty.MAT_COLOR);
        float[] specMatColor = (float[]) getPropertyValue(RenderProperty.SPEC_MAT_COLOR);
        GraphicsUtils.vecMultiply(4, ambLightMatColor, ambLightColor, diffMatColor);
        ambientLightColorUniform.setValue(ambLightMatColor);

        Lighting lighting = (Lighting) getPropertyValue(RenderProperty.LIGHTING);
        lightPosUniform.setValue(lighting.getLightPositions());

        diffLightMaterialUniform.setValue(lighting.calcLightMatColors(diffMatColor));
        specLightMaterialUniform.setValue(lighting.calcLightMatColors(specMatColor));
        onStateUniform.setValue(lighting.getOnStates());
        shininessUniform.setValue(getPropertyValue(RenderProperty.SHININESS));

        HoloData holoData = (HoloData) getPropertyValue(RenderProperty.HOLO_PROJECTION);
        eyePositionUniform.setValue(holoData.eye);
        zNearUniform.setValue(holoData.zNear);
        zFarUniform.setValue(holoData.zFar);
    }

    public static class HoloData
    {
        public final float[] eye;
        public float zNear;
        public float zFar;

        public HoloData(float[] eye, float zNear, float zFar)
        {
            this.eye = eye;
            this.zNear = zNear;
            this.zFar = zFar;
        }
    }
}
