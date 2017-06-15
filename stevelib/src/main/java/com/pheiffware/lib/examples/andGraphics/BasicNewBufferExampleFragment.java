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
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.texture.Texture;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.IndexBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.StaticAttributeBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.VertexAttributeHandle;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.VertexIndexHandle;

import java.util.EnumMap;

/**
 * Example of using a CombinedBuffer for storing some vertex attributes statically and other dynamically.  In this case, vertices are static and colors are dynamically updated.
 * Created by Steve on 3/27/2016.
 */
public class BasicNewBufferExampleFragment extends BaseGameFragment
{
    @Override
    public BaseGameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new BaseGameView(getContext(), new Renderer(), FilterQuality.MEDIUM, false);
    }

    private static class Renderer implements GameRenderer
    {
        private static Mesh createQuad()
        {
            float dim = 1.0f;
            short[] vertexIndices = new short[]{
                    0, 1, 2, 3, 4, 5
            };
            EnumMap<VertexAttribute, float[]> data = new EnumMap<>(VertexAttribute.class);
            data.put(VertexAttribute.POSITION, new float[]
                    {
                            -dim, -dim, -1.1f, 1,
                            -dim, dim, -1.1f, 1,
                            dim, dim, -1.1f, 1,
                            -dim, -dim, -1.1f, 1,
                            dim, dim, -1.1f, 1,
                            dim, -dim, -1.1f, 1,
                    });
            data.put(VertexAttribute.COLOR, new float[]
                    {
                            1f, 0f, 0f, 1f,
                            1f, 1f, 0f, 1f,
                            0f, 1f, 1f, 1f,
                            1f, 0f, 1f, 1f,
                            0f, 1f, 1f, 1f,
                            0f, 0f, 1f, 1f,
                    });
            data.put(VertexAttribute.TEXCOORD, new float[]
                    {
                            0f, 1f,
                            0f, 0f,
                            1f, 0f,
                            0f, 1f,
                            1f, 0f,
                            1f, 1f,
                    });

            return new Mesh(6, data, vertexIndices);
        }

        private Program testProgram;
        private IndexBuffer indexBuffer;
        private StaticAttributeBuffer staticBuffer;
        private float globalTestColor = 0.0f;
        private Matrix4 projectionMatrix;
        private Texture faceTexture;
        private VertexIndexHandle indexHandle;
        private VertexAttributeHandle staticAttributeHandle;

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

            testProgram = new Program(al, "shaders/vert_mtc.glsl", "shaders/frag_mtc.glsl");
            faceTexture = glCache.createImageTexture("images/face.png", true, FilterQuality.MEDIUM, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);

            indexBuffer = new IndexBuffer();
            staticBuffer = new StaticAttributeBuffer();
            Mesh mesh = createQuad();
            indexHandle = indexBuffer.addMesh(mesh);
            staticAttributeHandle = staticBuffer.addMesh(mesh);
            indexBuffer.packAndTransfer();
            staticBuffer.packAndTransfer();
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
            testProgram.bind();
            staticBuffer.bind(testProgram, staticAttributeHandle);
            indexBuffer.drawTriangles(indexHandle);
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
