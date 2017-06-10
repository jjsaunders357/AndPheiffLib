package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.program.Uniform;
import com.pheiffware.lib.graphics.managed.program.UniformNames;

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
 * RenderProperty.LIGHT_RENDER_POSITION - float[4]
 * <p/>
 * Created by Steve on 4/23/2016.
 */
public class ShadowTechnique extends Technique
{
    private final Uniform projectionViewModelUniform;

    //Used internally to compute values to apply to uniforms
    private final Matrix4 projectionViewModelMatrix = Matrix4.newIdentity();

    public ShadowTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/vert_mncl.glsl", "shaders/frag_mncl.glsl", new RenderProperty[]{
                RenderProperty.LIGHT_RENDER_POSITION
        });
        projectionViewModelUniform = getUniform(UniformNames.PROJECTION_VIEW_MODEL_MATRIX_UNIFORM);
    }


    @Override
    public void applyPropertiesToUniforms()
    {
        Matrix4 projMatrix = (Matrix4) getPropertyValue(RenderProperty.PROJECTION_MATRIX);

        Matrix4 viewMatrix = (Matrix4) getPropertyValue(RenderProperty.VIEW_MATRIX);

        //The model matrix holds the light's position.
        Matrix4 modelMatrix = (Matrix4) getPropertyValue(RenderProperty.MODEL_MATRIX);
        projectionViewModelMatrix.set(projMatrix);
        projectionViewModelMatrix.multiplyBy(viewMatrix);
        projectionViewModelMatrix.multiplyBy(modelMatrix);
        projectionViewModelUniform.setValue(projectionViewModelMatrix);
    }
}
