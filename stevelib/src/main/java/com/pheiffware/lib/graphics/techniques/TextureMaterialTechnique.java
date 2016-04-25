package com.pheiffware.lib.graphics.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.Texture;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.program.Uniform;
import com.pheiffware.lib.graphics.utils.GraphicsUtils;

/**
 * Shades mesh with a constant surface color and one light.  Handles, ambient, diffuse and specular lighting.
 * <p/>
 * Required Properties:
 * <p/>
 * ShadConst.PROJECTION_MATRIX - Matrix4
 * <p/>
 * ShadConst.VIEW_MATRIX - Matrix4
 * <p/>
 * ShadConst.MODEL_MATRIX) - Matrix4
 * <p/>
 * ShadConst.AMBIENT_LIGHT_COLOR - float[4]
 * <p/>
 * ShadConst.LIGHT_COLOR - float[4]
 * <p/>
 * ShadConst.LIGHT_POS - float[4]
 * <p/>
 * ShadConst.MAT_COLOR_TEXTURE - float[4]
 * <p/>
 * ShadConst.SPEC_MAT_COLOR - float[4]
 * <p/>
 * ShadConst.SHININESS - float
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
        super(al, "shaders/vert_mntl.glsl", "shaders/frag_mntl.glsl", new TechniqueProperty[]{
                TechniqueProperty.PROJECTION_MATRIX,
                TechniqueProperty.VIEW_MATRIX,
                TechniqueProperty.MODEL_MATRIX,
                TechniqueProperty.AMBIENT_LIGHT_COLOR,
                TechniqueProperty.MAT_COLOR,
                TechniqueProperty.LIGHT_COLOR,
                TechniqueProperty.SPEC_MAT_COLOR,
                TechniqueProperty.LIGHT_POS,
                TechniqueProperty.SHININESS,
                TechniqueProperty.MAT_COLOR_TEXTURE
        });
        projectionUniform = getUniform(ShadConst.PROJECTION_MATRIX_UNIFORM);
        viewModelUniform = getUniform(ShadConst.VIEW_MODEL_MATRIX_UNIFORM);
        normalUniform = getUniform(ShadConst.NORMAL_MATRIX_UNIFORM);
        ambientLightColorUniform = getUniform(ShadConst.AMBIENT_LIGHT_COLOR_UNIFORM);
        lightColorUniform = getUniform(ShadConst.LIGHT_COLOR_UNIFORM);
        specLightMatUniform = getUniform(ShadConst.SPEC_LIGHTMAT_COLOR_UNIFORM);
        lightEyePosUniform = getUniform(ShadConst.LIGHT_POS_EYE_UNIFORM);
        shininessUniform = getUniform(ShadConst.SHININESS_UNIFORM);
        matSamplerUniform = getUniform(ShadConst.MATERIAL_SAMPLER_UNIFORM);
    }

    @Override
    public void applyPropertiesToUniforms()
    {
        Matrix4 projMatrix = (Matrix4) getPropertyValue(TechniqueProperty.PROJECTION_MATRIX);
        projectionUniform.setValue(projMatrix.m);

        Matrix4 viewMatrix = (Matrix4) getPropertyValue(TechniqueProperty.VIEW_MATRIX);
        Matrix4 modelMatrix = (Matrix4) getPropertyValue(TechniqueProperty.MODEL_MATRIX);
        viewModelMatrix.set(viewMatrix);
        viewModelMatrix.multiplyBy(modelMatrix);

        viewModelUniform.setValue(viewModelMatrix.m);
        normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
        normalUniform.setValue(normalTransform.m);

        ambientLightColorUniform.setValue(getPropertyValue(TechniqueProperty.AMBIENT_LIGHT_COLOR));
        float[] lightColor = (float[]) getPropertyValue(TechniqueProperty.LIGHT_COLOR);
        lightColorUniform.setValue(lightColor);

        Texture texture = (Texture) getPropertyValue(TechniqueProperty.MAT_COLOR_TEXTURE);
        matSamplerUniform.setValue(texture.getSampler());

        float[] matColor = (float[]) getPropertyValue(TechniqueProperty.SPEC_MAT_COLOR);

        GraphicsUtils.vecMultiply(4, lightMatColor, lightColor, matColor);
        specLightMatUniform.setValue(lightMatColor);

        float[] lightPosition = (float[]) getPropertyValue(TechniqueProperty.LIGHT_POS);
        viewMatrix.transformFloatVector(lightEyeSpace, 0, lightPosition, 0);
        lightEyePosUniform.setValue(lightEyeSpace);

        shininessUniform.setValue(getPropertyValue(TechniqueProperty.SHININESS));
    }

    @Override
    public void putVertexAttributes(Mesh transferMesh, int vertexWriteOffset)
    {
        staticVertexBuffer.putAttributeFloats(ShadConst.VERTEX_POSITION_ATTRIBUTE, transferMesh.getPositionData(), vertexWriteOffset);
        staticVertexBuffer.putAttributeFloats(ShadConst.VERTEX_NORMAL_ATTRIBUTE, transferMesh.getNormalData(), vertexWriteOffset);
        staticVertexBuffer.putAttributeFloats(ShadConst.VERTEX_TEXCOORD_ATTRIBUTE, transferMesh.getTexCoordData(), vertexWriteOffset);
    }
}
