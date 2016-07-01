package com.pheiffware.lib.examples.andGraphics;

import android.opengl.GLES20;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLFragment;
import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLRenderer;
import com.pheiffware.lib.geometry.Transform2D;
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
        public void onSurfaceCreated(AssetLoader al, GLCache glCache) throws GraphicsException
        {
            // Wait for vertical retrace
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

            testProgram = new Program(al, "shaders/vert_mtc.glsl", "shaders/frag_mtc.glsl");
            faceTexture = glCache.createImageTexture("images/face.png", true, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);

            //Creates color texture render target, without alpha channel
            colorRenderTexture = glCache.createColorRenderTexture("colorRender1", 512, 512, false, FilterQuality.MEDIUM, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);

            //Creates a depth texture render target, without alpha channel
            depthRenderTexture = glCache.createDepthRenderTexture("depthRender1", 512, 512, FilterQuality.MEDIUM, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
            frameBuffer = new FrameBuffer();

            float x = 1f, y = 1f, z = 1.1f;
            //@formatter:off
            cb = new CombinedVertexBuffer(new VertexAttribute[] {
                    VertexAttribute.POSITION,
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
            frameBuffer.bind();
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
            PheiffGLUtils.bindFrameBuffer(0, -1, -1);

            GLES20.glViewport(0, 0, viewWidth, viewHeight);
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
