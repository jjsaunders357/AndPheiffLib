package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.UniformName;

/**
 * Mesh projected such that it appears embedded below or projecting out from screen surface.  All vertex locations should be expressed in screen space. [0,0,0] represents the
 * center of the screen on the surface.  A distance of 1, in any direction, corresponds to 1/2 of the width of the screen.
 * <p/>
 * Shades mesh with a constant surface color and multiple lights light.  Handles, ambient, diffuse and specular lighting.
 * Created by Steve on 4/23/2016.
 */
public class HoloColorMaterialTechnique extends Technique3D
{
    public HoloColorMaterialTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/vert_holo_mncl.glsl", "shaders/frag_holo_mncl.glsl", new RenderProperty[]{
                RenderProperty.HOLO_PROJECTION,
                RenderProperty.MODEL_MATRIX,
                RenderProperty.LIGHTING,
                RenderProperty.MAT_COLOR,
                RenderProperty.SPEC_MAT_COLOR,
                RenderProperty.SHININESS
        });
    }

    protected void applyConstantPropertiesImplement()
    {
        HoloData holoData = (HoloData) getPropertyValue(RenderProperty.HOLO_PROJECTION);

        Lighting lighting = (Lighting) getPropertyValue(RenderProperty.LIGHTING);
        float[] transformedLightPositions = lighting.transformLightPositions(holoData.orientationMatrix);
        setUniformValue(UniformName.LIGHT_POS, transformedLightPositions);
        setUniformValue(UniformName.ON_STATE, lighting.getOnStates());

        setUniformValue(UniformName.EYE_POSITION, holoData.eyePositionInScreenSpace);
        setUniformValue(UniformName.ZNEAR, holoData.zNear);
        setUniformValue(UniformName.ZFAR, holoData.zFar);
        setUniformValue(UniformName.ASPECT_RATIO, holoData.aspectRatio);
        setUniformValue(UniformName.SCREEN_COLOR, holoData.screenColor);
    }

    @Override
    public void applyInstanceProperties()
    {
        Matrix4 modelMatrix = (Matrix4) getPropertyValue(RenderProperty.MODEL_MATRIX);
        setUniformValue(UniformName.MODEL_MATRIX, modelMatrix.m);
        setNormalFrom(modelMatrix);
        setLightingColors();
        setUniformValue(UniformName.SHININESS, getPropertyValue(RenderProperty.SHININESS));
    }

    public static class HoloData
    {
        private final Matrix4 orientationMatrix;
        final float[] eyePositionInScreenSpace;
        final float zNear;
        final float zFar;
        final float aspectRatio;
        final float[] screenColor;

        /**
         * @param orientationMatrix
         * @param eyePositionInFlatScreenSpace absolute space is relative to the center of the screen, with flat orientation
         * @param zNear
         * @param zFar
         * @param aspectRatio
         * @param screenColor
         */
        public HoloData(Matrix4 orientationMatrix, float[] eyePositionInFlatScreenSpace, float zNear, float zFar, float aspectRatio, float[] screenColor)
        {
            this.orientationMatrix = orientationMatrix;
            this.eyePositionInScreenSpace = orientationMatrix.transform4DFloatVector(eyePositionInFlatScreenSpace);
            this.zNear = zNear;
            this.zFar = zFar;
            this.aspectRatio = aspectRatio;
            this.screenColor = screenColor;
        }
    }
}
