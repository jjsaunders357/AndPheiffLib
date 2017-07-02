package com.pheiffware.lib.graphics.managed.engine.renderers;

import android.opengl.GLES20;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.Camera;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.engine.Renderer;
import com.pheiffware.lib.graphics.managed.frameBuffer.FrameBuffer;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.techniques.DepthCubeTechnique;
import com.pheiffware.lib.graphics.managed.texture.TextureCubeMap;

/**
 * Created by Steve on 6/28/2017.
 */

public class CubeDepthRenderer extends Renderer
{
    private final FrameBuffer frameBuffer;
    //TODO: Make programs/techniques unique (put them in cache, so same program/shader loaded more than once, ends up being cached).
    private final DepthCubeTechnique depthCubeTechnique;
    private final TextureCubeMap cubeDepthTexture;
    private final TechniqueRenderPass depthRenderPass;
    private final Camera lightCamera = new Camera(90, 1, 0.1f, 100, false);
    private final float maximumLightDistance;

    private float[] renderPosition;


    public CubeDepthRenderer(AssetLoader al, TextureCubeMap cubeDepthTexture, float maximumLightDistance) throws GraphicsException
    {
        frameBuffer = new FrameBuffer();
        depthCubeTechnique = new DepthCubeTechnique(al);
        depthRenderPass = new TechniqueRenderPass(depthCubeTechnique);
        this.cubeDepthTexture = cubeDepthTexture;
        this.maximumLightDistance = maximumLightDistance;
    }

    @Override
    protected void renderImplement()
    {
        frameBuffer.bind(0, 0, cubeDepthTexture.getWidth(), cubeDepthTexture.getHeight());

        cubeDepthTexture.setAttachFace(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z);
        lightCamera.reset();
        lightCamera.setPosition(renderPosition[0], renderPosition[1], renderPosition[2]);
        renderFace();

        cubeDepthTexture.setAttachFace(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z);
        lightCamera.reset();
        lightCamera.rotate(180, 0, 1, 0);
        lightCamera.setPosition(renderPosition[0], renderPosition[1], renderPosition[2]);
        renderFace();

        cubeDepthTexture.setAttachFace(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X);
        lightCamera.reset();
        lightCamera.rotate(90, 0, 0, 1);
        lightCamera.setPosition(renderPosition[0], renderPosition[1], renderPosition[2]);
        renderFace();

        cubeDepthTexture.setAttachFace(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X);
        lightCamera.reset();
        lightCamera.rotate(-90, 0, 0, 1);
        lightCamera.setPosition(renderPosition[0], renderPosition[1], renderPosition[2]);
        renderFace();

        cubeDepthTexture.setAttachFace(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y);
        lightCamera.reset();
        lightCamera.rotate(90, 1, 0, 0);
        lightCamera.setPosition(renderPosition[0], renderPosition[1], renderPosition[2]);
        renderFace();

        cubeDepthTexture.setAttachFace(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y);
        lightCamera.reset();
        lightCamera.rotate(-90, 1, 0, 0);
        lightCamera.setPosition(renderPosition[0], renderPosition[1], renderPosition[2]);
        renderFace();
    }

    private void renderFace()
    {
        frameBuffer.attachDepth(cubeDepthTexture);
        GLES20.glClearDepthf(1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);

        depthCubeTechnique.setProperty(RenderProperty.PROJECTION_MATRIX, lightCamera.getProjectionMatrix());
        depthCubeTechnique.setProperty(RenderProperty.VIEW_MATRIX, lightCamera.getViewMatrix());
        depthCubeTechnique.setProperty(RenderProperty.MAXIMUM_LIGHT_DISTANCE, maximumLightDistance);

        //TODO: Test rendering a large shape which covers entire face.
        renderPass(depthRenderPass);
    }

    public void setRenderPosition(float[] renderPosition)
    {
        this.renderPosition = renderPosition;
    }

}
