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
import com.pheiffware.lib.graphics.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.managed.collada.Collada;
import com.pheiffware.lib.utils.dom.XMLParseException;
import com.pheiffware.lib.graphics.utils.MathUtils;
import com.pheiffware.lib.meshLegacy.MeshLegacy;

import java.util.Map;

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
    private Map<String, MeshLegacy> meshes;
    private float[] projectionMatrix;

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
        // Wait for vertical retrace
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        try
        {
            testProgram = manGL.getProgram("testProgram3D", "shaders/test_vertex_mnc.glsl", "shaders/test_fragment_mnc.glsl");
            Collada collada = new Collada();
            collada.loadCollada(manGL.getAssetManager(), "meshes/test_blend.dae");
        }
        catch (FatalGraphicsException | XMLParseException exception)
        {
            FatalErrorHandler.handleFatalError(exception);
        }
//        MeshLegacy meshLegacy = meshes.get("cube");
//        float[] colors = meshLegacy.generateMultiColorValues();
//        pb = new IndexBuffer(meshLegacy.getNumPrimitives());
//        pb.putIndices(meshLegacy.primitiveIndices);
//        pb.transfer();
//
//        // @formatter:off
//        sb = new StaticVertexBuffer(testProgram, meshLegacy.getNumVertices(),
//                new String[]
//                        {"vertexPosition", "vertexNormal", "vertexColor"});
//        // @formatter:on
//
//        sb.putFloats("vertexPosition", meshLegacy.vertices);
//        sb.putFloats("vertexNormal", meshLegacy.normals);
//        sb.putFloats("vertexColor", colors);
//
//        sb.transfer();

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
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//        GLES20.glUseProgram(testProgram.getHandle());
//        float[] matrix = MathUtils.createTranslationMatrix(0, 0, -2);
//        matrix = MathUtils.multiplyMatrix(projectionMatrix, matrix);
//        GLES20.glUniformMatrix4fv(
//                GLES20.glGetUniformLocation(testProgram.getHandle(), "transformViewMatrix"),
//                1, false, matrix, 0);
//        sb.bind();
//        pb.drawAll(GLES20.GL_TRIANGLES);
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
