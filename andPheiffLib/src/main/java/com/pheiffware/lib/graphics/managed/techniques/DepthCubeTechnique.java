package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;

/**
 * Renders the depth of geometry and nothing else.
 * <p>
 * Created by Steve on 6/21/2017.
 */

public class DepthCubeTechnique extends Technique3D
{
    public DepthCubeTechnique(AssetLoader al) throws GraphicsException
    {
        super(al, "shaders/vert_depth.glsl", "shaders/frag_depth.glsl", new RenderProperty[]{
                RenderProperty.PROJECTION_LINEAR_DEPTH,
                RenderProperty.VIEW_MATRIX,
                RenderProperty.MODEL_MATRIX
        });
    }

    public void applyConstantPropertiesImplement()
    {
        setProjectionLinearDepth();
    }

    @Override
    public void applyInstanceProperties()
    {
        setViewModel();
    }
}
