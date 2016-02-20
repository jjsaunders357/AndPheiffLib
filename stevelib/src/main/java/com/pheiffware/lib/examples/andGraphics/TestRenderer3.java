/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.examples.andGraphics;

import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.utils.MathUtils;
import com.pheiffware.lib.graphics.FatalGraphicsException;
import com.pheiffware.lib.graphics.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.buffer.StaticVertexBuffer;
import com.pheiffware.lib.meshLegacy.MeshLegacy;
import com.pheiffware.lib.fatalError.FatalErrorHandler;

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
        // Wait for vertical retrace
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        try
        {
            testProgram = manGL.getProgram("testProgram3D", "shaders/vert_mnc.glsl", "shaders/frag_mnc.glsl");
            meshes = MeshLegacy.loadMeshesLegacy(manGL.getAssetManager(), "meshes/spheres.mesh");
        } catch (FatalGraphicsException exception)
        {
            FatalErrorHandler.handleFatalError(exception);
        }
        MeshLegacy sphereMeshLegacy = meshes.get("sphere4");
        float[] colors = sphereMeshLegacy.generateMultiColorValues();
        pb = new IndexBuffer(sphereMeshLegacy.getNumPrimitives());
        pb.putIndices(sphereMeshLegacy.primitiveIndices);
        pb.transfer();

        // @formatter:off
        sb = new StaticVertexBuffer(testProgram, sphereMeshLegacy.getNumVertices(),
                new String[]
                        {"vertexPosition", "vertexNormal", "vertexColor"});
        // @formatter:on

        sb.putFloats("vertexPosition", sphereMeshLegacy.vertices);
        sb.putFloats("vertexNormal", sphereMeshLegacy.normals);
        sb.putFloats("vertexColor", colors);

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
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(testProgram.getHandle());
        float[] matrix = MathUtils.createTranslationMatrix(0, 0, -2);
        matrix = MathUtils.multiplyMatrix(projectionMatrix, matrix);
        GLES20.glUniformMatrix4fv(
                GLES20.glGetUniformLocation(testProgram.getHandle(), "transformViewMatrix"),
                1, false, matrix, 0);
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
