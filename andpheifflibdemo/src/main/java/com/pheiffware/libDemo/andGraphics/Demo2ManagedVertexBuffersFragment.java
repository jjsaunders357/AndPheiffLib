package com.pheiffware.libDemo.andGraphics;

import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.graphics.AndGraphicsUtils;
import com.pheiffware.lib.and.gui.graphics.openGL.BaseGameFragment;
import com.pheiffware.lib.and.gui.graphics.openGL.BaseGameRenderer;
import com.pheiffware.lib.and.gui.graphics.openGL.GameView;
import com.pheiffware.lib.and.gui.graphics.openGL.SystemInfo;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.engine.MeshDataManager;
import com.pheiffware.lib.graphics.managed.engine.MeshHandle;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.techniques.Tech2D.Color2DTechnique;
import com.pheiffware.lib.graphics.managed.techniques.Tech2D.ColorTexture2DTechnique;
import com.pheiffware.lib.graphics.managed.texture.Texture;
import com.pheiffware.lib.graphics.utils.MeshGenUtils;

import java.nio.ByteBuffer;
import java.util.EnumSet;

/**
 * Example of using a CombinedBuffer for storing some vertex attributes statically and other dynamically.  In this case, vertices are static and colors are dynamically updated.
 * Created by Steve on 3/27/2016.
 */
public class Demo2ManagedVertexBuffersFragment extends BaseGameFragment
{
    @Override
    public GameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new GameView(getContext(), new RendererBase(), FilterQuality.MEDIUM, false, false);
    }

    private static class RendererBase extends BaseGameRenderer
    {
        private MeshDataManager manager;

        private float globalTestColor = 0.0f;
        private Matrix4 ortho2DMatrix;
        private Texture faceTexture;
        private MeshHandle handle1;
        private MeshHandle handle2;
        private MeshHandle handle3;
        private Color2DTechnique color2DTechnique;
        private ColorTexture2DTechnique colorTexture2DTechnique;

        private RendererBase()
        {
            super(AndGraphicsUtils.GL_VERSION_30, AndGraphicsUtils.GL_VERSION_30);
        }

        @Override
        public void onSurfaceCreated(AssetLoader al, GLCache glCache, SystemInfo systemInfo) throws GraphicsException
        {
            // Wait for vertical retrace
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

            color2DTechnique = new Color2DTechnique(al);
            colorTexture2DTechnique = new ColorTexture2DTechnique(al);
            faceTexture = glCache.buildImageTex("images/face.png").build();

            manager = new MeshDataManager();
            Mesh mesh1 = MeshGenUtils.genSingleQuadMeshTexColor(0, 0, 1, VertexAttribute.POSITION4, new float[]{1, 0, 0, 1});
            Mesh mesh2 = MeshGenUtils.genSingleQuadMeshTexColor(1, 0, 1, VertexAttribute.POSITION4, new float[]{0, 1, 0, 1});
            Mesh mesh3 = MeshGenUtils.genSingleQuadMeshTexColor(1, 1, 1, VertexAttribute.POSITION4, new float[]{0, 0, 1, 1});

            //Mesh 1 - No texture, dynamic modulated color, red to purple
            handle1 = manager.addMesh(
                    mesh1,
                    EnumSet.of(VertexAttribute.COLOR),
                    color2DTechnique,
                    new RenderPropertyValue[]{new RenderPropertyValue(RenderProperty.MODEL_MATRIX, Matrix4.newIdentity())});

            //Mesh 2 - Texture, dynamic modulated color - green to cyan
            handle2 = manager.addMesh(
                    mesh2,
                    EnumSet.of(VertexAttribute.COLOR),
                    colorTexture2DTechnique,
                    new RenderPropertyValue[]{new RenderPropertyValue(RenderProperty.MAT_COLOR_TEXTURE, faceTexture), new RenderPropertyValue(RenderProperty.MODEL_MATRIX, Matrix4.newIdentity())});

            //Mesh 2 - Texture, static modulated color - constant blue
            handle3 = manager.addStaticMesh(
                    mesh3,
                    colorTexture2DTechnique,
                    new RenderPropertyValue[]{new RenderPropertyValue(RenderProperty.MAT_COLOR_TEXTURE, faceTexture), new RenderPropertyValue(RenderProperty.MODEL_MATRIX, Matrix4.newIdentity())});
            manager.packAndTransfer();
        }

        /* (non-Javadoc)
         * @see android.opengl.GLSurfaceView.RendererBase#onDrawFrame(javax.microedition.khronos.opengles.GL10)
         */
        @Override
        public void onDrawFrame()
        {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            ByteBuffer byteBuffer;
            byteBuffer = handle2.edit();
            for (int i = 0; i < 4; i++)
            {
                byteBuffer.putFloat(0f);
                byteBuffer.putFloat(1f);
                byteBuffer.putFloat(globalTestColor);
                byteBuffer.putFloat(1f);
            }
            byteBuffer = handle1.edit();
            for (int i = 0; i < 4; i++)
            {
                byteBuffer.putFloat(1f);
                byteBuffer.putFloat(0f);
                byteBuffer.putFloat(globalTestColor);
                byteBuffer.putFloat(1f);
            }
            manager.transferDynamicData();

            //Scale down everything drawn by a factor of 5.
            Matrix4 view = Matrix4.newScale(0.2f, 0.2f, 1f);
            RenderPropertyValue[] propertyValues = {
                    new RenderPropertyValue(RenderProperty.PROJECTION_MATRIX, ortho2DMatrix),
                    new RenderPropertyValue(RenderProperty.VIEW_MATRIX, view)
            };
            handle1.drawTriangles(propertyValues);
            handle2.drawTriangles(propertyValues);
            handle3.drawTriangles(propertyValues);
            globalTestColor += 0.01;
        }


        /* (non-Javadoc)
         * @see android.opengl.GLSurfaceView.RendererBase#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
         */
        @Override
        public void onSurfaceResize(int width, int height)
        {
            super.onSurfaceResize(width, height);
            GLES20.glViewport(0, 0, width, height);
            ortho2DMatrix = Matrix4.newOrtho2D(width / (float) height);
        }

        @Override
        public void onSensorChanged(SensorEvent event)
        {

        }
    }
}
