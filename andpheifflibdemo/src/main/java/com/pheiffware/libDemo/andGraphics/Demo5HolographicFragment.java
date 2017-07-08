package com.pheiffware.libDemo.andGraphics;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.AndUtils;
import com.pheiffware.lib.and.graphics.AndGraphicsUtils;
import com.pheiffware.lib.and.gui.graphics.openGL.BaseGameFragment;
import com.pheiffware.lib.and.gui.graphics.openGL.GameView;
import com.pheiffware.lib.and.gui.graphics.openGL.SystemInfo;
import com.pheiffware.lib.and.input.OrientationTracker;
import com.pheiffware.lib.and.input.TouchAnalyzer;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.Camera;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.engine.MeshDataManager;
import com.pheiffware.lib.graphics.managed.engine.MeshHandle;
import com.pheiffware.lib.graphics.managed.light.HoloLighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.techniques.HoloColorMaterialTechnique;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;
import com.pheiffware.libDemo.Demo3DRendererBase;

import java.io.IOException;

/**
 * Loads a mesh using the Collada library and displays it on the screen.  Allows the camera to be adjusted using TouchTransform events. Created by Steve on 3/27/2016.
 */

public class Demo5HolographicFragment extends BaseGameFragment
{
    @Override
    public GameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new GameView(getContext(), new RendererBase(), FilterQuality.MEDIUM, true, true)
        {
            private OrientationTracker orientationTracker = new OrientationTracker(true);

            @Override
            public void onSensorChanged(SensorEvent event)
            {
                super.onSensorChanged(event);
                switch (event.sensor.getType())
                {
                    case Sensor.TYPE_ROTATION_VECTOR:

                        orientationTracker.onSensorChanged(event);
                        Matrix4 currentOrientation = orientationTracker.getCurrentOrientation();
                        float[] vals = new float[]{0, 0, 1, 0};
                        float[] result = currentOrientation.transform4DFloatVector(vals);
                        float cosTheta = vals[0] * result[0] + vals[1] * result[1] + vals[2] * result[2];
                        AndUtils.setBrightness(getActivity().getWindow(), Math.min(0.1f / cosTheta, 1.0f));
                        break;
                }
            }
        };
    }

    private static class RendererBase extends Demo3DRendererBase
    {
        private static final float SCREEN_ALPHA = 0.3f;
        private OrientationTracker orientationTracker;
        private final HoloLighting lighting = new HoloLighting(new float[]{0.2f, 0.2f, 0.2f, 1.0f}, new float[]{2, 2, 3, 1}, new float[]{0.7f, 0.7f, 0.7f, 1.0f}, new boolean[]{true});
        private float rotation = 0;

        private HoloColorMaterialTechnique holoColorTechnique;
        private MeshDataManager manager;

        //Represents the position of the eye relative to surface of the direct center of the screen if screen is flat.
        private final float[] eyePositionInFlatScreenSpace = new float[]{0, 0, 6, 1};
        private float aspectRatio;
        private MeshHandle monkeyHandle;
        private MeshHandle monkeyHandle2;
        private MeshHandle monkeyHandle3;
        private Matrix4 transform3;

        public RendererBase()
        {
            super(AndGraphicsUtils.GL_VERSION_30, AndGraphicsUtils.GL_VERSION_30, 90f, 1.0f, 100.0f, 0.01f);
        }

        @Override
        public void onSurfaceCreated(AssetLoader al, GLCache glCache, SystemInfo systemInfo) throws GraphicsException
        {
            super.onSurfaceCreated(al, glCache, systemInfo);
            PheiffGLUtils.enableAlphaTransparency();
            orientationTracker = new OrientationTracker(true);
            GLES20.glClearColor(0.5f * SCREEN_ALPHA, 0.5f * SCREEN_ALPHA, 0.5f * SCREEN_ALPHA, 1.0f);

            PheiffGLUtils.enableAlphaTransparency();
            holoColorTechnique = new HoloColorMaterialTechnique(al);
            ColladaFactory colladaFactory = new ColladaFactory(true);
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
                        holoColorTechnique,
                        new RenderPropertyValue[]
                                {
                                        new RenderPropertyValue(RenderProperty.MAT_COLOR, new float[]{0f, 1f, 1f, 1f}),
                                        new RenderPropertyValue(RenderProperty.SPEC_MAT_COLOR, new float[]{1f, 1f, 1f, 1f}),
                                        new RenderPropertyValue(RenderProperty.SHININESS, 100f)
                                });
                monkeyHandle2 = monkeyHandle.copy();
                monkeyHandle3 = monkeyHandle.copy();
                manager.packAndTransfer();

                Matrix4 transform = Matrix4.newTranslation(0.3f, 0.3f, 0.2f);
                transform.scaleBy(0.2f, 0.2f, 0.2f);
                monkeyHandle.setProperty(RenderProperty.MODEL_MATRIX, transform);

                transform = Matrix4.newTranslation(-0.3f, -0.3f, 0.2f);
                transform.scaleBy(0.2f, 0.2f, 0.2f);
                monkeyHandle2.setProperty(RenderProperty.MODEL_MATRIX, transform);

                transform3 = Matrix4.newTranslation(0.3f, -0.3f, 0.2f);

                //Flipping z, but must also flip another axis, or cull-face will get us (this is equivalent to rotating).
                transform3.scaleBy(0.2f, -0.2f, -0.2f);
                monkeyHandle3.setProperty(RenderProperty.MODEL_MATRIX, transform3);
            }
            catch (IOException | XMLParseException e)
            {
                throw new GraphicsException(e);
            }
        }


        @Override
        protected void onDrawFrame(Camera camera) throws GraphicsException
        {
            GLES20.glViewport(0, 0, getSurfaceWidth(), getSurfaceHeight());
            aspectRatio = getSurfaceWidth() / (float) getSurfaceHeight();

            Matrix4 orientationMatrix = orientationTracker.getCurrentOrientation();
            if (orientationMatrix != null)
            {
                holoColorTechnique.setProperty(RenderProperty.HOLO_PROJECTION, new HoloColorMaterialTechnique.HoloData(orientationMatrix, eyePositionInFlatScreenSpace, 0.1f, 10f, aspectRatio, new float[]{0.5f, 0.5f, 0.5f, SCREEN_ALPHA}));
                holoColorTechnique.setProperty(RenderProperty.LIGHTING, lighting);
                holoColorTechnique.setProperty(RenderProperty.MAT_COLOR, new float[]{0.0f, 0.6f, 0.9f, 1.0f});
                holoColorTechnique.setProperty(RenderProperty.SPEC_MAT_COLOR, new float[]{0.75f, 0.85f, 1.0f, 1.0f});
                holoColorTechnique.applyConstantProperties();

                monkeyHandle.drawTriangles();
                monkeyHandle2.drawTriangles();

                transform3.rotateBy(1, 0, 0, 1);
                monkeyHandle3.setProperty(RenderProperty.MODEL_MATRIX, transform3);
                monkeyHandle3.drawTriangles();
            }
        }

        @Override
        public void onTouchTransformEvent(TouchAnalyzer.TouchTransformEvent event)
        {
            if (event.numPointers == 1)
            {
                //Scale distance of eye from the screen
                eyePositionInFlatScreenSpace[2] += event.transform.translation.x / 100.0f;
            }
        }


        @Override
        public void onSensorChanged(SensorEvent event)
        {
            switch (event.sensor.getType())
            {
                case Sensor.TYPE_ROTATION_VECTOR:
                    orientationTracker.onSensorChanged(event);
                    break;
            }
        }
    }


}