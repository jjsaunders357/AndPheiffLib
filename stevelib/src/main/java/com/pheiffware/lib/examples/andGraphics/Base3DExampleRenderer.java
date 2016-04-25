package com.pheiffware.lib.examples.andGraphics;

import android.opengl.GLES20;
import android.util.Log;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.gui.graphics.openGL.TouchViewRenderer;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.utils.MapCounterLong;

import java.util.Map;

/**
 * Base class used by 3D examples.  Does some basic graphics setup and also profiles rendering speed.
 * <p/>
 * Created by Steve on 4/23/2016.
 */
public abstract class Base3DExampleRenderer extends TouchViewRenderer
{
    private long startFrameTimeStamp;
    private final MapCounterLong<String> nanoTimes = new MapCounterLong<>();
    private int frameCounter;
    private int logFramePeriod = 100;

    public Base3DExampleRenderer(float initialFOV, float nearPlane, float farPlane, double screenDragToCameraTranslation)
    {
        super(initialFOV, nearPlane, farPlane, screenDragToCameraTranslation);
    }

    @Override
    public void onSurfaceCreated(AssetLoader al, GLCache GLCache) throws GraphicsException
    {
        frameCounter = 0;
        nanoTimes.clear();
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glClearDepthf(1);
    }

    @Override
    public void onDrawFrame() throws GraphicsException
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        startFrameTimeStamp = System.nanoTime();
        super.onDrawFrame();
        GLES20.glFinish();
        frameCounter++;
        logAverages();
        addFrameProfilePoint("Render");
    }

    private void logAverages()
    {
        if (frameCounter % logFramePeriod == 0)
        {
            for (Map.Entry<String, Long> entry : nanoTimes.entrySet())
            {
                Log.i("profile", entry.getKey() + ": " + (0.000000001 * entry.getValue() / frameCounter));
            }
        }
    }

    protected final void addFrameProfilePoint(String key)
    {
        long endTime = System.nanoTime();
        nanoTimes.addCount(key, endTime - startFrameTimeStamp);
        startFrameTimeStamp = System.nanoTime();
    }

    @Override
    public int maxMajorGLVersion()
    {
        return 3;
    }
}
