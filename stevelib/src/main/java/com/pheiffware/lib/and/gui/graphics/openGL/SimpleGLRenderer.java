package com.pheiffware.lib.and.gui.graphics.openGL;

import android.content.res.AssetManager;

import com.pheiffware.lib.and.touch.TouchTransformListener;
import com.pheiffware.lib.graphics.managed.ManGL;

/**
 * Wraps the GLSurfaceView.Renderer concepts.  The ManGL object manages/simplifies many aspects of OpenGL. This triggers TouchTransformListener events, in the rendering thread.
 */
public interface SimpleGLRenderer extends TouchTransformListener
{
    /**
     * Will receive a new manGL object and an asset manager. All data should be loaded using am. DO NOT RETAIN REFERENCE TO THIS as it could keep the entire view/fragment/activity
     * surrounding it from being deallocated.  This is especially true when the containing fragment's setRetainInstance(true) method was called.
     * <p/>
     * All gl resources should be created/recreated.
     *
     * @param am    asset manager, DO NOT RETAIN REFERENCE
     * @param manGL managed opengl object
     */
    void onSurfaceCreated(AssetManager am, ManGL manGL);

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


    /**
     * The maximum, major version of openGL which your renderer has been coded to support.  This prevents having your code break on later versions of devices if newer incompatible
     * versions of openGL become available.  The version made available to the renderer will always be <=maxGLVersion depending on hardware capabilities.  This should be a number
     * like: 1, 2 or 3.
     * <p/>
     * The actual version available to you is specified as part of the ManGL object provided when the surface is created.  This can be checked against constants such as
     * GL_VERSION_31.
     */
    int maxMajorGLVersion();
}