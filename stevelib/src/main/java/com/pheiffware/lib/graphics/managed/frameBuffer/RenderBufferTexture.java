package com.pheiffware.lib.graphics.managed.frameBuffer;

import com.pheiffware.lib.graphics.managed.texture.Texture;
import com.pheiffware.lib.graphics.managed.texture.TextureBinder;

/**
 * A texture with a built-in render buffer.  If attached for rendering, then rending happens on the render buffer.  Later when used data is blit-ed to texture.
 * <p/>
 * TODO: Implement me!
 * <p/>
 * Created by Steve on 6/3/2016.
 */
public class RenderBufferTexture extends Texture
{
    public RenderBufferTexture(int type, int handle, TextureBinder textureBinder)
    {
        super(type, handle, textureBinder);
    }
}
