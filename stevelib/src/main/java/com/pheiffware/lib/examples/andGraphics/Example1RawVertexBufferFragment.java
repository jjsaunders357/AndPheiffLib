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
import com.pheiffware.lib.graphics.managed.program.VertexAttributes;
import com.pheiffware.lib.graphics.managed.texture.Texture;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.DynamicAttributeBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.IndexBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.StaticAttributeBuffer;
import com.pheiffware.lib.graphics.utils.MeshGenUtils;

import java.nio.ByteBuffer;
import java.util.EnumSet;

/**
 * Example of using a CombinedBuffer for storing some vertex attributes statically and other dynamically.  In this case, vertices are static and colors are dynamically updated.
 * Created by Steve on 3/27/2016.
 */
public class Example1RawVertexBufferFragment extends BaseGameFragment
{
    @Override
    public BaseGameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new BaseGameView(getContext(), new Renderer(), FilterQuality.MEDIUM, false);
    }

    private static class Renderer implements GameRenderer
    {
        private Program programTextureColor;
        private IndexBuffer indexBuffer;
        private StaticAttributeBuffer staticBuffer;
        private DynamicAttributeBuffer dynamicBuffer;
        private float globalTestColor = 0.0f;
        private Matrix4 ortho2DMatrix;
        private Texture faceTexture;
        private VertexAttributes staticVertexAttributes;
        private VertexAttributes dynamicVertexAttributes;


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

            programTextureColor = new Program(al, "shaders/2d/color_texture_pos4_2d_vert.glsl", "shaders/2d/color_texture_pos4_2d_frag.glsl");
            faceTexture = glCache.createImageTexture("images/face.png", true, FilterQuality.MEDIUM, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);


            indexBuffer = new IndexBuffer();
            staticBuffer = new StaticAttributeBuffer();
            dynamicBuffer = new DynamicAttributeBuffer();

            short[] indexData = MeshGenUtils.genSingleQuadIndexData();
            indexBuffer.allocateSoftwareBuffer(6 * 2);
            ByteBuffer byteBuffer = indexBuffer.editBuffer();
            for (int i = 0; i < indexData.length; i++)
            {
                byteBuffer.putShort(indexData[i]);
            }

            float[] posData = MeshGenUtils.genSingleQuadPositionData(0, 0, 1, VertexAttribute.POSITION4);
            float[] textureData = MeshGenUtils.genSingleQuadTexData();
            staticBuffer.allocateSoftwareBuffer(6 * 4 * 4);
            byteBuffer = staticBuffer.editBuffer();
            for (int i = 0; i < 4; i++)
            {
                byteBuffer.putFloat(posData[i * 4 + 0]);
                byteBuffer.putFloat(posData[i * 4 + 1]);
                byteBuffer.putFloat(posData[i * 4 + 2]);
                byteBuffer.putFloat(posData[i * 4 + 3]);

                byteBuffer.putFloat(textureData[i * 2 + 0]);
                byteBuffer.putFloat(textureData[i * 2 + 1]);
            }
            staticVertexAttributes = new VertexAttributes(EnumSet.of(VertexAttribute.POSITION4, VertexAttribute.TEXCOORD));

            float[] colorData = MeshGenUtils.genSingleQuadColorData(new float[]{1, 0, 0, 1});
            dynamicBuffer.allocateSoftwareBuffer(4 * 4 * 4);
            byteBuffer = dynamicBuffer.editBuffer();
            for (int i = 0; i < 4; i++)
            {
                byteBuffer.putFloat(colorData[i * 4 + 0]);
                byteBuffer.putFloat(colorData[i * 4 + 1]);
                byteBuffer.putFloat(colorData[i * 4 + 2]);
                byteBuffer.putFloat(colorData[i * 4 + 3]);
            }
            dynamicVertexAttributes = new VertexAttributes(EnumSet.of(VertexAttribute.COLOR));
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
            byteBuffer = dynamicBuffer.editBuffer();
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

            programTextureColor.bind();
            programTextureColor.setUniformMatrix4("projectionViewModelMatrix", projectionViewModelMatrix.m);
            faceTexture.manualBind(0);
            programTextureColor.setUniformSampler("materialColorSampler", 0);

            staticBuffer.bindToProgram(programTextureColor, staticVertexAttributes, 0);
            dynamicBuffer.bindToProgram(programTextureColor, dynamicVertexAttributes, 0);
            indexBuffer.draw(GLES20.GL_TRIANGLES, 6, 0);
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
