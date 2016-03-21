/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.examples.andGraphics;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.pheiffware.lib.fatalError.FatalErrorHandler;
import com.pheiffware.lib.geometry.DecomposedTransform3D;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.GColor;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
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
import com.pheiffware.lib.utils.dom.XMLParseException;

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
    private float rotation = 0;
    private Matrix4 projectionMatrix = Matrix4.newZeroMatrix();
    private Matrix4 translationMatrix;
    private Matrix3 normalTransform = Matrix3.newZeroMatrix();

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
            ColladaFactory colladaFactory = new ColladaFactory(true);
            InputStream inputStream = manGL.getAssetManager().open("meshes/test_render.dae");
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
            sb.putAttributeFloats("vertexColor", mesh.generateSingleColorData(new GColor(0.0f, 0.6f, 0.9f, 1.0f)));

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

        Matrix4 transformMatrix = Matrix4.multiply(translationMatrix, Matrix4.newRotate(rotation, 1, 1, 0), Matrix4.newScale(1f, 2f, 1f));

        //Test decomposing/recomposing matrix
        DecomposedTransform3D decomposedTransform = transformMatrix.decompose();
        transformMatrix = decomposedTransform.compose();

        normalTransform.setNormalTransformFromMatrix4Fast(transformMatrix);

        rotation++;

        testProgram.setUniformMatrix4("projectionMatrix", projectionMatrix.m, false);
        testProgram.setUniformMatrix4("transformMatrix", transformMatrix.m, false);
        testProgram.setUniformMatrix3("normalMatrix", normalTransform.m, false);
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
        projectionMatrix.setProjection(90f, width / (float) height, 1, 10, false);
    }
}
