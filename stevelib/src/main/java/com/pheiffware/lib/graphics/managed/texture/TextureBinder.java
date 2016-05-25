package com.pheiffware.lib.graphics.managed.texture;

/**
 * TODO: Comment me!
 * <p/>
 * Created by Steve on 5/17/2016.
 */
public class TextureBinder
{
    private final Texture[] boundTextures;
    private final TextureBindingStrategy textureBindingStrategy;

    public TextureBinder(int numTextureUnits, TextureBindingStrategy textureBindingStrategy)
    {
        Texture nullTexture = new Texture(0, 0, null);
        boundTextures = new Texture[numTextureUnits];
        for (int i = 0; i < boundTextures.length; i++)
        {
            boundTextures[i] = nullTexture;
        }
        this.textureBindingStrategy = textureBindingStrategy;
    }

    public final void bindToTextureUnit(Texture texture, int textureUnitIndex)
    {
        if (texture.boundTextureUnitIndex == -1)
        {
            boundTextures[textureUnitIndex].boundTextureUnitIndex = -1;
            boundTextures[textureUnitIndex] = texture;
            texture.boundTextureUnitIndex = textureUnitIndex;
            texture.bind(textureUnitIndex);
        }
        else
        {
            textureBindingStrategy.accessed(texture);
        }
    }

    public final void bindToTextureUnit(Texture texture)
    {
        if (texture.boundTextureUnitIndex == -1)
        {
            int textureUnitIndex = textureBindingStrategy.chooseTextureUnitIndex(texture);
            boundTextures[textureUnitIndex].boundTextureUnitIndex = -1;
            boundTextures[textureUnitIndex] = texture;
            texture.boundTextureUnitIndex = textureUnitIndex;
            texture.bind(textureUnitIndex);
        }
        else
        {
            textureBindingStrategy.accessed(texture);
        }
    }
}
