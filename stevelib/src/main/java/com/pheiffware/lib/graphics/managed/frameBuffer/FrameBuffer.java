package com.pheiffware.lib.graphics.managed.frameBuffer;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.utils.PheiffGLUtils;

/**
 * Wraps an openGL frame buffer object.
 * <p/>
 * Created by Steve on 6/3/2016.
 */
public class FrameBuffer
{
    //Special null render target used to specify that an attachment point should render nowhere.
    public static final RenderTarget NULL_RENDER_TARGET = new RenderTarget()
    {
        @Override
        public void attach(int attachmentPoint)
        {
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, attachmentPoint, GLES20.GL_TEXTURE_2D, 0, 0);
        }
    };

    //Reference to the main frame buffer
    public static final FrameBuffer main = new FrameBuffer(0);

    //openGL handle to frame buffer object
    private final int handle;

    public FrameBuffer()
    {
        this(PheiffGLUtils.createFrameBuffer());
    }

    private FrameBuffer(int handle)
    {
        this.handle = handle;
    }

    /**
     * Make the frame buffer active.  This must happen before attaching.
     */
    public final void bind(int viewX, int viewY, int viewWidth, int viewHeight)
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, handle);
        GLES20.glViewport(viewX, viewY, viewWidth, viewHeight);
    }

    /**
     * Attach a color render target at the given color attachment point.
     *
     * @param colorAttachmentIndex
     * @param renderTarget
     */
    public final void attachColor(int colorAttachmentIndex, RenderTarget renderTarget)
    {
        if (renderTarget == null)
        {
            renderTarget = NULL_RENDER_TARGET;
        }
        renderTarget.attach(GLES20.GL_COLOR_ATTACHMENT0 + colorAttachmentIndex);
    }

    /**
     * Attach a depth render target.
     *
     * @param renderTarget
     */
    public final void attachDepth(RenderTarget renderTarget)
    {
        if (renderTarget == null)
        {
            renderTarget = NULL_RENDER_TARGET;
        }
        renderTarget.attach(GLES20.GL_DEPTH_ATTACHMENT);
    }

    /**
     * Attach a stencil render target.
     *
     * @param renderTarget
     */
    public final void attachStencil(RenderTarget renderTarget)
    {
        if (renderTarget == null)
        {
            renderTarget = NULL_RENDER_TARGET;
        }
        renderTarget.attach(GLES20.GL_STENCIL_ATTACHMENT);
    }
}
