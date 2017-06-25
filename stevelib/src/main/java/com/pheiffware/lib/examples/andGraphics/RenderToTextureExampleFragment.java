package com.pheiffware.lib.examples.andGraphics;

import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.gui.graphics.openGL.BaseGameFragment;
import com.pheiffware.lib.and.gui.graphics.openGL.BaseGameView;
import com.pheiffware.lib.and.gui.graphics.openGL.GameRenderer;
import com.pheiffware.lib.and.gui.graphics.openGL.SurfaceMetrics;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.frameBuffer.FrameBuffer;
import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.texture.Texture;
import com.pheiffware.lib.graphics.managed.vertexBuffer.CombinedVertexBuffer;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;

/**
 * Example of rendering to a texture. Created by Steve on 3/27/2016.
 */
public class RenderToTextureExampleFragment extends BaseGameFragment
{
    @Override
    public BaseGameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new BaseGameView(getContext(), new Renderer(), FilterQuality.MEDIUM, false);
    }

    private static class Renderer implements GameRenderer
    {
        private final Matrix4 cameraProjectionMatrix = Matrix4.newProjection(140.0f, 1, 1, 10, true);
        private Matrix4 projectionMatrix;
        private Program testProgram;
        private Texture faceTexture;
        private Texture colorRenderTexture;
        private Texture depthRenderTexture;
        private CombinedVertexBuffer cb;
        private float globalTestColor = 0.0f;
        private FrameBuffer frameBuffer;
        private int viewWidth;
        private int viewHeight;

        @Override
        public int maxMajorGLVersion()
        {
            return 3;
        }

        /* (non-Javadoc)
         * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
         */
        @Override
        public void onSurfaceCreated(AssetLoader al, GLCache glCache, SurfaceMetrics surfaceMetrics) throws GraphicsException
        {
            // Wait for vertical retrace
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

            testProgram = new Program(al, "shaders/test/test_vert_mtc.glsl", "shaders/test/test_frag_mtc.glsl");
            faceTexture = glCache.buildImageTex("images/face.png").build();


            //Creates color texture render target, without alpha channel
            colorRenderTexture = glCache.buildColorRenderTex(512, 512).build();

            //Creates a depth texture render target, without alpha channel
            depthRenderTexture = glCache.buildDepthTex(512, 512).build();

            frameBuffer = new FrameBuffer();

            float x = 1f, y = 1f, z = 1.1f;
            //@formatter:off
            cb = new CombinedVertexBuffer(new VertexAttribute[] {
                    VertexAttribute.POSITION4,
                    VertexAttribute.TEXCOORD
            },new VertexAttribute[] {
                    VertexAttribute.COLOR
            });
            //@formatter:on
            cb.allocate(200);
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
        public void onDrawFrame() throws GraphicsException
        {
            //Set to render to texture.
            frameBuffer.bind(0, 0, 512, 512);
            frameBuffer.attachColor(0, colorRenderTexture);
            frameBuffer.attachDepth(null);

            PheiffGLUtils.assertFrameBufferStatus();

            GLES20.glViewport(0, 0, 512, 512);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            testProgram.bind();
            testProgram.setUniformMatrix4("transformViewMatrix", cameraProjectionMatrix.m);
            faceTexture.manualBind(0);
            testProgram.setUniformSampler("texture", 0);

            //Vertex positions and texture coordinates static.  This encodes a color to mix in.  In this case we want a pure texture render.
            cb.putDynamicVec4(0, 0, 0, 0, 0);
            cb.putDynamicVec4(0, 0, 0, 0, 0);
            cb.putDynamicVec4(0, 0, 0, 0, 0);
            cb.putDynamicVec4(0, 0, 0, 0, 0);
            cb.putDynamicVec4(0, 0, 0, 0, 0);
            cb.putDynamicVec4(0, 0, 0, 0, 0);
            cb.transferDynamic();
            cb.bind(testProgram);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

            FrameBuffer.main.bind(0, 0, viewWidth, viewHeight);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            testProgram.bind();
            testProgram.setUniformMatrix4("transformViewMatrix", projectionMatrix.m);
            colorRenderTexture.manualBind(1);
            testProgram.setUniformSampler("texture", 1);
            cb.putDynamicVec4(0, globalTestColor, 0, 0, 0);
            cb.putDynamicVec4(0, 0, globalTestColor, 0, 0);
            cb.putDynamicVec4(0, 0, 0, globalTestColor, 0);
            cb.putDynamicVec4(0, globalTestColor, 0, 0, 0);
            cb.putDynamicVec4(0, 0, 0, globalTestColor, 0);
            cb.putDynamicVec4(0, 0, 0, 0, 0);

            cb.transferDynamic();
            cb.bind(testProgram);

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

        @Override
        public void onSensorChanged(SensorEvent event)
        {

        }
    }
}
