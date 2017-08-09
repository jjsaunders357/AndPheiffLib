package com.pheiffware.lib.graphics.managed;

import com.pheiffware.lib.graphics.GraphicsException;

import java.util.Map;

/**
 * Listener for graphics configuration changes.  Graphics objects implement this and are notified on the OpenGL Surface thread
 * when configuration changes, allowing them to reconfigure as appropriate.
 * Created by Steve on 8/9/2017.
 */

public interface GraphicsConfigListener
{
    /**
     * @param graphicsSystemConfig the new system configuration state, a map of arbitrary objects
     */
    void onSystemConfigChanged(Map<String, Object> graphicsSystemConfig) throws GraphicsException;
}
