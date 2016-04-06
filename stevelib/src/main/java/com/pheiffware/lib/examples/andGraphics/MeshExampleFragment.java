package com.pheiffware.lib.examples.andGraphics;

import android.content.res.AssetManager;
import android.opengl.GLES20;

import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLFragment;
import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLRenderer;
import com.pheiffware.lib.fatalError.FatalErrorHandler;
import com.pheiffware.lib.geometry.DecomposedTransform3D;
import com.pheiffware.lib.geometry.Transform2D;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.graphics.Camera;
import com.pheiffware.lib.graphics.Color4F;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.mesh.Object3D;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Loads a mesh using the Collada library and displays it on the screen.  Allows the camera to be adjusted using TouchTransform events. Created by Steve on 3/27/2016.
 */

public class MeshExampleFragment extends SimpleGLFragment
{
    public MeshExampleFragment()
    {
        super(new ExampleRenderer(), FilterQuality.MEDIUM);
    }

    private static class ExampleRenderer implements SimpleGLRenderer
    {
        private static final double SCREEN_DRAG_TO_CAMERA_TRANSLATION = 0.01f;
        private ManGL manGL;
        private IndexBuffer pb;
        private StaticVertexBuffer sb;
        private float rotation = 0;
        private Matrix4 translationMatrix;
        private Matrix3 normalTransform = Matrix3.newZeroMatrix();
        private Camera camera = new Camera(90f, 1, 1, 100, false);

        @Override
        public int maxMajorGLVersion()
        {
            return 3;
        }

        @Override
        public void onSurfaceCreated(AssetManager am, ManGL manGL)
        {
            try
            {
                this.manGL = manGL;
                FatalErrorHandler.installUncaughtExceptionHandler();
                GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
                //Must enable depth testing!
                GLES20.glEnable(GLES20.GL_DEPTH_TEST);
                Program testProgram = manGL.createProgram(am, "testProgram3D", "shaders/vert_mncl.glsl", "shaders/frag_mncl.glsl");
                ColladaFactory colladaFactory = new ColladaFactory(true);
                InputStream inputStream = am.open("meshes/test_render.dae");
                Collada collada = colladaFactory.loadCollada(inputStream);

                //Lookup material from loaded file by "name" (what user named it in editing tool)
                Material material = collada.materialsByName.get("renderMaterial");

                //Lookup object from loaded file by "name" (what user named it in editing tool)
//            Object3D sphere = collada.objects.get("Sphere");
//            Object3D cube = collada.objects.get("Cube");
                Object3D monkey = collada.objects.get("Monkey");

                //From a given object get all meshes which should be rendered with the given material (in this case there is only one mesh which uses the single material defined in the file).
                List<Mesh> meshList = monkey.getMeshGroup().getMeshes(material);
                Mesh mesh = meshList.get(0);

                //Extract the translation aspect of the transform
                DecomposedTransform3D decomposedTransform = monkey.getMatrix().decompose();
                translationMatrix = decomposedTransform.getTranslation();

                pb = new IndexBuffer(mesh.getNumVertexIndices());
                pb.putIndices(mesh.vertexIndices);
                pb.transfer();

                // @formatter:off
            sb = new StaticVertexBuffer(testProgram, mesh.getNumUniqueVertices(),
                    new String[]
                            {"vertexPosition", "vertexNormal", "vertexColor"});
            // @formatter:on

                sb.putAttributeFloats("vertexPosition", mesh.getPositionData());
                sb.putAttributeFloats("vertexNormal", mesh.getNormalData());
                sb.putAttributeFloats("vertexColor", mesh.generateSingleColorData(new Color4F(0.0f, 0.6f, 0.9f, 1.0f)));

                sb.transfer();
                PheiffGLUtils.assertNoError();
            }
            catch (GraphicsException | XMLParseException | IOException exception)
            {
                FatalErrorHandler.handleFatalError(exception);
            }
        }

        @Override
        public void onDrawFrame()
        {
            try
            {
                Program testProgram = manGL.getProgram("testProgram3D");

                //Default view volume is based on sitting at origin and looking in negative z direction
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
                testProgram.bind();

                Matrix4 modelMatrix = Matrix4.multiply(translationMatrix, Matrix4.newRotate(rotation, 1, 1, 0), Matrix4.newScale(1f, 2f, 1f));

                Matrix4 viewModelMatrix;
                viewModelMatrix = new Matrix4(camera.getCameraMatrix());
                viewModelMatrix.multiplyBy(modelMatrix);
                normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);

                Matrix4 projectionMatrix = camera.getProjectionMatrix();


                testProgram.setUniformMatrix4("projectionMatrix", projectionMatrix.m, false);
                testProgram.setUniformMatrix4("transformMatrix", viewModelMatrix.m, false);
                testProgram.setUniformMatrix3("normalMatrix", normalTransform.m, false);
                testProgram.setUniformVec4("ambientColorIntensity", new float[]{0.2f, 0.2f, 0.2f, 1.0f});
                testProgram.setUniformVec4("lightColorIntensity", new float[]{1.0f, 1.0f, 1.0f, 1.0f});
                testProgram.setUniformFloat("shininess", 30.0f);
                testProgram.setUniformVec3("lightPosition", new float[]{-3, 3, 0});

                sb.bind();
                pb.drawAll(GLES20.GL_TRIANGLES);
                PheiffGLUtils.assertNoError();
            }
            catch (GraphicsException e)
            {
                FatalErrorHandler.handleFatalError(e);
            }
            rotation++;
        }

        @Override
        public void onSurfaceResize(int width, int height)
        {
            GLES20.glViewport(0, 0, width, height);
            camera.setAspect(width / (float) height);
        }

        /**
         * Must be called in renderer thread
         *
         * @param numPointers
         * @param transform   The transform generated by the last pointer motion event.
         */
        @Override
        public void touchTransformEvent(int numPointers, Transform2D transform)
        {
            if (numPointers > 2)
            {
                camera.zoom((float) transform.scale.x);
            }
            else if (numPointers > 1)
            {
                camera.roll((float) (180 * transform.rotation / Math.PI));
                camera.rotateScreenInputVector((float) transform.translation.x, (float) -transform.translation.y);
            }
            else
            {
                float cameraX = (float) (transform.translation.x * SCREEN_DRAG_TO_CAMERA_TRANSLATION);
                float cameraZ = (float) (transform.translation.y * SCREEN_DRAG_TO_CAMERA_TRANSLATION);
                camera.translateScreen(cameraX, 0, cameraZ);
            }
        }

    }
}