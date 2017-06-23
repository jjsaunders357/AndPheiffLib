package com.pheiffware.lib.graphics.managed.texture;

/**
 * Manages automated/manual managed binding of textures.  Automated binding allows a texture unit to be chosen automatically, for a given texture.  If the texture is already bound
 * no work is done.
 * <p/>
 * Created by Steve on 5/17/2016.
 */
public class TextureBinder
{
    //A map from texture units to textures which are bound to them.
    private final Texture[] boundTextures;

    //The strategy used to manage the binding of textures to texture units.
    private final TextureBindingStrategy textureBindingStrategy;

    public TextureBinder(int numTextureUnits, TextureBindingStrategy textureBindingStrategy)
    {
        Texture nullTexture = new Texture(0, null)
        {
            @Override
            public void attach(int attachmentPoint)
            {
                throw new UnsupportedOperationException("Cannot attache nullTexture");
            }
        };
        boundTextures = new Texture[numTextureUnits];
        for (int i = 0; i < boundTextures.length; i++)
        {
            boundTextures[i] = nullTexture;
        }
        this.textureBindingStrategy = textureBindingStrategy;
    }


    /**
     * Automatically chooses a texture unit to bind this texture to and binds it (if not already bound).  Unbinds any previously bound texture if necessary.
     *
     * @param texture
     */
    public final void autoBindTexture(Texture texture)
    {
        if (texture.boundTextureUnitIndex == -1)
        {
            int textureUnitIndex = textureBindingStrategy.getBestTextureUnitIndex(texture);
            boundTextures[textureUnitIndex].boundTextureUnitIndex = -1;
            boundTextures[textureUnitIndex] = texture;
            texture.boundTextureUnitIndex = textureUnitIndex;
            texture.manualBind(textureUnitIndex);
        }
        else
        {
            textureBindingStrategy.accessed(texture);
        }
    }
}
