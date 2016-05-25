package com.pheiffware.lib.graphics.managed.texture;

/**
 * An interface for managing binding for textures to texture units.  Textures can request being bound to texture units either explicitly or automatically.  If chosen automatically,
 * this can attempt to optimize the priority of which textures are bound.
 * <p/>
 * Created by Steve on 5/18/2016.
 */
public interface TextureBindingStrategy
{
    void accessed(Texture texture);

    int chooseTextureUnitIndex(Texture texture);
}
