package com.pheiffware.lib.graphics.managed.frameBuffer;

/**
 * Anything which can be bound to a FrameBuffer
 * <p/>
 * Created by Steve on 6/3/2016.
 */
public interface RenderTarget
{
    void attach(int attachmentPoint);
}
