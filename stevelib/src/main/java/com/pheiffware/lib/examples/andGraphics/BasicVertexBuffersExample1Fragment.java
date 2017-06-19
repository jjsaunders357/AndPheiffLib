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
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.DynamicAttributeBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.IndexBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.MeshVertexDataPacker;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.MeshVertexIndexPacker;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.StaticAttributeBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.VertexAttributeHandle;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.VertexIndexHandle;
import com.pheiffware.lib.graphics.utils.MeshGenUtils;

import java.nio.ByteBuffer;
import java.util.EnumSet;

/**
 * Example of using a CombinedBuffer for storing some vertex attributes statically and other dynamically.  In this case, vertices are static and colors are dynamically updated.
 * Created by Steve on 3/27/2016.
 */
public class BasicVertexBuffersExample1Fragment extends BaseGameFragment
{
    @Override
    public BaseGameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new BaseGameView(getContext(), new Renderer(), FilterQuality.MEDIUM, false);
    }

    private static class Renderer implements GameRenderer
    {
        private Program programTextureColor;
        private Program programColor;
        private MeshVertexIndexPacker indexPacker;
        private MeshVertexDataPacker staticPacker;
        private MeshVertexDataPacker dynamicPacker;

        private IndexBuffer indexBuffer;
        private StaticAttributeBuffer staticBuffer;
        private DynamicAttributeBuffer dynamicBuffer;
        private float globalTestColor = 0.0f;
        private Matrix4 ortho2DMatrix;
        private Texture faceTexture;
        private VertexIndexHandle indexHandle1;
        private VertexAttributeHandle staticAttributeHandle1;
        private VertexAttributeHandle dynamicAttributeHandle1;
        private VertexIndexHandle indexHandle2;
        private VertexAttributeHandle staticAttributeHandle2;
        private VertexAttributeHandle dynamicAttributeHandle2;
        private VertexIndexHandle indexHandle3;
        private VertexAttributeHandle staticAttributeHandle3;

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

            programTextureColor = new Program(al, "shaders/2d/texture_color_pos4_2d_vert.glsl", "shaders/2d/texture_color_pos4_2d_frag.glsl");
            programColor = new Program(al, "shaders/2d/color_pos4_2d_vert.glsl", "shaders/2d/color_pos4_2d_frag.glsl");
            faceTexture = glCache.createImageTexture("images/face.png", true, FilterQuality.MEDIUM, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
            indexPacker = new MeshVertexIndexPacker();
            staticPacker = new MeshVertexDataPacker();
            dynamicPacker = new MeshVertexDataPacker();

            indexBuffer = new IndexBuffer();
            staticBuffer = new StaticAttributeBuffer();
            dynamicBuffer = new DynamicAttributeBuffer();
            Mesh mesh1 = MeshGenUtils.genSingleQuadMesh(0, 0, 1, VertexAttribute.POSITION4, new float[]{1, 0, 0, 1});
            Mesh mesh2 = MeshGenUtils.genSingleQuadMesh(1, 0, 1, VertexAttribute.POSITION4, new float[]{0, 1, 0, 1});
            Mesh mesh3 = MeshGenUtils.genSingleQuadMesh(1, 1, 1, VertexAttribute.POSITION4, new float[]{0, 0, 1, 1});
            indexHandle1 = indexPacker.addMesh(mesh1);
            staticAttributeHandle1 = staticPacker.addMesh(mesh1, EnumSet.of(VertexAttribute.POSITION4, VertexAttribute.TEXCOORD));
            dynamicAttributeHandle1 = dynamicPacker.addMesh(mesh1, EnumSet.of(VertexAttribute.COLOR));

            indexHandle2 = indexPacker.addMesh(mesh2);
            staticAttributeHandle2 = staticPacker.addMesh(mesh2, EnumSet.of(VertexAttribute.POSITION4, VertexAttribute.TEXCOORD));
            dynamicAttributeHandle2 = dynamicPacker.addMesh(mesh2, EnumSet.of(VertexAttribute.COLOR));

            //3rd mesh will be considered a different "type" as it has 3 attributes instead of 2 in its attribute set
            indexHandle3 = indexPacker.addMesh(mesh3);
            staticAttributeHandle3 = staticPacker.addMesh(mesh3, EnumSet.of(VertexAttribute.POSITION4, VertexAttribute.TEXCOORD, VertexAttribute.COLOR));

            indexPacker.packBuffer(indexBuffer);
            staticPacker.packBuffer(staticBuffer);
            dynamicPacker.packBuffer(dynamicBuffer);
            indexBuffer.transfer();
            staticBuffer.transfer();
            dynamicBuffer.transfer();
        }

        /* (non-Javadoc)
         * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
         */
        @Override
        public void onDrawFrame()
        {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            ByteBuffer byteBuffer;
            byteBuffer = dynamicBuffer.edit(dynamicAttributeHandle2);
            for (int i = 0; i < 4; i++)
            {
                byteBuffer.putFloat(0f);
                byteBuffer.putFloat(1f);
                byteBuffer.putFloat(globalTestColor);
                byteBuffer.putFloat(1f);
            }
            byteBuffer = dynamicBuffer.edit(dynamicAttributeHandle1);
            for (int i = 0; i < 4; i++)
            {
                byteBuffer.putFloat(1f);
                byteBuffer.putFloat(0f);
                byteBuffer.putFloat(globalTestColor);
                byteBuffer.putFloat(1f);
            }
            dynamicBuffer.transfer();


            //Scale down everything drawn by a factor of 5.
            Matrix4 scale = Matrix4.newScale(0.2f, 0.2f, 1f);
            Matrix4 projectionViewModelMatrix = Matrix4.multiply(ortho2DMatrix, scale);

            //Will ignore the texture vertex attribute in mesh
            programColor.bind();
            programColor.setUniformMatrix4("projectionViewModelMatrix", projectionViewModelMatrix.m);

            staticBuffer.drawSetup(programColor, staticAttributeHandle1);
            dynamicBuffer.drawSetup(programColor, dynamicAttributeHandle1);
            indexBuffer.drawTriangles(indexHandle1);

            programTextureColor.bind();
            programTextureColor.setUniformMatrix4("projectionViewModelMatrix", projectionViewModelMatrix.m);
            faceTexture.manualBind(0);
            programTextureColor.setUniformSampler("texture", 0);

            staticBuffer.drawSetup(programTextureColor, staticAttributeHandle2);
            dynamicBuffer.drawSetup(programTextureColor, dynamicAttributeHandle2);
            indexBuffer.drawTriangles(indexHandle2);

            staticBuffer.drawSetup(programTextureColor, staticAttributeHandle3);
            indexBuffer.drawTriangles(indexHandle3);
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
