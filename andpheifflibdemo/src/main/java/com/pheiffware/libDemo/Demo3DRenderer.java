package com.pheiffware.libDemo;

import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.util.Log;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.gui.graphics.openGL.GameRenderer;
import com.pheiffware.lib.and.gui.graphics.openGL.SystemInfo;
import com.pheiffware.lib.and.input.TouchAnalyzer;
import com.pheiffware.lib.geometry.Transform2D;
import com.pheiffware.lib.graphics.Camera;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.utils.dataContainers.MapCounterLong;

import java.util.Map;

/**
 * Base class used by 3D examples.  Does some basic graphics setup, camera tracking and also profiles rendering speed.
 * <p/>
 * Created by Steve on 4/23/2016.
 */
public abstract class Demo3DRenderer extends GameRenderer
{
    //How far a move of a pointer on the screen scales to a translation of the camera
    private final double screenDragToCameraTranslation;
    private final Camera camera;
    private long profileStartTime;
    private final MapCounterLong<String> nanoTimes = new MapCounterLong<>();
    private int frameCounter;
    private int logFramePeriod = 120;

    public Demo3DRenderer(int minSupportedGLVersion, int maxSupportedGLVersion, float initialFOV, float nearPlane, float farPlane, double screenDragToCameraTranslation)
    {
        super(minSupportedGLVersion, maxSupportedGLVersion);
        this.screenDragToCameraTranslation = screenDragToCameraTranslation;
        camera = new Camera(initialFOV, 1, nearPlane, farPlane, false);
    }

    @Override
    public void onSurfaceCreated(AssetLoader al, GLCache glCache, SystemInfo systemInfo) throws GraphicsException
    {
        frameCounter = 0;
        nanoTimes.clear();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
    }

    @Override
    public void onSurfaceResize(int width, int height)
    {
        super.onSurfaceResize(width, height);
        camera.setAspect(width / (float) height);
    }

    @Override
    public void onDrawFrame() throws GraphicsException
    {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glClearDepthf(1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        profileStartTime = System.nanoTime();
        onDrawFrame(camera);
        GLES20.glFinish();
        frameCounter++;
        logAverages();
        addFrameProfilePoint("Render");
    }

    protected abstract void onDrawFrame(Camera camera) throws GraphicsException;

    private void logAverages()
    {
        if (frameCounter == logFramePeriod - 1)
        {
            for (Map.Entry<String, Long> entry : nanoTimes.entrySet())
            {
                Log.i("profile", entry.getKey() + ": " + (0.000000001 * entry.getValue() / frameCounter));
            }
            frameCounter = 0;
            nanoTimes.clear();
        }
    }

    protected final void addFrameProfilePoint(String key)
    {
        long endTime = System.nanoTime();
        nanoTimes.addCount(key, endTime - profileStartTime);
        profileStartTime = System.nanoTime();
    }

    @Override
    public void onTouchTransformEvent(TouchAnalyzer.TouchTransformEvent event)
    {
        int numPointers = event.numPointers;
        Transform2D transform = event.transform;
        if (numPointers > 2)
        {
            //Geometric average of x and y scale factors
            float scaleFactor = (float) Math.sqrt(transform.scale.x * transform.scale.y);
            camera.zoom(scaleFactor);
        }
        else if (numPointers > 1)
        {
            camera.roll((float) (180 * transform.rotation / Math.PI));
            camera.rotateScreenInputVector((float) transform.translation.x, (float) -transform.translation.y);
        }
        else
        {
            float cameraX = (float) (transform.translation.x * screenDragToCameraTranslation);
            float cameraZ = (float) (transform.translation.y * screenDragToCameraTranslation);
            camera.translateScreen(cameraX, 0, cameraZ);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {

    }
}
