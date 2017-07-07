package com.pheiffware.lib.graphics.managed.texture;

/**
 * Implementing classes manage texture unit binding.  They are signalled every time the texture in a texture unit is accessed and are queried when a texture, which is not already
 * bound to a texture unit, needs to be bound.
 * <p/>
 * Created by Steve on 5/18/2016.
 */
public interface TextureBindingStrategy
{
    /**
     * Called every time the bound texture is accessed.  This should be used to determine the importance/value of the bound texture.
     *
     * @param boundTexture a currently bound texture which is being accessed
     */
    void accessed(Texture boundTexture);

    /**
     * Called when a given unbound texture needs to be bound.  This should return the "best" texture unit to use.
     *
     * @param unboundTexture an unbound texture which needs to be bound to a texture unit.
     * @return the texture unit to bind to
     */
    int getBestTextureUnitIndex(Texture unboundTexture);
}
