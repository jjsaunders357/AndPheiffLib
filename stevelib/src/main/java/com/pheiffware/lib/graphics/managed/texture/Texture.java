package com.pheiffware.lib.graphics.managed.texture;

import com.pheiffware.lib.graphics.utils.TextureUtils;

/**
 * Wraps a GL texture handle.  This allows conveniences for binding textures to samplers. This can be done in a managed way which automatically chooses how samplers are bound or
 * manually. Created by Steve on 2/13/2016.
 */
public class Texture
{
    //The type of the texture such as GL_TEXTURE_2D
    final int type;

    //The openGL handler to this texture
    final int handle;

    //Reference back to the central texture manager.  This is what actually assigns samplers if the manualBind() method is called.
    final TextureBinder textureBinder;

    //The samplerIndex this texture was last bound to or -1 if currently unbound.  This could be held by the texture manager, but this is easier/more efficient than keeping a HashMap there.
    int boundTextureUnitIndex = -1;

    //A priority associate with this texture, in terms of desirability to keep it bound to a sampler.  This could be held by the texture manager, but this is easier/more efficient than keeping a HashMap there.
    double texturePriority = 0;

    public Texture(int type, int handle, TextureBinder textureBinder)
    {
        this.type = type;
        this.handle = handle;
        this.textureBinder = textureBinder;
    }

    public final int getHandle()
    {
        return handle;
    }

    /**
     * Binds the texture to a texture unit chosen automatically.
     */
    public final int autoBind()
    {
        textureBinder.bindToTextureUnit(this);
        return boundTextureUnitIndex;
    }

    /**
     * Binds this texture to the specified texture unit in a what which is compatible with the automated binding process.
     *
     * @param textureUnitIndex
     */
    public final void manualBind(int textureUnitIndex)
    {
        textureBinder.bindToTextureUnit(this, textureUnitIndex);
    }

    /**
     * This unmanaged method makes the OpenGL calls required to bind this texture to the given texture unit.  This CANNOT be mixed with calls to managed methods for sampler
     * binding.
     *
     * @param textureUnitIndex
     */
    public final void bind(int textureUnitIndex)
    {
        TextureUtils.bindTextureToSampler(handle, textureUnitIndex, type);
    }


    public final int getBoundTextureUnitIndex()
    {
        return boundTextureUnitIndex;
    }
}
