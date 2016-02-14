/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.examples.andGraphics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.pheiffware.lib.fatalError.FatalErrorHandler;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.managed.Texture;
import com.pheiffware.lib.graphics.utils.MathUtils;
import com.pheiffware.lib.graphics.FatalGraphicsException;
import com.pheiffware.lib.graphics.buffer.CombinedVertexBuffer;
import com.pheiffware.lib.graphics.buffer.IndexBuffer;

/**
 *
 */
public class TestRenderer1 implements Renderer
{
    private final ManGL manGL;
    private Program testProgram;
    private IndexBuffer pb;
    private CombinedVertexBuffer cb;
    private float globalTestColor = 0.0f;
    private float[] projectionMatrix;
    private Texture faceTexture;

    public TestRenderer1(ManGL manGL)
    {
        this.manGL = manGL;
    }

    /* (non-Javadoc)
     * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Log.i("OPENGL", "Surface created");
        FatalErrorHandler.installUncaughtExceptionHandler();
        // Wait for vertical retrace
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        try
        {
            testProgram = manGL.getProgram("testProgram", "shaders/test_vertex_matrix_texture_color.glsl", "shaders/test_fragment_matrix_texture_color.glsl");
            System.out.println(testProgram);
            faceTexture = manGL.getImageTexture("images/face.png", true, FilterQuality.MEDIUM, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
        } catch (FatalGraphicsException exception)
        {
            FatalErrorHandler.handleFatalError(exception);
        }

        pb = new IndexBuffer(2000);

        float x = 1f, y = 1f, z = 1.1f;
        //@formatter:off
        cb = new CombinedVertexBuffer(testProgram, 2000,
                new String[] { "vertexPosition", "vertexTexCoord" },
                new String[]{"vertexColor"});
        //@formatter:on

        cb.putStaticVec4(-x, -y, -z, 1);
        cb.putStaticVec2(0, 1);

        cb.putStaticVec4(-x, y, -z, 1);
        cb.putStaticVec2(0, 0);

        cb.putStaticVec4(x, y, -z, 1);
        cb.putStaticVec2(1, 0);

        cb.putStaticVec4(-x, -y, -z, 1);
        cb.putStaticVec2(0, 1);

        cb.putStaticVec4(x, y, -z, 1);
        cb.putStaticVec2(1, 0);

        cb.putStaticVec4(x, -y, -z, 1);
        cb.putStaticVec2(1, 1);
        cb.transferStatic();

        pb.putIndex(0);
        pb.putIndex(1);
        pb.putIndex(2);
        pb.putIndex(3);
        pb.putIndex(4);
        pb.putIndex(5);
        pb.transfer();

    }

    /* (non-Javadoc)
     * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
     */
    @Override
    public void onDrawFrame(GL10 gl)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(testProgram.getHandle());
        testProgram.setUniformMatrix4("transformViewMatrix", projectionMatrix);
        testProgram.setUniformTexture2D("texture", faceTexture, 0);
        cb.putDynamicVec4(0, globalTestColor, 0, 0, 0);
        cb.putDynamicVec4(0, 0, globalTestColor, 0, 0);
        cb.putDynamicVec4(0, 0, 0, globalTestColor, 0);
        cb.putDynamicVec4(0, globalTestColor, 0, 0, 0);
        cb.putDynamicVec4(0, 0, 0, globalTestColor, 0);
        cb.putDynamicVec4(0, 0, 0, 0, 0);
        cb.transferDynamic();
        cb.bind();

        pb.draw(6, GLES20.GL_TRIANGLES);
        globalTestColor += 0.01;
    }

    /* (non-Javadoc)
     * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        Log.i("OPENGL", "Surface changed");
        GLES20.glViewport(0, 0, width, height);
        projectionMatrix = MathUtils.generateProjectionMatrix(60.0f, width / (float) height, 1, 10, false);
    }
}
