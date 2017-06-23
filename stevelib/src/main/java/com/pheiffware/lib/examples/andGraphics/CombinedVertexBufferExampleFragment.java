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
import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.texture.Texture;
import com.pheiffware.lib.graphics.managed.vertexBuffer.CombinedVertexBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.IndexBuffer;

/**
 * Example of using a CombinedBuffer for storing some vertex attributes statically and other dynamically.  In this case, vertices are static and colors are dynamically updated.
 * Created by Steve on 3/27/2016.
 */
public class CombinedVertexBufferExampleFragment extends BaseGameFragment
{
    @Override
    public BaseGameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new BaseGameView(getContext(), new Renderer(), FilterQuality.MEDIUM, false);
    }

    private static class Renderer implements GameRenderer
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
        public void onSurfaceCreated(AssetLoader al, GLCache glCache, SurfaceMetrics surfaceMetrics) throws GraphicsException
        {
            // Wait for vertical retrace
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

            testProgram = new Program(al, "shaders/test/test_vert_mtc.glsl", "shaders/test/test_frag_mtc.glsl");
            faceTexture = glCache.buildImageTex("face","images/face.png").build();

            pb = new IndexBuffer(false);
            pb.allocate(2000);

            float x = 1f, y = 1f, z = 1.1f;
            //@formatter:off
            cb = new CombinedVertexBuffer(new VertexAttribute[] {
                    VertexAttribute.POSITION4,
                    VertexAttribute.TEXCOORD
            },new VertexAttribute[] {
                    VertexAttribute.COLOR
            });
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
            faceTexture.manualBind(0);
            testProgram.setUniformSampler("texture", 0);
            cb.putDynamicVec4(0, globalTestColor, 0, 0, 0);
            cb.putDynamicVec4(0, 0, globalTestColor, 0, 0);
            cb.putDynamicVec4(0, 0, 0, globalTestColor, 0);
            cb.putDynamicVec4(0, globalTestColor, 0, 0, 0);
            cb.putDynamicVec4(0, 0, 0, globalTestColor, 0);
            cb.putDynamicVec4(0, 0, 0, 0, 0);
            cb.transferDynamic();
            cb.bind(testProgram);

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

        @Override
        public void onSensorChanged(SensorEvent event)
        {

        }
    }
}
