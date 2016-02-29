/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.examples.andGraphics;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.pheiffware.lib.fatalError.FatalErrorHandler;
import com.pheiffware.lib.graphics.FatalGraphicsException;
import com.pheiffware.lib.graphics.GColor;
import com.pheiffware.lib.graphics.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.managed.collada.Collada;
import com.pheiffware.lib.graphics.managed.collada.ColladaFactory;
import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.mesh.Object3D;
import com.pheiffware.lib.utils.dom.XMLParseException;
import com.pheiffware.lib.graphics.utils.MathUtils;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 *
 */
public class TestRenderer4 implements Renderer
{
    private final ManGL manGL;
    private Program testProgram;
    private IndexBuffer pb;
    private StaticVertexBuffer sb;
    private float[] projectionMatrix;
    private ColladaFactory colladaFactory;
    private Collada collada;

    public TestRenderer4(ManGL manGL)
    {
        this.manGL = manGL;
    }

    @Override
    public void onSurfaceCreated(GL10 gl,
                                 javax.microedition.khronos.egl.EGLConfig config)
    {
        Log.i("OPENGL", "Surface created");
        FatalErrorHandler.installUncaughtExceptionHandler();
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        //Must enable depth testing!
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        try
        {
            testProgram = manGL.getProgram("testProgram3D", "shaders/vert_mncl.glsl", "shaders/frag_mncl.glsl");
            colladaFactory = new ColladaFactory();
            collada = colladaFactory.loadCollada(manGL.getAssetManager(), "meshes/test_render.dae");
        }
        catch (FatalGraphicsException | XMLParseException exception)
        {
            FatalErrorHandler.handleFatalError(exception);
        }

        //Lookup material from loaded file by "name" (what user named it in editing tool)
        Material material = collada.materialsByName.get("renderMaterial");
        //Lookup object from loaded file by "name" (what user named it in editing tool)
        Object3D sphere = collada.objects.get("Sphere");
        Object3D cube = collada.objects.get("Cube");
        Object3D monkey = collada.objects.get("Monkey");

        //From a given object get all meshes which should be rendered with the given material (in this case there is only one mesh which uses the single material defined in the file).
        List<Mesh> meshList = sphere.getMeshGroup().getMeshes(material);
        Mesh sphereMesh = meshList.get(0);


        pb = new IndexBuffer(sphereMesh.getNumVertexIndices());
        pb.putIndices(sphereMesh.vertexIndices);
        pb.transfer();

        // @formatter:off
        sb = new StaticVertexBuffer(testProgram, sphereMesh.getNumUniqueVertices(),
                new String[]
                        {"vertexPosition", "vertexNormal", "vertexColor"});
        // @formatter:on

        //TODO: Convert collada mesh positions and normals to homogeneous coords.
        float[] p = new float[4 * sphereMesh.getNumUniqueVertices()];
        int src = 0;
        int dest = 0;
        for (int i = 0; i < sphereMesh.getNumUniqueVertices(); i++)
        {
            p[dest++] = sphereMesh.uniqueVertexData.get("POSITION")[src++];
            p[dest++] = sphereMesh.uniqueVertexData.get("POSITION")[src++];
            p[dest++] = sphereMesh.uniqueVertexData.get("POSITION")[src++];
            p[dest++] = 1;
        }
        float[] n = new float[4 * sphereMesh.getNumUniqueVertices()];
        src = 0;
        dest = 0;
        for (int i = 0; i < sphereMesh.getNumUniqueVertices(); i++)
        {
            n[dest++] = sphereMesh.uniqueVertexData.get("NORMAL")[src++];
            n[dest++] = sphereMesh.uniqueVertexData.get("NORMAL")[src++];
            n[dest++] = sphereMesh.uniqueVertexData.get("NORMAL")[src++];
            n[dest++] = 0;
        }

        sb.putAttributeFloats("vertexPosition", p);
        sb.putAttributeFloats("vertexNormal", n);
        sb.putAttributeFloats("vertexColor", sphereMesh.generateSingleColorData(new GColor(0.0f, 0.6f, 0.9f, 1.0f)));

        sb.transfer();

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
        //Translate sphere in negative z direction so we can see it
        float[] transformMatrix = MathUtils.createTranslationMatrix(1, 1, -3);

        testProgram.setUniformMatrix4("projectionMatrix", projectionMatrix, false);
        testProgram.setUniformMatrix4("transformMatrix", transformMatrix, false);
        testProgram.setUniformVec4("eyePosition", new float[]{0, 0, 0, 1});
        testProgram.setUniformVec4("ambientColorIntensity", new float[]{0.2f, 0.2f, 0.2f, 1.0f});
        testProgram.setUniformVec4("lightColorIntensity", new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        testProgram.setUniformFloat("shininess", 30.0f);
        testProgram.setUniformVec4("lightPosition", new float[]{-2, 2, 1, 1});
        sb.bind();
        pb.drawAll(GLES20.GL_TRIANGLES);
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
        projectionMatrix = MathUtils.generateProjectionMatrix(60.0f, width
                / (float) height, 1, 10, false);
    }
}
