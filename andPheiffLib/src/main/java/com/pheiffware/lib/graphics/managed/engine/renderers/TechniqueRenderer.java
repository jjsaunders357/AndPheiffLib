package com.pheiffware.lib.graphics.managed.engine.renderers;

import com.pheiffware.lib.graphics.managed.engine.Renderer;
import com.pheiffware.lib.graphics.managed.program.Technique;

/**
 * Created by Steve on 6/21/2017.
 */

public class TechniqueRenderer extends Renderer
{
    private final TechniqueRenderPass techniqueRenderPass;

    public TechniqueRenderer(Technique overridingTechnique)
    {
        techniqueRenderPass = new TechniqueRenderPass(overridingTechnique);
    }

    @Override
    protected void renderImplement()
    {
        renderPass(techniqueRenderPass);
    }
}
