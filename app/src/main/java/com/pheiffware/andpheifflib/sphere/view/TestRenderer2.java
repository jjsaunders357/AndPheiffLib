/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.view;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.pheiffware.andpheifflib.sphere.Utils;
import com.pheiffware.andpheifflib.sphere.engine.graphics.GraphicsException;
import com.pheiffware.andpheifflib.sphere.engine.graphics.ImageUtils;
import com.pheiffware.andpheifflib.sphere.engine.graphics.buffer.CombinedVertexBuffer;
import com.pheiffware.andpheifflib.sphere.engine.graphics.program.Program;
import com.pheiffware.andpheifflib.sphere.engine.graphics.program.Shader;
import com.pheiffware.andpheifflib.sphere.fatalError.FatalErrorHandler;

/**
 *
 */
public class TestRenderer2 implements Renderer
{
	private int testProgram;
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
			int vertexShaderHandle = Shader.createShader(GLES20.GL_VERTEX_SHADER, assetManager, "shaders/test_vertex_matrix_texture_color.glsl");
			int fragmentShaderHandle = Shader
					.createShader(GLES20.GL_FRAGMENT_SHADER, assetManager, "shaders/test_fragment_matrix_texture_color.glsl");
			testProgram = Program.createProgram(vertexShaderHandle, fragmentShaderHandle);
			faceTextureHandle = ImageUtils.loadAssetImageIntoTexture(assetManager, "images/face.png", true);
			colorRenderTextureHandle = ImageUtils.createColorRenderTexture(512, 512, false);
			depthRenderTextureHandle = ImageUtils.createDepthRenderTexture(512, 512);
			frameBufferHandle = ImageUtils.createFrameBuffer();
		}
		catch (GraphicsException exception)
		{
			FatalErrorHandler.handleFatalError(exception);
		}

		float x = 1f, y = 1f, z = 1.1f;
		//@formatter:off 
		cb = new CombinedVertexBuffer(testProgram, 200, 
				new String[] { "vertexPosition", "vertexTexCoord" }, 
				new int[] { 4, 2 }, 
				new int[] {GLES20.GL_FLOAT, GLES20.GL_FLOAT }, 
				new String[] { "vertexColor" }, 
				new int[] { 4 }, 
				new int[] { GLES20.GL_FLOAT });
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
		ImageUtils.bindFrameBuffer(frameBufferHandle, colorRenderTextureHandle, 0);
		int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if (status != GLES20.GL_FRAMEBUFFER_COMPLETE)
		{
			FatalErrorHandler.handleFatalError("Framebuffer failure");
		}
		GLES20.glViewport(0, 0, 512, 512);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		GLES20.glUseProgram(testProgram);
		float[] cameraProjectionMatrix = Utils.generateProjectionMatrix(70.0f, 1, 1, 10, true);

		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(testProgram, "transformViewMatrix"), 1, false, cameraProjectionMatrix, 0);
		ImageUtils.uniformTexture2D(testProgram, "texture", faceTextureHandle);
		cb.putDynamicVec4(0, 0, 0, 0, 0);
		cb.putDynamicVec4(0, 0, 0, 0, 0);
		cb.putDynamicVec4(0, 0, 0, 0, 0);
		cb.putDynamicVec4(0, 0, 0, 0, 0);
		cb.putDynamicVec4(0, 0, 0, 0, 0);
		cb.putDynamicVec4(0, 0, 0, 0, 0);
		cb.transferDynamic();
		cb.bind();
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		ImageUtils.bindFrameBuffer(0, -1, -1);

		GLES20.glViewport(0, 0, viewWidth, viewHeight);
		projectionMatrix = Utils.generateProjectionMatrix(60.0f, viewWidth / (float) viewHeight, 1, 10, false);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		GLES20.glUseProgram(testProgram);
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(testProgram, "transformViewMatrix"), 1, false, projectionMatrix, 0);
		ImageUtils.uniformTexture2D(testProgram, "texture", colorRenderTextureHandle);
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
