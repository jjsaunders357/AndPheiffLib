package com.pheiffware.lib.graphics.managed.engine.newEngine.renderers;

import com.pheiffware.lib.graphics.managed.engine.newEngine.BaseRenderPass;
import com.pheiffware.lib.graphics.managed.engine.newEngine.MeshHandle;

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
