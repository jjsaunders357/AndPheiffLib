package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
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


    public Technique3D(AssetLoader al, String vertexShaderAsset, String fragmentShaderAsset, RenderProperty[] properties) throws GraphicsException
    {
        super(al, vertexShaderAsset, fragmentShaderAsset, properties);
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
        float[] transformedLightPositions = lighting.transformLightPositions(viewMatrix);
        setUniformValue(UniformName.LIGHT_POS_EYE, transformedLightPositions);
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

        final float[] specMatColor = (float[]) getPropertyValue(RenderProperty.SPEC_MAT_COLOR);

        final Lighting lighting = (Lighting) getPropertyValue(RenderProperty.LIGHTING);
        float[] ambLightMatColor = lighting.calcAmbientMatColor(matColor);
        float[] lightDiffMatColor = lighting.calcDiffMatColor(matColor);
        float[] lightSpecColor = lighting.calcSpecMatColor(specMatColor);
        setUniformValue(UniformName.AMBIENT_LIGHTMAT_COLOR, ambLightMatColor);
        setUniformValue(UniformName.DIFF_LIGHTMAT_COLOR, lightDiffMatColor);
        setUniformValue(UniformName.SPEC_LIGHTMAT_COLOR, lightSpecColor);
        setUniformValue(UniformName.MAT_ALPHA, alpha);
    }

    /**
     * Perform specular lighting pre-shader lighting calculations and apply uniforms for meshes with non-constant color (such as those which are textured).
     */
    protected final void setSpecLightingColor()
    {
        final float[] specMatColor = (float[]) getPropertyValue(RenderProperty.SPEC_MAT_COLOR);
        final Lighting lighting = (Lighting) getPropertyValue(RenderProperty.LIGHTING);
        float[] lightSpecColor = lighting.calcSpecMatColor(specMatColor);
        setUniformValue(UniformName.SPEC_LIGHTMAT_COLOR, lightSpecColor);
    }


}
