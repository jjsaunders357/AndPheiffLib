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
import com.pheiffware.lib.graphics.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.utils.MathUtils;
import com.pheiffware.lib.meshLegacy.MeshLegacy;

import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

/**
 *
 */
public class TestRenderer3 implements Renderer
{
    private final ManGL manGL;
    private Program testProgram;
    private IndexBuffer pb;
    private StaticVertexBuffer sb;
    private Map<String, MeshLegacy> meshes;
    private float[] projectionMatrix;

    public TestRenderer3(ManGL manGL)
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
            meshes = MeshLegacy.loadMeshesLegacy(manGL.getAssetManager(), "meshes/spheres.mesh");

        }
        catch (GraphicsException exception)
        {
            FatalErrorHandler.handleFatalError(exception);
        }
        //MeshLegacy meshLegacy = meshes.get("cube");
        MeshLegacy meshLegacy = meshes.get("sphere4");
        float[] colors = meshLegacy.generateSingleColorValues(0.0f, 0.6f, 0.9f, 1.0f);
        pb = new IndexBuffer(meshLegacy.getNumPrimitives());
        pb.putIndices(meshLegacy.primitiveIndices);
        pb.transfer();

        // @formatter:off
        sb = new StaticVertexBuffer(testProgram, meshLegacy.getNumVertices(),
                new String[]
                        {"vertexPosition", "vertexNormal", "vertexColor"});
        // @formatter:on

        sb.putAttributeFloats("vertexPosition", meshLegacy.vertices);
        sb.putAttributeFloats("vertexNormal", meshLegacy.normals);
        sb.putAttributeFloats("vertexColor", colors);

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
        float[] transformMatrix = MathUtils.createTranslationMatrix(0, 0, -2);

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
