package com.pheiffware.lib.examples.andGraphics;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.AndUtils;
import com.pheiffware.lib.and.gui.graphics.openGL.BaseGameFragment;
import com.pheiffware.lib.and.gui.graphics.openGL.SurfaceMetrics;
import com.pheiffware.lib.and.input.OrientationTracker;
import com.pheiffware.lib.geometry.DecomposedTransform3D;
import com.pheiffware.lib.geometry.Transform2D;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.light.HoloLighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.techniques.HoloColorMaterialTechnique;
import com.pheiffware.lib.graphics.managed.vertexBuffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;

/**
 * Loads a mesh using the Collada library and displays it on the screen.  Allows the camera to be adjusted using TouchTransform events. Created by Steve on 3/27/2016.
 */

public class HolographicExampleFragment extends BaseGameFragment
{
    private OrientationTracker orientationTracker;
    private float startBrightness;

    public HolographicExampleFragment()
    {
        super(new ExampleRenderer(), FilterQuality.MEDIUM, true, true);
    }

    private static class ExampleRenderer extends Base3DExampleRenderer
    {
        private static final float SCREEN_ALPHA = 0.3f;
        private OrientationTracker orientationTracker;
        private final HoloLighting lighting = new HoloLighting(new float[]{2, 2, 3, 1}, new float[]{0.7f, 0.7f, 0.7f, 1.0f}, new boolean[]{true});
        private float rotation = 0;

        private HoloColorMaterialTechnique holoColorTechnique;
        private IndexBuffer indexBuffer;
        private Matrix4 translationMatrix;
        private StaticVertexBuffer colorVertexBuffer;

        //Represents the position of the eye relative to surface of the direct center of the screen
        private final float[] eyePositionRelativeToScreen = new float[]{0, 0, 6, 1};
        private float aspectRatio;

        public ExampleRenderer()
        {
            super(90f, 1.0f, 100.0f, 0.01f);
        }

        @Override
        public void onSurfaceCreated(AssetLoader al, GLCache glCache, SurfaceMetrics surfaceMetrics) throws GraphicsException
        {
            super.onSurfaceCreated(al, glCache, surfaceMetrics);
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

                //Extract the translation aspect of the transform
                DecomposedTransform3D decomposedTransform = monkey.getInitialMatrix().decompose();
                translationMatrix = decomposedTransform.getTranslation();

                indexBuffer = new IndexBuffer(false);
                indexBuffer.allocate(mesh.getNumIndices());
                indexBuffer.putIndices(mesh.getVertexIndices());
                indexBuffer.transfer();

                colorVertexBuffer = new StaticVertexBuffer(new VertexAttribute[]{VertexAttribute.POSITION, VertexAttribute.NORMAL});
                colorVertexBuffer.allocate(mesh.getNumVertices());
                colorVertexBuffer.putVertexAttributes(mesh, 0);
                colorVertexBuffer.transfer();
            }
            catch (IOException | XMLParseException e)
            {
                throw new GraphicsException(e);
            }
        }


        @Override
        protected void onDrawFrame(Matrix4 projectionMatrix, Matrix4 viewMatrix) throws GraphicsException
        {
            Matrix4 orientationMatrix = orientationTracker.getCurrentOrientation();


            if (orientationMatrix != null)
            {
                float[] eyePosition = orientationMatrix.transform4DFloatVector(eyePositionRelativeToScreen);

                lighting.calcOnLightPositionsInEyeSpace(orientationMatrix);
//                float[] vals = new float[3];
//                SensorManager.getOrientation(orientationMatrix.m, vals);
//                Log.i("sensor", Math.toDegrees(vals[0]) + " , " + Math.toDegrees(vals[1]) + " , " + Math.toDegrees(vals[2]));

                holoColorTechnique.bind();
                colorVertexBuffer.bind(holoColorTechnique);
                translationMatrix = Matrix4.newTranslation(0.3f, 0.3f, 0.2f);
                Matrix4 modelMatrix = Matrix4.multiply(translationMatrix, Matrix4.newRotate(rotation, 1, 1, 0), Matrix4.newScale(0.2f, 0.2f, 0.2f));

                holoColorTechnique.setProperty(RenderProperty.MODEL_MATRIX, modelMatrix);
                holoColorTechnique.setProperty(RenderProperty.AMBIENT_LIGHT_COLOR, new float[]{0.2f, 0.2f, 0.2f, 1.0f});

                holoColorTechnique.setProperty(RenderProperty.LIGHTING, lighting);

                holoColorTechnique.setProperty(RenderProperty.MAT_COLOR, new float[]{0.0f, 0.6f, 0.9f, 1.0f});
                holoColorTechnique.setProperty(RenderProperty.SPEC_MAT_COLOR, new float[]{0.75f, 0.85f, 1.0f, 1.0f});
                holoColorTechnique.setProperty(RenderProperty.SHININESS, 30.0f);
                holoColorTechnique.setProperty(RenderProperty.HOLO_PROJECTION, new HoloColorMaterialTechnique.HoloData(eyePosition, 0.1f, 10f, aspectRatio, new float[]{0.5f, 0.5f, 0.5f, SCREEN_ALPHA}));
                holoColorTechnique.applyProperties();

                indexBuffer.drawAll(GLES20.GL_TRIANGLES);

                translationMatrix = Matrix4.newTranslation(-0.3f, 0.0f, -0.3f);
                modelMatrix = Matrix4.multiply(translationMatrix, Matrix4.newRotate(rotation, 1, 1, 0), Matrix4.newScale(0.2f, 0.2f, 0.2f));

                holoColorTechnique.setProperty(RenderProperty.MODEL_MATRIX, modelMatrix);
                holoColorTechnique.applyProperties();

                indexBuffer.drawAll(GLES20.GL_TRIANGLES);

                translationMatrix = Matrix4.newTranslation(0.0f, -0.2f, -0.1f);
                modelMatrix = Matrix4.multiply(translationMatrix, Matrix4.newRotate(rotation, 1, 1, 0), Matrix4.newScale(0.2f, 0.2f, 0.2f));

                holoColorTechnique.setProperty(RenderProperty.MODEL_MATRIX, modelMatrix);
                holoColorTechnique.applyProperties();

                indexBuffer.drawAll(GLES20.GL_TRIANGLES);
                //rotation++;
            }
        }

        @Override
        public void touchTransformEvent(int numPointers, Transform2D transform)
        {
            if (numPointers == 1)
            {
                //Scale distance of eye from the screen
                eyePositionRelativeToScreen[2] += transform.translation.x / 100.0f;
            }
        }

        @Override
        public void onSurfaceResize(int width, int height)
        {
            GLES20.glViewport(0, 0, width, height);
            aspectRatio = width / (float) height;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        orientationTracker = new OrientationTracker(true);
        startBrightness = AndUtils.getBrightness(getActivity().getWindow());
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onSensorChanged(SensorEvent event)
    {
        super.onSensorChanged(event);
        orientationTracker.onSensorChanged(event);
        Matrix4 currentOrientation = orientationTracker.getCurrentOrientation();
        float[] vals = new float[]{0, 0, 1, 0};
        float[] result = currentOrientation.transform4DFloatVector(vals);
        float cosTheta = vals[0] * result[0] + vals[1] * result[1] + vals[2] * result[2];
        AndUtils.setBrightness(getActivity().getWindow(), Math.min(0.1f / cosTheta, 1.0f));
    }
}