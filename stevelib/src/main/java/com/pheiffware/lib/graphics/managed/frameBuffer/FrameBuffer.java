package com.pheiffware.lib.graphics.managed.frameBuffer;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.utils.PheiffGLUtils;

/**
 * TODO: Comment me!
 * <p/>
 * Created by Steve on 6/3/2016.
 */
public class FrameBuffer
{
    public static final RenderTarget NULL_RENDER_TARGET = new RenderTarget()
    {
        @Override
        public void attach(FrameBuffer frameBuffer, int attachmentPoint)
        {
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, attachmentPoint, GLES20.GL_TEXTURE_2D, 0, 0);
        }
    };
    private final int handle;

    public FrameBuffer()
    {
        this(PheiffGLUtils.createFrameBuffer());
    }

    public FrameBuffer(int handle)
    {
        this.handle = handle;
    }

    public final void makeActive()
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, handle);
    }

    public final void attachColor(int colorAttachmentIndex, RenderTarget renderTarget)
    {
        if (renderTarget == null)
        {
            renderTarget = NULL_RENDER_TARGET;
        }
        renderTarget.attach(this, GLES20.GL_COLOR_ATTACHMENT0 + colorAttachmentIndex);
    }

    public final void attachDepth(RenderTarget renderTarget)
    {
        if (renderTarget == null)
        {
            renderTarget = NULL_RENDER_TARGET;
        }
        renderTarget.attach(this, GLES20.GL_DEPTH_ATTACHMENT);
    }

    public final void attachStencil(RenderTarget renderTarget)
    {
        if (renderTarget == null)
        {
            renderTarget = NULL_RENDER_TARGET;
        }
        renderTarget.attach(this, GLES20.GL_STENCIL_ATTACHMENT);
    }

    public int getHandle()
    {
        return handle;
    }
}
