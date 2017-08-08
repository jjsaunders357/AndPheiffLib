package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.ParseException;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;

import java.io.IOException;
import java.util.HashMap;

/**
 * Renders the depth of geometry and nothing else.
 * Created by Steve on 6/21/2017.
 */

public class DepthSpotTechnique extends Technique3D
{
    private final Matrix4 projectionViewModelMatrix = Matrix4.newIdentity();

    public DepthSpotTechnique(GLCache glCache) throws GraphicsException, IOException, ParseException
    {
        super(glCache, new HashMap<String, Object>(), new RenderProperty[]{
                RenderProperty.PROJECTION_LINEAR_DEPTH,
                RenderProperty.VIEW_MATRIX,
                RenderProperty.MODEL_MATRIX
        }, "vert_depth.glsl", "frag_depth.glsl");
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
