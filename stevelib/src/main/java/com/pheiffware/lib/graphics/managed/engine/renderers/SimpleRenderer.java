package com.pheiffware.lib.graphics.managed.engine.renderers;

import com.pheiffware.lib.graphics.managed.engine.Renderer;

/**
 * Created by Steve on 6/19/2017.
 */

public class SimpleRenderer extends Renderer
{
    @Override
    protected void renderImplement()
    {
        renderPass(new SimpleRenderPass());
    }
}
