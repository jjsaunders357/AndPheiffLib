package com.pheiffware.lib.and.gui.graphics.openGL;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.input.TouchTransformListener;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.GLCache;

/**
 * Wraps the GLSurfaceView.Renderer concepts.  The GLCache object manages/simplifies many aspects of OpenGL. This triggers TouchTransformListener events, in the rendering thread.
 */
public interface SimpleGLRenderer extends TouchTransformListener
{
    /**
     * Will receive a new GLCache object and an asset manager. All data should be loaded using am. DO NOT RETAIN REFERENCE TO THIS as it could keep the entire view/fragment/activity
     * surrounding it from being deallocated.  This is especially true when the containing fragment's setRetainInstance(true) method was called.
     * <p/>
     * All gl resources should be created/recreated.
     *  @param al    asset manager, DO NOT RETAIN REFERENCE
     * @param glCache managed opengl object
     */
    void onSurfaceCreated(AssetLoader al, GLCache glCache) throws GraphicsException;

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
    void onDrawFrame() throws GraphicsException;


    /**
     * The maximum, major version of openGL which your renderer has been coded to support.  This prevents having your code break on later versions of devices if newer incompatible
     * versions of openGL become available.  The version made available to the renderer will always be <=maxGLVersion depending on hardware capabilities.  This should be a number
     * like: 1, 2 or 3.
     * <p/>
     * The actual version available to you is specified as part of the GLCache object provided when the surface is created.  This can be checked against constants such as
     * GL_VERSION_31.
     */
    int maxMajorGLVersion();

    /**
     * If this returns true, then the renderer will receive touchTransformEvents.
     * @return
     */
    boolean receivesTouchTransformEvents();

    /**
     * If this returns true, then the renderer will receive orientationChanged events.
     * @return
     */
    boolean receivesOrientationEvents();
}