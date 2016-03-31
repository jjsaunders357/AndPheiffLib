package com.pheiffware.lib.and.fragments.graphics;

import com.pheiffware.lib.and.touch.TouchTransformListener;
import com.pheiffware.lib.graphics.managed.ManGL;

/**
 * Wraps the GLSurfaceView.Renderer concepts.  The ManGL object manages/simplifies many aspects of OpenGL.
 * This triggers TouchTransformListener events, in the rendering thread.
 */
public interface SimpleGLRenderer extends TouchTransformListener
{
    /**
     * Will receive a new manGL object.  All gl resources should be recreated/reloaded.
     *
     * @param manGL managed opengl object
     */
    void onSurfaceCreated(ManGL manGL);

    /**
     * Called when surface changes size.
     *
     * @param width
     * @param height
     */
    void onSurfaceResize(int width, int height);

    /**
     * Called when its time to render a new frame.
     */
    void onDrawFrame();
}