package com.pheiffware.libDemo.andGraphics;

import android.hardware.Sensor;
import android.opengl.GLES20;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.graphics.AndGraphicsUtils;
import com.pheiffware.lib.and.gui.graphics.openGL.BaseGameFragment;
import com.pheiffware.lib.and.gui.graphics.openGL.GameView;
import com.pheiffware.lib.and.gui.graphics.openGL.SystemInfo;
import com.pheiffware.lib.and.input.OrientationTracker;
import com.pheiffware.lib.and.input.TouchAnalyzer;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.EuclideanCamera;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.Projection;
import com.pheiffware.lib.graphics.Vec4F;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.engine.MeshDataManager;
import com.pheiffware.lib.graphics.managed.engine.MeshHandle;
import com.pheiffware.lib.graphics.managed.frameBuffer.FrameBuffer;
import com.pheiffware.lib.graphics.managed.light.HoloLighting;
import com.pheiffware.lib.graphics.managed.program.GraphicsConfig;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.techniques.Std3DTechnique;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;
import com.pheiffware.libDemo.Demo3DRenderer;

import java.io.IOException;

/**
 * Loads a mesh using the Collada library and displays it on the screen.  Allows the camera to be adjusted using TouchTransform events. Created by Steve on 3/27/2016.
 */

