package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.ProgramTechnique;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.UniformName;
import com.pheiffware.lib.utils.GraphicsUtils;

/**
 * Shades mesh with a constant surface color and given lights' settings.  Handles, ambient, diffuse and specular lighting.
 * Created by Steve on 4/23/2016.
 */
public class ColorMaterialTechnique extends ProgramTechnique
{
    //Used internally to compute values to apply to uniforms
    private final Matrix4 viewModelMatrix = Matrix4.newIdentity();
    private final Matrix3 normalTransform = Matrix3.newIdentity();
    private final float[] ambLightMatColor = new float[4];
    //Holds the diffraction material color, with the alpha component set to 0
    private final float[] diffMatColor = new float[4];

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
    }


    @Override
    public void applyInstanceProperties()
    {
        Matrix4 projectionMatrix = (Matrix4) getPropertyValue(RenderProperty.PROJECTION_MATRIX);
        Matrix4 viewMatrix = (Matrix4) getPropertyValue(RenderProperty.VIEW_MATRIX);
        Matrix4 modelMatrix = (Matrix4) getPropertyValue(RenderProperty.MODEL_MATRIX);
        viewModelMatrix.set(viewMatrix);
        viewModelMatrix.multiplyBy(modelMatrix);
        normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);

        float[] ambLightColor = (float[]) getPropertyValue(RenderProperty.AMBIENT_LIGHT_COLOR);
        float[] temp = (float[]) getPropertyValue(RenderProperty.MAT_COLOR);
        diffMatColor[0] = temp[0];
        diffMatColor[1] = temp[1];
        diffMatColor[2] = temp[2];
        float alpha = temp[3];
        float[] specMatColor = (float[]) getPropertyValue(RenderProperty.SPEC_MAT_COLOR);
        GraphicsUtils.vecMultiply(4, ambLightMatColor, ambLightColor, diffMatColor);

        Lighting lighting = (Lighting) getPropertyValue(RenderProperty.LIGHTING);

        setUniformValue(UniformName.PROJECTION_MATRIX, projectionMatrix.m);
        setUniformValue(UniformName.VIEW_MODEL_MATRIX, viewModelMatrix.m);
        setUniformValue(UniformName.NORMAL_MATRIX, normalTransform.m);
        setUniformValue(UniformName.AMBIENT_LIGHTMAT_COLOR, ambLightMatColor);
        setUniformValue(UniformName.LIGHT_POS_EYE, lighting.getLightPositionsInEyeSpace());
        setUniformValue(UniformName.DIFF_LIGHTMAT_COLOR, lighting.calcLightMatColors(diffMatColor));
        setUniformValue(UniformName.SPEC_LIGHTMAT_COLOR, lighting.calcLightMatColors(specMatColor));
        setUniformValue(UniformName.ON_STATE, lighting.getOnStates());
        setUniformValue(UniformName.MAT_ALPHA, alpha);
        setUniformValue(UniformName.SHININESS, getPropertyValue(RenderProperty.SHININESS));
    }
}
