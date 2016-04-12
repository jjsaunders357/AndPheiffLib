package com.pheiffware.lib.examples.andGraphics;

import android.content.res.AssetManager;
import android.opengl.GLES20;

import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLFragment;
import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLRenderer;
import com.pheiffware.lib.fatalError.FatalErrorHandler;
import com.pheiffware.lib.geometry.Transform2D;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.managed.Texture;
import com.pheiffware.lib.graphics.managed.buffer.CombinedVertexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;

/**
 * Example of using a CombinedBuffer for storing some vertex attributes statically and other dynamically.  In this case, vertices are static and colors are dynamically updated.
 * Created by Steve on 3/27/2016.
 */
public class CombinedVertexBufferExampleFragment extends SimpleGLFragment
{
    public CombinedVertexBufferExampleFragment()
    {
        super(new CombinedVertexBufferExampleRenderer(), FilterQuality.MEDIUM);
    }

    private static class CombinedVertexBufferExampleRenderer implements SimpleGLRenderer
    {
        private Program testProgram;
        private IndexBuffer pb;
        private CombinedVertexBuffer cb;
        private float globalTestColor = 0.0f;
        private Matrix4 projectionMatrix;
        private Texture faceTexture;

        @Override
        public int maxMajorGLVersion()
        {
            return 3;
        }

        /* (non-Javadoc)
         * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
         */
        @Override
        public void onSurfaceCreated(AssetManager am, ManGL manGL)
        {
            FatalErrorHandler.installUncaughtExceptionHandler();
            // Wait for vertical retrace
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

            try
            {
                testProgram = manGL.createProgram(am, "testProgram", "shaders/vert_mtc.glsl", "shaders/frag_mtc.glsl");
                faceTexture = manGL.createImageTexture(am, "images/face.png", true, FilterQuality.MEDIUM, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
            }
            catch (GraphicsException exception)
            {
                FatalErrorHandler.handleFatalError(exception);
            }

            pb = new IndexBuffer(false);
            pb.allocate(2000);

            float x = 1f, y = 1f, z = 1.1f;
            //@formatter:off
            cb = new CombinedVertexBuffer(testProgram,
                    new String[] { "vertexPosition", "vertexTexCoord" },
                    new String[] { "vertexColor" });
            //@formatter:on
            cb.allocate(2000);

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
        public void onDrawFrame()
        {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            GLES20.glUseProgram(testProgram.getHandle());
            testProgram.setUniformMatrix4("transformViewMatrix", projectionMatrix.m);
            testProgram.setUniformTexture2D("texture", faceTexture, 0);
            cb.putDynamicVec4(0, globalTestColor, 0, 0, 0);
            cb.putDynamicVec4(0, 0, globalTestColor, 0, 0);
            cb.putDynamicVec4(0, 0, 0, globalTestColor, 0);
            cb.putDynamicVec4(0, globalTestColor, 0, 0, 0);
            cb.putDynamicVec4(0, 0, 0, globalTestColor, 0);
            cb.putDynamicVec4(0, 0, 0, 0, 0);
            cb.transferDynamic();
            cb.bind();

            pb.draw(GLES20.GL_TRIANGLES, 0, 6);
            globalTestColor += 0.01;
        }


        /* (non-Javadoc)
         * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
         */
        @Override
        public void onSurfaceResize(int width, int height)
        {
            GLES20.glViewport(0, 0, width, height);
            projectionMatrix = Matrix4.newProjection(120.0f, width / (float) height, 1, 10, false);
        }

        /**
         * Must be called in renderer thread
         *
         * @param numPointers
         * @param transform   The tranform generated by the last pointer motion event.
         */
        @Override
        public void touchTransformEvent(int numPointers, Transform2D transform)
        {
        }
    }
}
