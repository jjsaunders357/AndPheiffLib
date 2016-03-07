/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.examples.andGraphics;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.pheiffware.lib.fatalError.FatalErrorHandler;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.GColor;
import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.mesh.Object3D;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;
import com.pheiffware.lib.graphics.utils.Transform;
import com.pheiffware.lib.utils.dom.XMLParseException;
import com.pheiffware.lib.graphics.utils.MathUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 *
 */
public class ExampleMeshRenderer implements Renderer
{
    private final ManGL manGL;
    private Program testProgram;
    private IndexBuffer pb;
    private StaticVertexBuffer sb;
    private float[] projectionMatrix;
    private ColladaFactory colladaFactory;
    private Collada collada;
    private Object3D sphere;
    private Object3D cube;
    private Object3D monkey;
    private float rotation = 0;
    private float[] translationMatrix;

    public ExampleMeshRenderer(ManGL manGL)
    {
        this.manGL = manGL;
    }

    @Override
    public void onSurfaceCreated(GL10 gl,
                                 javax.microedition.khronos.egl.EGLConfig config)
    {
        try
        {
            Log.i("OPENGL", "Surface created");
            FatalErrorHandler.installUncaughtExceptionHandler();
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            //Must enable depth testing!
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            testProgram = manGL.getProgram("testProgram3D", "shaders/vert_mncl.glsl", "shaders/frag_mncl.glsl");
            colladaFactory = new ColladaFactory(true);
            InputStream inputStream = null;
            inputStream = manGL.getAssetManager().open("meshes/test_render.dae");
            collada = colladaFactory.loadCollada(inputStream);

            //Lookup material from loaded file by "name" (what user named it in editing tool)
            Material material = collada.materialsByName.get("renderMaterial");

            //Lookup object from loaded file by "name" (what user named it in editing tool)
            sphere = collada.objects.get("Sphere");
            cube = collada.objects.get("Cube");
            monkey = collada.objects.get("Monkey");

            //From a given object get all meshes which should be rendered with the given material (in this case there is only one mesh which uses the single material defined in the file).
            List<Mesh> meshList = monkey.getMeshGroup().getMeshes(material);
            Mesh sphereMesh = meshList.get(0);

            //Extract the translation aspect of the transform
            Transform transform = new Transform(monkey.getMatrix());
            translationMatrix = transform.getTranslation();

            pb = new IndexBuffer(sphereMesh.getNumVertexIndices());
            pb.putIndices(sphereMesh.vertexIndices);
            pb.transfer();

            // @formatter:off
            sb = new StaticVertexBuffer(testProgram, sphereMesh.getNumUniqueVertices(),
                    new String[]
                            {"vertexPosition", "vertexNormal", "vertexColor"});
            // @formatter:on

            sb.putAttributeFloats("vertexPosition", sphereMesh.uniqueVertexData.get("POSITION"));
            sb.putAttributeFloats("vertexNormal", sphereMesh.uniqueVertexData.get("NORMAL"));
            sb.putAttributeFloats("vertexColor", sphereMesh.generateSingleColorData(new GColor(0.0f, 0.6f, 0.9f, 1.0f)));

            sb.transfer();
            PheiffGLUtils.assertNoError();
        }
        catch (GraphicsException | XMLParseException | IOException exception)
        {
            FatalErrorHandler.handleFatalError(exception);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.
     * khronos.opengles.GL10)
     */
    @Override
    public void onDrawFrame(GL10 gl)
    {
        //Default view volume is based on sitting at origin and looking in negative z direction
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        testProgram.bind();

        float[] transformMatrix = MathUtils.multiplyMatrices(translationMatrix, MathUtils.createRotationMatrix(rotation, 1, 1, 0), MathUtils.createScaleMatrix(1f, 2f, 1f));
        rotation++;

        testProgram.setUniformMatrix4("projectionMatrix", projectionMatrix, false);
        testProgram.setUniformMatrix4("transformMatrix", transformMatrix, false);
        testProgram.setUniformMatrix3("normalMatrix", MathUtils.createNormalTransformMatrix(transformMatrix), false);
        testProgram.setUniformVec4("ambientColorIntensity", new float[]{0.2f, 0.2f, 0.2f, 1.0f});
        testProgram.setUniformVec4("lightColorIntensity", new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        testProgram.setUniformFloat("shininess", 30.0f);
        testProgram.setUniformVec3("lightPosition", new float[]{-3, 3, 0});

        sb.bind();
        pb.drawAll(GLES20.GL_TRIANGLES);
        try
        {
            PheiffGLUtils.assertNoError();
        }
        catch (GraphicsException e)
        {
            FatalErrorHandler.handleFatalError(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition
     * .khronos.opengles.GL10, int, int)
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        Log.i("OPENGL", "Surface changed");
        GLES20.glViewport(0, 0, width, height);
        projectionMatrix = MathUtils.createProjectionMatrix(90f, width / (float) height, 1, 10, false);
    }
}
