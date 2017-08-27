package com.pheiffware.lib.graphics.managed.engine.renderers;

import com.pheiffware.lib.graphics.managed.engine.Renderer;
import com.pheiffware.lib.graphics.managed.program.Technique;

import java.util.List;

/**
 * Created by Steve on 6/19/2017.
 */

public class SimpleRenderer extends Renderer
{
    public SimpleRenderer(Technique... techniques)
    {
        super(techniques);
    }

    public SimpleRenderer(List<Technique> techniques)
    {
        super(techniques);
    }

    public void render()
    {
        renderPass();
    }
}
