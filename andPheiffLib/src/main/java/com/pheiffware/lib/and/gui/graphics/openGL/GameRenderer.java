package com.pheiffware.lib.and.gui.graphics.openGL;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.input.TouchAnalyzer;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.GLCache;

import java.util.Map;

/**
 * Wraps the GLSurfaceView.Renderer concepts.  The GLCache object manages/simplifies many aspects of OpenGL. This triggers TouchTransformListener events, in the rendering thread.
 */
public abstract class GameRenderer
{
    private final int minSupportedGLVersion;
    private final int maxSupportedGLVersion;
    private final String rootShaderPath;

    //The asset loader object for this renderer.
    private AssetLoader al;

    //The glCache object
    private GLCache glCache;
    private int surfaceWidth;
    private int surfaceHeight;
    private SystemInfo systemInfo;

    /**
     * @param minSupportedGLVersion The minimum, version of openGL, which your renderer has been coded to support.  If this version is not available,
     *                              this will fail gracefully.  This should a major/minor version number such as AndGraphicsUtils.GL_VERSION_31
     * @param maxSupportedGLVersion The maximum, major version of openGL, which your renderer has been coded to support.  This prevents your code breaking
     *                              on later versions of devices if newer incompatible versions of openGL become available.  The version made
     *                              available to the renderer will always be <=maxSupportedGLVersion depending on hardware capabilities.
     *                              This should a major/minor version number such as AndGraphicsUtils.GL_VERSION_31
     */
    public GameRenderer(int minSupportedGLVersion, int maxSupportedGLVersion, String rootShaderPath)
    {
        this.minSupportedGLVersion = minSupportedGLVersion;
        this.maxSupportedGLVersion = maxSupportedGLVersion;
        this.rootShaderPath = rootShaderPath;
    }

    /**
     * Sets up the AssetLoader and GLCache for the renderer and calls its initialize() method.
     *
     * @param al
     * @param deviceGLVersion
     * @param defaultFilterQuality
     * @param systemInfo
     * @throws GraphicsException
     */
    void onSurfaceCreated(AssetLoader al, int deviceGLVersion, FilterQuality defaultFilterQuality, Map<String, Object> graphicsSystemConfig, SystemInfo systemInfo) throws GraphicsException
    {
        this.al = al;
        glCache = new GLCache(al, deviceGLVersion, graphicsSystemConfig, defaultFilterQuality, rootShaderPath);
        this.systemInfo = systemInfo;
        onSurfaceCreated(al, glCache, systemInfo);
    }

    /**
     * Called when the surface is created, giving several key resources and pieces of information for initial loading/setup.
     *
     * @param al         use this asset loader to load assets
     * @param glCache    graphics cache used for loading graphics resources (this is automatically cleaned up when surface is destroyed)
     * @param systemInfo information about the system
     * @throws GraphicsException
     */
    protected abstract void onSurfaceCreated(AssetLoader al, GLCache glCache, SystemInfo systemInfo) throws GraphicsException;

    /**
     * Cleans up resources in the glCache and any addition tear down necessary for this renderer.
     */
    protected void onSurfaceDestroyed()
    {
        glCache.destroy();
    }

    /**
     * Called when the dimensions of the surface being rendered on changes.
     *
     * @param width
     * @param height
     */
    public void onSurfaceResize(int width, int height)
    {
        this.surfaceWidth = width;
        this.surfaceHeight = height;
    }

    /**
     * Called when its time to render a new frame.
     */
    public abstract void onDrawFrame() throws GraphicsException;


    /**
     * If the surrounding view is initialized to forward touch events this is called in the rendering thread whenever a transform event happens.
     *
     * @param event
     */
    public void onTouchTransformEvent(TouchAnalyzer.TouchTransformEvent event)
    {

    }

    /**
     * If the surrounding view is initialized to forward touch events this is called in the rendering thread whenever a tap event happens.
     *
     * @param event
     */
    public void onTouchTapEvent(TouchAnalyzer.TouchTapEvent event)
    {

    }


    /**
     * All data should be loaded using this AssetLoader
     */
    protected AssetLoader getAL()
    {
        return al;
    }

    /**
     * GL resources should be allocated through this interface.  It is critical vertex buffers are allocated this way,
     * so they can have backing memory forcibly deallocated.
     *
     * @return
     */
    protected GLCache getGlCache()
    {
        return glCache;
    }

    protected int getSurfaceWidth()
    {
        return surfaceWidth;
    }

    protected int getSurfaceHeight()
    {
        return surfaceHeight;
    }

    /**
     * If the surrounding view is initialized to forward one or more types of sensor events this is called in the rendering thread whenever a sensor event happens.
     * <p>
     * Note: parameters are copied from the original event, for thread safety reasons.
     *
     * @param type      the type of sensor (example: Sensor.TYPE_ROTATION_VECTOR)
     * @param values    the sensor's values
     * @param timestamp when the event occurred
     */
    protected void onSensorChanged(int type, float[] values, long timestamp)
    {

    }

    /**
     * The minimum, major version of openGL, which your renderer has been coded to support.  If this version is not available,
     * this will fail gracefully.
     *
     * @return minimum major version number supported
     */
    int getMinSupportedGLVersion()
    {
        return minSupportedGLVersion;
    }

    /**
     * The maximum, major version of openGL, which your renderer has been coded to support.  This prevents your code breaking
     * on later versions of devices if newer incompatible versions of openGL become available.  The version made
     * available to the renderer will always be <=maxSupportedGLVersion depending on hardware capabilities.  This will be a number like: 1, 2 or 3.
     *
     * @return maximum major version number supported
     */
    int getMaxSupportedGLVersion()
    {
        return maxSupportedGLVersion;
    }

}