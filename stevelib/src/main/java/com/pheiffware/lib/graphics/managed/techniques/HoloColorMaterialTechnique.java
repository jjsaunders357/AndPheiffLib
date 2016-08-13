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
 * Mesh projected such that it appears embedded below or projecting out from screen surface.  All vertex locations should be expressed in screen space. [0,0,0] represents the
 * center of the screen on the surface.  A distance of 1, in any direction, corresponds to 1/2 of the width of the screen.
 * <p/>
 * Shades mesh with a constant surface color and multiple lights light.  Handles, ambient, diffuse and specular lighting.
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
    private final Uniform modelUniform = getUniform(UniformNames.MODEL_MATRIX_UNIFORM);
    private final Uniform normalUniform = getUniform(UniformNames.NORMAL_MATRIX_UNIFORM);
    private final Uniform ambientLightColorUniform = getUniform(UniformNames.AMBIENT_LIGHTMAT_COLOR_UNIFORM);
    private final Uniform diffLightMaterialUniform = getUniform(UniformNames.DIFF_LIGHTMAT_COLOR_UNIFORM);
    private final Uniform specLightMaterialUniform = getUniform(UniformNames.SPEC_LIGHTMAT_COLOR_UNIFORM);
    private final Uniform lightPosUniform = getUniform(UniformNames.LIGHT_POS_UNIFORM);
    private final Uniform onStateUniform = getUniform(UniformNames.ON_STATE_UNIFORM);
    private final Uniform shininessUniform = getUniform(UniformNames.SHININESS_UNIFORM);
    private final Uniform eyePositionUniform = getUniform(UniformNames.EYE_POSITION_UNIFORM);
    private final Uniform zNearUniform = getUniform(UniformNames.ZNEAR_UNIFORM);
    private final Uniform zFarUniform = getUniform(UniformNames.ZFAR_UNIFORM);
    private final Uniform aspectRatioUniform = getUniform(UniformNames.ASPECT_RATIO_UNIFORM);
    private final Uniform screenColorUniform = getUniform(UniformNames.SCREEN_COLOR_UNIFORM);

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
//        lightPosUniform.setValue(lighting.getRawLightPositions());
        lightPosUniform.setValue(lighting.getLightPositionsInEyeSpace());

        diffLightMaterialUniform.setValue(lighting.calcLightMatColors(diffMatColor));
        specLightMaterialUniform.setValue(lighting.calcLightMatColors(specMatColor));
        onStateUniform.setValue(lighting.getOnStates());
        shininessUniform.setValue(getPropertyValue(RenderProperty.SHININESS));

        HoloData holoData = (HoloData) getPropertyValue(RenderProperty.HOLO_PROJECTION);
        eyePositionUniform.setValue(holoData.eye);
        zNearUniform.setValue(holoData.zNear);
        zFarUniform.setValue(holoData.zFar);
        aspectRatioUniform.setValue(holoData.aspectRatio);
        screenColorUniform.setValue(holoData.screenColor);
    }

    public static class HoloData
    {
        public final float[] eye;
        public float zNear;
        public float zFar;
        public float aspectRatio;
        public float[] screenColor;

        public HoloData(float[] eye, float zNear, float zFar, float aspectRatio, float[] screenColor)
        {
            this.eye = eye;
            this.zNear = zNear;
            this.zFar = zFar;
            this.aspectRatio = aspectRatio;
            this.screenColor = screenColor;
        }
    }
}
