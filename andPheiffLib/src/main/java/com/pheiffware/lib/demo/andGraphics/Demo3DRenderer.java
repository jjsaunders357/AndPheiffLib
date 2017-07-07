package com.pheiffware.lib.demo.andGraphics;

import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.util.Log;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.gui.graphics.openGL.GameRenderer;
import com.pheiffware.lib.and.gui.graphics.openGL.SurfaceMetrics;
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
public abstract class Demo3DRenderer implements GameRenderer
{
    //How far a move of a pointer on the screen scales to a translation of the camera
    private final double screenDragToCameraTranslation;
    private final Camera camera;
    private long profileStartTime;
    private final MapCounterLong<String> nanoTimes = new MapCounterLong<>();
    private int frameCounter;
    private int logFramePeriod = 120;
    private TouchAnalyzer touchAnalyzer;
    private int renderWidth;
    private int renderHeight;

    public Demo3DRenderer(float initialFOV, float nearPlane, float farPlane, double screenDragToCameraTranslation)
    {
        this.screenDragToCameraTranslation = screenDragToCameraTranslation;
        camera = new Camera(initialFOV, 1, nearPlane, farPlane, false);
    }

    @Override
    public void onSurfaceCreated(AssetLoader al, GLCache glCache, SurfaceMetrics surfaceMetrics) throws GraphicsException
    {
        touchAnalyzer = new TouchAnalyzer(surfaceMetrics.xdpi, surfaceMetrics.ydpi);
        frameCounter = 0;
        nanoTimes.clear();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
    }

    @Override
    public void onSurfaceResize(int width, int height)
    {
        this.renderWidth = width;
        this.renderHeight = height;
        //For renderers which never change from the main FrameBuffer, this keeps the viewport working
        //Any renderer which does change frame buffer will be changing the viewport manually before rendering anyway and will not be affected by this
        GLES20.glViewport(0, 0, renderWidth, renderHeight);
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
    public int maxMajorGLVersion()
    {
        return 3;
    }

    public void touchTransformEvent(int numPointers, Transform2D transform)
    {
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

    public int getRenderWidth()
    {
        return renderWidth;
    }

    public int getRenderHeight()
    {
        return renderHeight;
    }
}
