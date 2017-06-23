package com.pheiffware.lib.graphics.managed.texture;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;

/**
 * Created by Steve on 6/22/2017.
 */

public abstract class TextureBuilder<T>
{
    protected final TextureBinder textureBinder;
    protected FilterQuality filterQuality;
    protected int sWrap = GLES20.GL_CLAMP_TO_EDGE;
    protected int tWrap = GLES20.GL_CLAMP_TO_EDGE;
    protected boolean colorRenderUseAlpha = false;
    protected boolean generateMipMaps = true;
    protected Integer width;
    protected Integer height;

    public TextureBuilder(TextureBinder textureBinder, FilterQuality defaultFilterQuality, boolean defaultGenerateMipMaps)
    {
        this.textureBinder = textureBinder;
        this.filterQuality = defaultFilterQuality;
        this.generateMipMaps = defaultGenerateMipMaps;
    }

    public abstract T build() throws GraphicsException;

    public TextureBuilder setFilterQuality(FilterQuality filterQuality)
    {
        this.filterQuality = filterQuality;
        return this;
    }

    public TextureBuilder setsWrap(int sWrap)
    {
        this.sWrap = sWrap;
        return this;
    }

    public TextureBuilder settWrap(int tWrap)
    {
        this.tWrap = tWrap;
        return this;
    }

    public TextureBuilder setColorRenderUseAlpha(boolean colorRenderUseAlpha)
    {
        this.colorRenderUseAlpha = colorRenderUseAlpha;
        return this;
    }

    public TextureBuilder setGenerateMipMaps(boolean generateMipMaps)
    {
        this.generateMipMaps = generateMipMaps;
        return this;
    }

    public TextureBuilder setWidth(Integer width)
    {
        this.width = width;
        return this;
    }

    public TextureBuilder setHeight(Integer height)
    {
        this.height = height;
        return this;
    }
}
