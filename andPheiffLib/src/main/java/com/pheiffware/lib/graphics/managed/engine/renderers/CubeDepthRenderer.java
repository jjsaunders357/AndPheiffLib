package com.pheiffware.lib.graphics.managed.engine.renderers;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.EuclideanCamera;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Projection;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.engine.Renderer;
import com.pheiffware.lib.graphics.managed.frameBuffer.FrameBuffer;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.techniques.DepthCubeTechnique;
import com.pheiffware.lib.graphics.managed.texture.TextureCubeMap;

/**
 * Created by Steve on 6/28/2017.
 */

public class CubeDepthRenderer extends Renderer
{
    private final FrameBuffer frameBuffer;
    private final Technique depthCubeTechnique;
    private final TextureCubeMap cubeDepthTexture;
    private final TechniqueRenderPass depthRenderPass;
    private final EuclideanCamera lightCamera = new EuclideanCamera();
    private final Projection projection;
    private float[] renderPosition;


    public CubeDepthRenderer(GLCache glCache, TextureCubeMap cubeDepthTexture) throws GraphicsException
    {
        frameBuffer = new FrameBuffer();
        depthCubeTechnique = glCache.buildTechnique(DepthCubeTechnique.class);
        depthRenderPass = new TechniqueRenderPass(depthCubeTechnique);
        this.cubeDepthTexture = cubeDepthTexture;
        projection = new Projection(90.0f, 1.0f, 0.1f, 20.0f, false);
    }

    @Override
    protected void renderImplement()
    {
        frameBuffer.bind(0, 0, cubeDepthTexture.getWidth(), cubeDepthTexture.getHeight());

        cubeDepthTexture.setAttachFace(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z);
        lightCamera.lookAt(renderPosition[0], renderPosition[1], renderPosition[2], renderPosition[0], renderPosition[1], renderPosition[2] + 1.0f, 0.0f, -1.0f, 0.0f);
        renderFace();

        cubeDepthTexture.setAttachFace(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z);
        lightCamera.lookAt(renderPosition[0], renderPosition[1], renderPosition[2], renderPosition[0], renderPosition[1], renderPosition[2] - 1.0f, 0.0f, -1.0f, 0.0f);
        renderFace();

        cubeDepthTexture.setAttachFace(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X);
        lightCamera.lookAt(renderPosition[0], renderPosition[1], renderPosition[2], renderPosition[0] + 1.0f, renderPosition[1], renderPosition[2], 0.0f, -1.0f, 0.0f);
        renderFace();

        cubeDepthTexture.setAttachFace(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X);
        lightCamera.lookAt(renderPosition[0], renderPosition[1], renderPosition[2], renderPosition[0] - 1.0f, renderPosition[1], renderPosition[2], 0.0f, -1.0f, 0.0f);
        renderFace();

        cubeDepthTexture.setAttachFace(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y);
        lightCamera.lookAt(renderPosition[0], renderPosition[1], renderPosition[2], renderPosition[0], renderPosition[1] + 1.0f, renderPosition[2], 0.0f, 0.0f, 1.0f);
        renderFace();

        cubeDepthTexture.setAttachFace(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y);
        lightCamera.lookAt(renderPosition[0], renderPosition[1], renderPosition[2], renderPosition[0], renderPosition[1] - 1.0f, renderPosition[2], 0.0f, 0.0f, -1.0f);
        renderFace();
    }

    private void renderFace()
    {
        frameBuffer.attachDepth(cubeDepthTexture);
        GLES20.glClearDepthf(1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);

        depthCubeTechnique.setProperty(RenderProperty.PROJECTION_LINEAR_DEPTH, projection.getLinearDepth());
//        depthCubeTechnique.setProperty(RenderProperty.PROJECTION_MATRIX, projection.getProjectionMatrix());
        depthCubeTechnique.setProperty(RenderProperty.VIEW_MATRIX, lightCamera.getViewMatrix());
        depthCubeTechnique.applyConstantProperties();
        renderPass(depthRenderPass);
    }

    public void setRenderPosition(float[] renderPosition)
    {
        this.renderPosition = renderPosition;
    }

    public Projection getProjection()
    {
        return projection;
    }
}
