package com.pheiffware.lib.examples.andGraphics;

import android.content.res.AssetManager;
import android.opengl.GLES20;

import com.pheiffware.lib.and.gui.graphics.SimpleGLFragment;
import com.pheiffware.lib.and.gui.graphics.SimpleGLRenderer;
import com.pheiffware.lib.fatalError.FatalErrorHandler;
import com.pheiffware.lib.geometry.Transform2D;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.managed.Texture;
import com.pheiffware.lib.graphics.managed.buffer.CombinedVertexBuffer;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;

/**
 * Example of rendering to a texture. Created by Steve on 3/27/2016.
 */
public class RenderToTextureExampleFragment extends SimpleGLFragment
{
    public RenderToTextureExampleFragment()
    {
        super(new RenderToTextureExampleRenderer(), FilterQuality.MEDIUM);
    }

    private static class RenderToTextureExampleRenderer implements SimpleGLRenderer
    {
        private final Matrix4 cameraProjectionMatrix = Matrix4.newProjection(140.0f, 1, 1, 10, true);
        private Matrix4 projectionMatrix;
        private Program testProgram;
        private Texture faceTexture;
        private Texture colorRenderTexture;
        private Texture depthRenderTexture;
        private CombinedVertexBuffer cb;
        private float globalTestColor = 0.0f;
        private int frameBufferHandle;
        private int viewWidth;
        private int viewHeight;

        /* (non-Javadoc)
         * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
         */
        @Override
        public void onSurfaceCreated(AssetManager am, ManGL manGL)
        {
            FatalErrorHandler.installUncaughtExceptionHandler();
            // Wait for vertical retrace
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

            try
            {
                testProgram = manGL.createProgram(am, "testProgram", "shaders/vert_mtc.glsl", "shaders/frag_mtc.glsl");
                faceTexture = manGL.createImageTexture(am, "images/face.png", true, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);

                //Creates color texture render target, without alpha channel
                colorRenderTexture = manGL.createColorRenderTexture("colorRender1", 512, 512, false, FilterQuality.MEDIUM, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);

                //Creates a depth texture render target, without alpha channel
                depthRenderTexture = manGL.createDepthRenderTexture("depthRender1", 512, 512, FilterQuality.MEDIUM, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
                frameBufferHandle = PheiffGLUtils.createFrameBuffer();
            }
            catch (GraphicsException exception)
            {
                FatalErrorHandler.handleFatalError(exception);
            }

            float x = 1f, y = 1f, z = 1.1f;
            //@formatter:off
        cb = new CombinedVertexBuffer(manGL.getProgram("testProgram"), 200,
                new String[]{"vertexPosition", "vertexTexCoord"},
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
        }

        /* (non-Javadoc)
         * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
         */
        @Override
        public void onDrawFrame()
        {
            //Set to render to texture.
            PheiffGLUtils.bindFrameBuffer(frameBufferHandle, colorRenderTexture.getHandle(), 0);
            try
            {
                PheiffGLUtils.assertFrameBufferStatus();
            }
            catch (GraphicsException e)
            {
                FatalErrorHandler.handleFatalError(e);
            }

            GLES20.glViewport(0, 0, 512, 512);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            testProgram.bind();
            testProgram.setUniformMatrix4("transformViewMatrix", cameraProjectionMatrix.m);
            testProgram.setUniformTexture2D("texture", faceTexture, 0);

            //Vertex positions and texture coordinates static.  This encodes a color to mix in.  In this case we want a pure texture render.
            cb.putDynamicVec4(0, 0, 0, 0, 0);
            cb.putDynamicVec4(0, 0, 0, 0, 0);
            cb.putDynamicVec4(0, 0, 0, 0, 0);
            cb.putDynamicVec4(0, 0, 0, 0, 0);
            cb.putDynamicVec4(0, 0, 0, 0, 0);
            cb.putDynamicVec4(0, 0, 0, 0, 0);
            cb.transferDynamic();
            cb.bind();
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
            PheiffGLUtils.bindFrameBuffer(0, -1, -1);

            GLES20.glViewport(0, 0, viewWidth, viewHeight);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            testProgram.bind();
            testProgram.setUniformMatrix4("transformViewMatrix", projectionMatrix.m);
            testProgram.setUniformTexture2D("texture", colorRenderTexture, 0);
            cb.putDynamicVec4(0, globalTestColor, 0, 0, 0);
            cb.putDynamicVec4(0, 0, globalTestColor, 0, 0);
            cb.putDynamicVec4(0, 0, 0, globalTestColor, 0);
            cb.putDynamicVec4(0, globalTestColor, 0, 0, 0);
            cb.putDynamicVec4(0, 0, 0, globalTestColor, 0);
            cb.putDynamicVec4(0, 0, 0, 0, 0);

            cb.transferDynamic();
            cb.bind();

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
            globalTestColor += 0.01;
        }

        /* (non-Javadoc)
         * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
         */
        @Override
        public void onSurfaceResize(int width, int height)
        {
            viewWidth = width;
            viewHeight = height;
            projectionMatrix = Matrix4.newProjection(120.0f, viewWidth / (float) viewHeight, 1, 10, false);
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
