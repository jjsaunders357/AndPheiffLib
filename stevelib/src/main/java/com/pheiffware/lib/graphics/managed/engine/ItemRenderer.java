package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.vertexBuffer.StaticVertexBuffer;

/**
 * Capable of rendering a series of render items.  Calling sequence:
 * <p/>
 * preRender()
 * <p/>
 * renderItem()...once per item
 * <p/>
 * postRender()
 * @param <M>
 */
public interface ItemRenderer<M>
{
    void preRender();

    void renderItem(MeshRenderHandle<M> meshHandle, M material, StaticVertexBuffer vertexBuffer, RenderPropertyValue[] propertyValues, RenderProperty[] overrideProperties, Object[] overridePropertyValues);

    void postRender();
}
