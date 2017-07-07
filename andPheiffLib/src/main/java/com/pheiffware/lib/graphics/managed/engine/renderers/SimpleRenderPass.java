package com.pheiffware.lib.graphics.managed.engine.renderers;

import com.pheiffware.lib.graphics.managed.engine.BaseRenderPass;
import com.pheiffware.lib.graphics.managed.engine.MeshHandle;

/**
 * Created by Steve on 6/19/2017.
 */

public class SimpleRenderPass extends BaseRenderPass
{
    @Override
    protected void renderObject(MeshHandle[] meshHandles)
    {
        for (MeshHandle meshHandle : meshHandles)
        {
            meshHandle.drawTriangles();
        }
    }
}
