/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.examples.andGraphics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;
import com.pheiffware.lib.graphics.utils.GraphicsMathUtils;
import com.pheiffware.lib.graphics.FatalGraphicsException;
import com.pheiffware.lib.graphics.utils.TextureUtils;
import com.pheiffware.lib.graphics.buffer.CombinedVertexBuffer;
import com.pheiffware.lib.graphics.utils.ProgramUtils;
import com.pheiffware.lib.fatalError.FatalErrorHandler;

/**
 *
 */
public class TestRenderer2 implements Renderer
{
    private Program testProgram;
    private CombinedVertexBuffer cb;
	private float globalTestColor = 0.0f;
	private float[] projectionMatrix;
	private AssetManager assetManager;
	private int faceTextureHandle;
	private int colorRenderTextureHandle;
	private int depthRenderTextureHandle;
	private int frameBufferHandle;
	private int viewWidth;
	private int viewHeight;

	public TestRenderer2(AssetManager assetManager)
	{
		this.assetManager = assetManager;
	}

	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		Log.i("OPENGL", "Surface created");
		FatalErrorHandler.installUncaughtExceptionHandler();
		// Wait for vertical retrace
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		try
		{
            int vertexShaderHandle = ProgramUtils.createShader(assetManager, GLES20.GL_VERTEX_SHADER, "shaders/test_vertex_matrix_texture_color.glsl");
            int fragmentShaderHandle = ProgramUtils
                    .createShader(assetManager, GLES20.GL_FRAGMENT_SHADER, "shaders/test_fragment_matrix_texture_color.glsl");
            int testProgramHandle = ProgramUtils.createProgram(vertexShaderHandle, fragmentShaderHandle);
            testProgram = new Program(testProgramHandle);
            System.out.println(testProgram);
            //Creates a clamped texture, from a file, with mipmapping
            faceTextureHandle = TextureUtils.genTextureFromImage(assetManager, "images/face.png", true, FilterQuality.MEDIUM, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);

            //Creates color texture render target, without alpha channel
            colorRenderTextureHandle = TextureUtils.genTextureForColorRendering(512, 512, false, FilterQuality.MEDIUM, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);

            //Creates a depth texture render target, without alpha channel
            depthRenderTextureHandle = TextureUtils.genTextureForDepthRendering(512, 512, FilterQuality.MEDIUM, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
            frameBufferHandle = PheiffGLUtils.createFrameBuffer();
        } catch (FatalGraphicsException exception) {
			FatalErrorHandler.handleFatalError(exception);
		}

		float x = 1f, y = 1f, z = 1.1f;
        //@formatter:off
        cb = new CombinedVertexBuffer(testProgram, 200,
                new String[] { "vertexPosition", "vertexTexCoord" },
                new String[]{"vertexColor"});
        //@formatter:on

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
	public void onDrawFrame(GL10 gl)
	{
        //Set to render to texture.
        PheiffGLUtils.bindFrameBuffer(frameBufferHandle, colorRenderTextureHandle, 0);
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if (status != GLES20.GL_FRAMEBUFFER_COMPLETE)
		{
			FatalErrorHandler.handleFatalError("Framebuffer failure");
		}
		GLES20.glViewport(0, 0, 512, 512);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(testProgram.getHandle());
        float[] cameraProjectionMatrix = GraphicsMathUtils.generateProjectionMatrix(70.0f, 1, 1, 10, true);

        //Vertex positions and texture coordinates static.  This encodes a color to mix in.  In this case we want a pure texture render.
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(testProgram.getHandle(), "transformViewMatrix"), 1, false, cameraProjectionMatrix, 0);
        TextureUtils.uniformTexture2D(testProgram.getHandle(), "texture", faceTextureHandle, 0);
        cb.putDynamicVec4(0, 0, 0, 0, 0);
		cb.putDynamicVec4(0, 0, 0, 0, 0);
		cb.putDynamicVec4(0, 0, 0, 0, 0);
		cb.putDynamicVec4(0, 0, 0, 0, 0);
		cb.putDynamicVec4(0, 0, 0, 0, 0);
		cb.putDynamicVec4(0, 0, 0, 0, 0);
		cb.transferDynamic();
		cb.bind();
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        PheiffGLUtils.bindFrameBuffer(0, -1, -1);

		GLES20.glViewport(0, 0, viewWidth, viewHeight);
        projectionMatrix = GraphicsMathUtils.generateProjectionMatrix(60.0f, viewWidth / (float) viewHeight, 1, 10, false);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(testProgram.getHandle());
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(testProgram.getHandle(), "transformViewMatrix"), 1, false, projectionMatrix, 0);
        TextureUtils.uniformTexture2D(testProgram.getHandle(), "texture", colorRenderTextureHandle, 0);
        cb.putDynamicVec4(0, globalTestColor, 0, 0, 0);
		cb.putDynamicVec4(0, 0, globalTestColor, 0, 0);
		cb.putDynamicVec4(0, 0, 0, globalTestColor, 0);
		cb.putDynamicVec4(0, globalTestColor, 0, 0, 0);
		cb.putDynamicVec4(0, 0, 0, globalTestColor, 0);
		cb.putDynamicVec4(0, 0, 0, 0, 0);

		cb.transferDynamic();
		cb.bind();

		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		globalTestColor += 0.01;
	}

	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		Log.i("OPENGL", "Surface changed");
		viewWidth = width;
		viewHeight = height;
	}

	public final void setAssetManager(AssetManager assetManager)
	{
		this.assetManager = assetManager;
	}
}
