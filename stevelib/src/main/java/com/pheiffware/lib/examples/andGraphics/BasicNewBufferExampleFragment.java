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
import com.pheiffware.lib.graphics.utils.MeshGenUtils;

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
        private Program program;
        private IndexBuffer indexBuffer;
        private StaticAttributeBuffer staticBuffer;
        private float globalTestColor = 0.0f;
        private Matrix4 ortho2DMatrix;
        private Texture faceTexture;
        private VertexIndexHandle indexHandle1;
        private VertexAttributeHandle staticAttributeHandle1;
        private VertexIndexHandle indexHandle2;
        private VertexAttributeHandle staticAttributeHandle2;

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

            program = new Program(al, "shaders/2d/texture_color_pos4_2d_vert.glsl", "shaders/2d/texture_color_pos4_2d_frag.glsl");
            faceTexture = glCache.createImageTexture("images/face.png", true, FilterQuality.MEDIUM, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);

            indexBuffer = new IndexBuffer();
            staticBuffer = new StaticAttributeBuffer();
            Mesh mesh1 = MeshGenUtils.genSingleQuadMesh(0, 0, 1, VertexAttribute.POSITION4, new float[]{1, 0, 0, 1});
            Mesh mesh2 = MeshGenUtils.genSingleQuadMesh(1, 0, 1, VertexAttribute.POSITION4, new float[]{0, 1, 0, 1});
            Mesh mesh3 = MeshGenUtils.genSingleQuadMesh(1, 1, 1, VertexAttribute.POSITION4, new float[]{0, 0, 1, 1});
            indexHandle1 = indexBuffer.addMesh(mesh1);
            staticAttributeHandle1 = staticBuffer.addMesh(mesh1);
            indexHandle2 = indexBuffer.addMesh(mesh2);
            staticAttributeHandle2 = staticBuffer.addMesh(mesh2);
            indexBuffer.packAndTransfer();
            staticBuffer.packAndTransfer();
            //TODO: Consider whether whole mesh needs to be passed as parameter
            //TODO: Deal with same mesh entered twice or ban it.
            //TODO: Make color dynamic
            //TODO: Test multiple meshes simultaneously
        }

        /* (non-Javadoc)
         * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
         */
        @Override
        public void onDrawFrame()
        {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            GLES20.glUseProgram(program.getHandle());

            //Scale down everything drawn by a factor of 5.
            Matrix4 scale = Matrix4.newScale(0.2f, 0.2f, 1f);
            Matrix4 projectionViewModelMatrix = Matrix4.multiply(ortho2DMatrix, scale);
            program.setUniformMatrix4("projectionViewModelMatrix", projectionViewModelMatrix.m);
            faceTexture.manualBind(0);
            program.setUniformSampler("texture", 0);
            program.bind();
            staticBuffer.bind(program, staticAttributeHandle1);
            indexBuffer.drawTriangles(indexHandle1);
            staticBuffer.bind(program, staticAttributeHandle2);
            indexBuffer.drawTriangles(indexHandle2);
            globalTestColor += 0.01;
        }


        /* (non-Javadoc)
         * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
         */
        @Override
        public void onSurfaceResize(int width, int height)
        {
            GLES20.glViewport(0, 0, width, height);
            ortho2DMatrix = Matrix4.newOrtho2D(width / (float) height);
        }

        @Override
        public void onSensorChanged(SensorEvent event)
        {

        }
    }
}
