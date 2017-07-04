package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.UniformName;

/**
 * Renders the depth of geometry and nothing else.
 * Created by Steve on 6/21/2017.
 */

public class DepthSpotTechnique extends Technique3D
{
    private final Matrix4 projectionViewModelMatrix = Matrix4.newIdentity();

    public DepthSpotTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/vert_depth.glsl", "shaders/frag_depth.glsl", new RenderProperty[]{
                RenderProperty.PROJECTION_MATRIX,
                RenderProperty.VIEW_MATRIX,
                RenderProperty.MODEL_MATRIX
        });
    }

    public void applyConstantPropertiesImplement()
    {
    }

    @Override
    public void applyInstanceProperties()
    {
        setProjectionViewModel();
    }
}
