package com.pheiffware.lib.graphics.managed.engine.newEngine.renderers;

import com.pheiffware.lib.graphics.managed.engine.newEngine.BaseRenderPass;
import com.pheiffware.lib.graphics.managed.engine.newEngine.MeshHandle;
import com.pheiffware.lib.graphics.managed.program.Technique;

/**
 * Created by Steve on 6/21/2017.
 */

public class TechniqueRenderPass extends BaseRenderPass
{
    private final Technique overridingTechnique;

    public TechniqueRenderPass(Technique overridingTechnique)
    {
        this.overridingTechnique = overridingTechnique;
    }

    @Override
    protected void renderObject(MeshHandle[] meshHandles)
    {
        for (MeshHandle meshHandle : meshHandles)
        {
            meshHandle.drawTriangles(overridingTechnique);
        }
    }
}
