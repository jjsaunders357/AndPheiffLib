package com.pheiffware.lib.graphics.managed.engine.renderers;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.EuclideanCamera;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.Technique;
import com.pheiffware.lib.graphics.managed.engine.MeshHandle;
import com.pheiffware.lib.graphics.managed.engine.Renderer;
import com.pheiffware.lib.graphics.managed.frameBuffer.FrameBuffer;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.techniques.CubeDepthTechnique;
import com.pheiffware.lib.graphics.managed.texture.TextureCubeMap;
import com.pheiffware.lib.graphics.projection.FieldOfViewProjection;
import com.pheiffware.lib.graphics.projection.Projection;

/**
 * Created by Steve on 6/28/2017.
 */

public class CubeDepthRenderer extends Renderer
{
    private final FrameBuffer frameBuffer;
    private final Technique depthCubeTechnique;
    private final EuclideanCamera lightCamera = new EuclideanCamera();
    private final Projection projection;

    public CubeDepthRenderer(GLCache glCache, float near, float far) throws GraphicsException
    {
        super(glCache.buildTechnique(CubeDepthTechnique.class));
        depthCubeTechnique = getTechnique(0);
        frameBuffer = new FrameBuffer();
        projection = new FieldOfViewProjection(90.0f, near, far);
    }

    public void render(float x, float y, float z, TextureCubeMap cubeDepthTexture)
    {
        frameBuffer.bind(0, 0, cubeDepthTexture.getWidth(), cubeDepthTexture.getHeight());
        GLES20.glClearDepthf(1.0f);
        depthCubeTechnique.setProperty(RenderProperty.PROJECTION_MATRIX, projection.getProjectionMatrix());

        lightCamera.reset(x, y, z, 0, 0, 1.0f, 0.0f, -1.0f, 0.0f);
        renderFace(cubeDepthTexture, GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z);

        lightCamera.reset(x, y, z, 0, 0, -1.0f, 0.0f, -1.0f, 0.0f);
        renderFace(cubeDepthTexture, GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z);

        lightCamera.reset(x, y, z, 1.0f, 0, 0, 0.0f, -1.0f, 0.0f);
        renderFace(cubeDepthTexture, GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X);

        lightCamera.reset(x, y, z, -1.0f, 0, 0, 0.0f, -1.0f, 0.0f);
        renderFace(cubeDepthTexture, GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X);

        lightCamera.reset(x, y, z, 0, 1.0f, 0, 0.0f, 0.0f, 1.0f);
        renderFace(cubeDepthTexture, GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y);

        lightCamera.reset(x, y, z, 0, -1.0f, 0, 0.0f, 0.0f, -1.0f);
        renderFace(cubeDepthTexture, GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y);
    }

    private void renderFace(TextureCubeMap cubeDepthTexture, int attachFace)
    {
        cubeDepthTexture.setAttachFace(attachFace);
        frameBuffer.attachDepth(cubeDepthTexture);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        depthCubeTechnique.setProperty(RenderProperty.VIEW_MATRIX, lightCamera.getViewMatrix());
        depthCubeTechnique.applyConstantProperties();
        renderPass();
    }

    @Override
    protected void renderObject(MeshHandle[] meshHandles)
    {
        for (MeshHandle meshHandle : meshHandles)
        {
            meshHandle.drawTriangles(depthCubeTechnique);
        }
    }

    public Projection getProjection()
    {
        return projection;
    }

    public float getDepthZConst()
    {
        return projection.getDepthZConst();
    }

    public float getDepthZFactor()
    {
        return projection.getDepthZFactor();
    }
}
