package com.pheiffware.lib.examples.andGraphics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.util.Log;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLFragment;
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
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.techniques.HoloColorMaterialTechnique;
import com.pheiffware.lib.graphics.managed.vertexBuffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.StaticVertexBuffer;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;

/**
 * Loads a mesh using the Collada library and displays it on the screen.  Allows the camera to be adjusted using TouchTransform events. Created by Steve on 3/27/2016.
 */

public class HolographicExampleFragment extends SimpleGLFragment
{
    private final ExampleRenderer renderer;

    public HolographicExampleFragment()
    {
        this(new ExampleRenderer(), FilterQuality.MEDIUM);
    }

    //This can only be called internally, who cares?
    @SuppressLint("ValidFragment")
    private HolographicExampleFragment(ExampleRenderer renderer, FilterQuality filterQuality)
    {
        super(renderer, filterQuality);
        this.renderer = renderer;
    }

    private static class ExampleRenderer extends Base3DExampleRenderer
    {
        private OrientationTracker orientationTracker;
        private final Lighting lighting = new Lighting(new float[]{-3, -3, 1, 1, -2, 1, 1, 1}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.3f, 0.2f, 1.0f});
        private float rotation = 0;

        private HoloColorMaterialTechnique holoColorTechnique;
        private IndexBuffer indexBuffer;
        private Matrix4 translationMatrix;
        private StaticVertexBuffer colorVertexBuffer;

        //Represents the position of the eye relative to surface of the direct center of the screen
        private final float[] eyePositionRelativeToScreen = new float[]{0, 0, 3, 1};

        public ExampleRenderer()
        {
            super(90f, 1.0f, 100.0f, 0.01f);
        }

        @Override
        public void onSurfaceCreated(AssetLoader al, GLCache glCache) throws GraphicsException
        {
            super.onSurfaceCreated(al, glCache);
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
            Matrix4 orientationMatrix = orientationTracker.calcOrientation();
            if (orientationMatrix != null)
            {
                float[] values = new float[3];
//                SensorManager.getOrientation(orientationMatrix.m, values);
//                float x = (float) Math.toDegrees(values[0]);
//                float y = (float) Math.toDegrees(values[1]);
//                float z = (float) Math.toDegrees(values[2]);
//                Log.i("sensor", "orientation: (" + x + " , " + y + " , " + z + ")");

                float[] eyePosition = orientationMatrix.transform4DFloatVector(eyePositionRelativeToScreen);
                Log.i("sensor", "eyePosition: (" + eyePosition[0] + " , " + eyePosition[1] + " , " + eyePosition[2] + ")");

                //lighting.calcLightPositionsInEyeSpace(viewMatrix);
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
                holoColorTechnique.setProperty(RenderProperty.HOLO_PROJECTION, new HoloColorMaterialTechnique.HoloData(eyePosition, 1f, 30f));
                holoColorTechnique.applyProperties();

                indexBuffer.drawAll(GLES20.GL_TRIANGLES);

                translationMatrix = Matrix4.newTranslation(-0.2f, 0.0f, -0.2f);
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
                eyePositionRelativeToScreen[2] += transform.translation.x / 200.0f;
            }
//            if (numPointers > 2)
//            {
//                camera.zoom((float) transform.scale.x);
//            }
//            else if (numPointers > 1)
//            {
//                camera.roll((float) (180 * transform.rotation / Math.PI));
//                camera.rotateScreenInputVector((float) transform.translation.x, (float) -transform.translation.y);
//            }
//            else
//            {
//                float cameraX = (float) (transform.translation.x * screenDragToCameraTranslation);
//                float cameraZ = (float) (transform.translation.y * screenDragToCameraTranslation);
//                camera.translateScreen(cameraX, 0, cameraZ);
//            }
        }

        public void setOrientationTracker(OrientationTracker orientationTracker)
        {
            this.orientationTracker = orientationTracker;
        }

        public OrientationTracker getOrientationTracker()
        {
            return orientationTracker;
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        renderer.setOrientationTracker(new OrientationTracker(sensorManager, true));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        renderer.getOrientationTracker().register();
    }

    @Override
    public void onPause()
    {
        renderer.getOrientationTracker().unregister();
        super.onPause();
    }

    @Override
    public void onDetach()
    {
        renderer.setOrientationTracker(null);
        super.onDetach();
    }
}