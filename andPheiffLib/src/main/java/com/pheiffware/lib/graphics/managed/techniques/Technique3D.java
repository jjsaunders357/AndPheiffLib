package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Vec4F;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.ProgramTechnique;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.UniformName;

/**
 * Created by Steve on 7/4/2017.
 */

public abstract class Technique3D extends ProgramTechnique
{
    //Used internally to compute values to apply to uniforms
    private final Matrix4 viewModelMatrix = Matrix4.newIdentity();
    private final Matrix3 normalTransform = Matrix3.newIdentity();
    private final float[] matColor = new float[4];
    private final Vec4F lightEyePositions;
    private final Vec4F ambientLightMat;
    private final Vec4F diffLightMat;
    private final Vec4F specLightMat;

    public Technique3D(String... shaderPaths) throws GraphicsException
    {
        super(shaderPaths);
        lightEyePositions = new Vec4F(Lighting.numLightsSupported);
        ambientLightMat = new Vec4F(1);
        diffLightMat = new Vec4F(Lighting.numLightsSupported);
        specLightMat = new Vec4F(Lighting.numLightsSupported);
    }

    protected final void setViewModel()
    {
        Matrix4 viewMatrix = (Matrix4) getPropertyValue(RenderProperty.VIEW_MATRIX);
        Matrix4 modelMatrix = (Matrix4) getPropertyValue(RenderProperty.MODEL_MATRIX);
        viewModelMatrix.set(viewMatrix);
        viewModelMatrix.multiplyBy(modelMatrix);
        setUniformValue(UniformName.VIEW_MODEL_MATRIX, viewModelMatrix.m);
    }

    protected final void setViewModelNormal()
    {
        setViewModel();
        setNormalFrom(viewModelMatrix);
    }

    protected final void setNormalFrom(Matrix4 matrix)
    {
        this.normalTransform.setNormalTransformFromMatrix4Fast(matrix);
        setUniformValue(UniformName.NORMAL_MATRIX, normalTransform.m);
    }


    protected final void setProjection()
    {
        Matrix4 projectionMatrix = (Matrix4) getPropertyValue(RenderProperty.PROJECTION_MATRIX);
        setUniformValue(UniformName.PROJECTION_MATRIX, projectionMatrix.m);
    }

    protected final void setLightingConstants()
    {
        Matrix4 viewMatrix = (Matrix4) getPropertyValue(RenderProperty.VIEW_MATRIX);
        Lighting lighting = (Lighting) getPropertyValue(RenderProperty.LIGHTING);
        lighting.transformLightPositions(lightEyePositions, viewMatrix);
        setUniformValue(UniformName.LIGHT_POS_EYE, lightEyePositions.getData());
        setUniformValue(UniformName.ON_STATE, lighting.getOnStates());
    }

    /**
     * Perform all lighting pre-shader lighting calculations and apply uniforms for constant color mesh.
     */
    protected final void setLightingColors()
    {
        //Get material color and extract alpha/non-alpha components
        final float[] temp = (float[]) getPropertyValue(RenderProperty.MAT_COLOR);

        //Material color, does not contribute to opaqueness and is left transparent
        matColor[0] = temp[0];
        matColor[1] = temp[1];
        matColor[2] = temp[2];
        final float alpha = temp[3];

        final Lighting lighting = (Lighting) getPropertyValue(RenderProperty.LIGHTING);
        ambientLightMat.copy(lighting.getAmbientLightColor());
        ambientLightMat.multiplyBy(matColor);
        diffLightMat.copyAll(lighting.getColors());
        diffLightMat.multiplyEachBy(new Vec4F(matColor));

        setUniformValue(UniformName.AMBIENT_LIGHTMAT_COLOR, ambientLightMat.getData());
        setUniformValue(UniformName.DIFF_LIGHTMAT_COLOR, diffLightMat.getData());
        setUniformValue(UniformName.MAT_ALPHA, alpha);

        setSpecLightingColor();
    }

    /**
     * Perform specular lighting pre-shader lighting calculations and apply uniforms for meshes with non-constant color (such as those which are textured).
     */
    protected final void setSpecLightingColor()
    {
        final Lighting lighting = (Lighting) getPropertyValue(RenderProperty.LIGHTING);
        final float[] specMatColor = (float[]) getPropertyValue(RenderProperty.SPEC_MAT_COLOR);

        specLightMat.copyAll(lighting.getColors());
        specLightMat.multiplyEachBy(new Vec4F(specMatColor));

        setUniformValue(UniformName.SPEC_LIGHTMAT_COLOR, specLightMat.getData());
    }


}