public class Demo5HolographicFragment extends BaseGameFragment
{
    @Override
    public GameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new GameView(getContext(), new Renderer(), FilterQuality.MEDIUM, true, true);
    }

    private static class Renderer extends Demo3DRenderer
    {
        private static final float SCREEN_ALPHA = 0.3f;
        private OrientationTracker orientationTracker;
        private final HoloLighting lighting = new HoloLighting(new float[]{0.2f, 0.2f, 0.2f, 1.0f}, new float[]{2, 2, 3, 1}, new float[]{0.7f, 0.7f, 0.7f, 1.0f}, new boolean[]{true});

        private Std3DTechnique colorTechnique;
        private MeshDataManager manager;

        //Represents the position of the eye relative to surface of the direct center of the screen if screen is flat.
        private final Vec4F eyePosition = new Vec4F(0, 0, 4, 1);
        private MeshHandle monkeyHandle;
        private MeshHandle monkeyHandle2;
        private MeshHandle monkeyHandle3;
        private Matrix4 transform3;

        public Renderer()
        {
            super(AndGraphicsUtils.GL_VERSION_30, AndGraphicsUtils.GL_VERSION_30, "shaders", 90f, 1.0f, 100.0f, 0.01f);
        }

        @Override
        public void onSurfaceCreated(AssetLoader al, GLCache glCache, SystemInfo systemInfo) throws GraphicsException
        {
            super.onSurfaceCreated(al, glCache, systemInfo);
            PheiffGLUtils.enableAlphaTransparency();
            orientationTracker = new OrientationTracker(true);
            GLES20.glClearColor(0.5f * SCREEN_ALPHA, 0.5f * SCREEN_ALPHA, 0.5f * SCREEN_ALPHA, 1.0f);

            PheiffGLUtils.enableAlphaTransparency();

            colorTechnique = glCache.buildTechnique(Std3DTechnique.class, GraphicsConfig.TEXTURED_MATERIAL, false);
            ColladaFactory colladaFactory = new ColladaFactory();
            try
            {
                Collada collada = colladaFactory.loadCollada(al, "meshes/test_render.dae");

                //Lookup object from loaded file by "name" (what user named it in editing tool)
                ColladaObject3D monkey = collada.objects.get("Monkey");

                //From a given object get all meshes which should be rendered with the given material (in this case there is only one mesh which uses the single material defined in the file).
                Mesh mesh = monkey.getMesh(0);

                manager = new MeshDataManager();
                monkeyHandle = manager.addStaticMesh(
                        mesh,
                        colorTechnique,
                        new RenderPropertyValue[]
                                {
                                        new RenderPropertyValue(RenderProperty.MAT_COLOR, new float[]{0f, 1f, 1f, 1f}),
                                        new RenderPropertyValue(RenderProperty.SPEC_MAT_COLOR, new float[]{1f, 1f, 1f, 1f}),
                                        new RenderPropertyValue(RenderProperty.SHININESS, 100f)
                                });
                monkeyHandle2 = monkeyHandle.copy();
                monkeyHandle3 = monkeyHandle.copy();
                manager.packAndTransfer();

                Matrix4 transform = Matrix4.newTranslation(0.15f, 0.15f, 0.1f);
                transform.scaleBy(0.1f, 0.1f, 0.1f);
                monkeyHandle.setProperty(RenderProperty.MODEL_MATRIX, transform);

                transform = Matrix4.newTranslation(-0.15f, -0.15f, 0.05f);
                transform.scaleBy(0.1f, 0.1f, 0.1f);
                monkeyHandle2.setProperty(RenderProperty.MODEL_MATRIX, transform);

                transform3 = Matrix4.newTranslation(0.15f, -0.15f, 0);

                //Flipping z, but must also flip another axis, or cull-face will get us (this is equivalent to rotating).
                transform3.scaleBy(0.1f, -0.1f, -0.1f);
                monkeyHandle3.setProperty(RenderProperty.MODEL_MATRIX, transform3);
            }
            catch (IOException | XMLParseException e)
            {
                throw new GraphicsException(e);
            }
        }

        private static class HoloCamera
        {
            private static final float screenHeight = 1;
            Matrix4 viewMatrix;
            Matrix4 projectionMatrix;

            /**
             * @param eyeX
             * @param eyeY
             * @param eyeZ
             * @param orientationMatrix
             * @param aspect            ratio of width to height
             */
            HoloCamera(float eyeX, float eyeY, float eyeZ, Matrix4 orientationMatrix, float aspect)
            {
                float near = 1.0f;
                float far = 100.0f;
                Vec4F screenCenter = new Vec4F(1);
                screenCenter.setX(-eyeX);
                screenCenter.setY(-eyeY);
                screenCenter.setZ(-eyeZ);
                screenCenter.setW(1);
                screenCenter.transformBy(orientationMatrix);
                viewMatrix = Matrix4.newTranslation(screenCenter.x(), screenCenter.y(), screenCenter.z());

                float screenWidth = aspect * screenHeight;

                //Where the center of the screen will be projected.  All projected (x,y) screen coordinates should be offset by this
                float projectedCenterX = screenCenter.x() / -screenCenter.z();
                float projectedCenterY = screenCenter.y() / -screenCenter.z();

                //Once screen coordinates are offset, they should be scaled based on projected screen dimensions
                //We want to scale coordinates to ranges y: [-1,1], x: [-aspect, aspect]
                float halfProjectedWidth = screenWidth / (2 * -screenCenter.z());
                float halfProjectedHeight = screenHeight / (2 * -screenCenter.z());

                //xProjected = (x/z - projectedCenterX) / projectedWidth
                //xProjected = (x/z) / projectedWidth - projectedCenterX / projectedWidth
                //xProjected = (x/z) * xScale + xOffset

                //Scale by inverse of 1/2 width
                float xScale = 1 / halfProjectedWidth;

                //Offset is positive, because it will be multiplied by z, but then divided by -z
                float xOffset = projectedCenterX / halfProjectedWidth;

                //Scale by inverse of 1/2 height
                float yScale = 1 / halfProjectedHeight;

                //Offset is positive, because it will be multiplied by z, but then divided by -z
                float yOffset = projectedCenterY / halfProjectedHeight;

                float[] m = new float[16];
                //@formatter:off
                m[0] = xScale;  m[4] = 0;       m[8] =  xOffset;                        m[12] = 0;
                m[1] = 0;       m[5] = yScale;  m[9] =  yOffset;                        m[13] = 0;
                m[2] = 0;       m[6] = 0;       m[10] = -(far + near) / (far - near);   m[14] = -2 * far * near / (far - near);
                m[3] = 0;       m[7] = 0;       m[11] = -1;                             m[15] = 0;
                //@formatter:on
                projectionMatrix = Matrix4.newFromFloats(m);
            }
        }

        @Override
        protected void onDrawFrame(Projection projection, EuclideanCamera camera) throws GraphicsException
        {
            Matrix4 orientationMatrix = orientationTracker.getCurrentOrientation();
            if (orientationMatrix != null)
            {
                //Bind main frame buffer
                FrameBuffer.main.bind(0, 0, getSurfaceWidth(), getSurfaceHeight());
                GLES20.glViewport(0, 0, getSurfaceWidth(), getSurfaceHeight());
                HoloCamera holoCamera = new HoloCamera(eyePosition.x(), eyePosition.y(), eyePosition.z(), orientationMatrix, ((float) getSurfaceWidth() / getSurfaceHeight()));

                colorTechnique.setProperty(RenderProperty.PROJECTION_MATRIX, holoCamera.projectionMatrix);
                colorTechnique.setProperty(RenderProperty.VIEW_MATRIX, holoCamera.viewMatrix);
                colorTechnique.setProperty(RenderProperty.LIGHTING, lighting);
                colorTechnique.setProperty(RenderProperty.MAT_COLOR, new float[]{0.0f, 0.6f, 0.9f, 1.0f});
                colorTechnique.setProperty(RenderProperty.SPEC_MAT_COLOR, new float[]{0.75f, 0.85f, 1.0f, 1.0f});
                colorTechnique.setProperty(RenderProperty.DEPTH_Z_CONST, 1.0f);
                colorTechnique.setProperty(RenderProperty.DEPTH_Z_FACTOR, 1.0f);
                colorTechnique.applyConstantProperties();

                monkeyHandle.drawTriangles();
                monkeyHandle2.drawTriangles();

                transform3.rotateBy(1, 0, 0, 1);
                monkeyHandle3.setProperty(RenderProperty.MODEL_MATRIX, transform3);
                monkeyHandle3.drawTriangles();
            }
        }

        @Override
        public void onSurfaceResize(int width, int height)
        {
            super.onSurfaceResize(width, height);
        }

        @Override
        public void onTouchTransformEvent(TouchAnalyzer.TouchTransformEvent event)
        {
            if (event.numPointers == 1)
            {
                //Scale distance of eye from the screen
                eyePosition.setZ((float) (eyePosition.z() + event.transform.translation.x / 100.0f));
            }
        }

        @Override
        protected void onSensorChanged(int type, float[] values, long timestamp)
        {
            switch (type)
            {
                case Sensor.TYPE_ROTATION_VECTOR:
                    orientationTracker.onSensorChanged(values);
                    break;
            }
        }
    }

}