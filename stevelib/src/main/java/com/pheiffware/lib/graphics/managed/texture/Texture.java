package com.pheiffware.lib.graphics.managed.texture;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.managed.frameBuffer.RenderTarget;
import com.pheiffware.lib.graphics.utils.TextureUtils;

/**
 * Wraps a GL texture handle.  When after construction, the texture will be bound.
 * <p>
 * Allows binding to samplers to be managed automatically.
 * <p/>
 * Created by Steve on 2/13/2016.
 */
public abstract class Texture implements RenderTarget
{
    //The type of the texture such as GL_TEXTURE_2D
    private final int type;

    //The openGL handle to this texture
    final int handle;

    //When attaching this to a FrameBuffer, use this mip-level as the attachment point
    int attachmentLevel = 0;

    //Reference back to the central texture manager.  This is what actually assigns textures to texture units.
    private final TextureBinder textureBinder;

    //The texture unit this texture is bound to or -1 if currently unbound.  This could be held by the texture manager, but this is easier/more efficient than keeping a HashMap there.
    int boundTextureUnitIndex = -1;

    //A priority associated with this texture, in terms of desirability to keep it bound to a texture unit.  This could be held by the texture manager, but this is easier/more efficient than keeping a HashMap there.
    double texturePriority = 0;

    public Texture(int type, TextureBinder textureBinder)
    {
        this.handle = TextureUtils.genTexture();
        this.type = type;
        this.textureBinder = textureBinder;
        GLES20.glBindTexture(type, handle);
    }

    /**
     * Automatically binds the texture to a texture unit.  This cannot be mixed with calls to manualBind() on any texture.
     */
    public final int autoBind()
    {
        textureBinder.autoBindTexture(this);
        return boundTextureUnitIndex;
    }

    /**
     * Binds this texture to the given texture unit.  This CANNOT be mixed with calls to autoBind() on any texture.
     *
     * @param textureUnitIndex texture unit to bind to.
     */
    public final void manualBind(int textureUnitIndex)
    {
        TextureUtils.bindTextureToSampler(handle, textureUnitIndex, type);
    }

    public void setAttachmentLevel(int attachmentLevel)
    {
        this.attachmentLevel = attachmentLevel;
    }


}
