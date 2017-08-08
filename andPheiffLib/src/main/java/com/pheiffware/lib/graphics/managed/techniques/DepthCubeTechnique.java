package com.pheiffware.lib.graphics.managed.techniques;

import com.pheiffware.lib.ParseException;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;

import java.io.IOException;
import java.util.HashMap;

/**
 * Renders the depth of geometry and nothing else.
 * <p>
 * Created by Steve on 6/21/2017.
 */

public class DepthCubeTechnique extends Technique3D
{
    public DepthCubeTechnique(GLCache glCache) throws GraphicsException, IOException, ParseException
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
