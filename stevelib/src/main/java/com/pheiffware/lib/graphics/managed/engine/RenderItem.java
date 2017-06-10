package com.pheiffware.lib.graphics.managed.engine;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;

/**
 * Used to hold information about a single submitted item for rendering.
 *
 * @param <M>
 */
public class RenderItem<M>
{
    public final MeshRenderHandle<M> meshHandle;
    public final RenderProperty[] overrideProperties;
    public final Object[] overridePropertyValues;

    public RenderItem(MeshRenderHandle<M> meshHandle, RenderProperty[] overrideProperties, Object[] overridePropertyValues)
    {
        this.meshHandle = meshHandle;
        this.overrideProperties = overrideProperties;
        this.overridePropertyValues = overridePropertyValues;
    }
}
