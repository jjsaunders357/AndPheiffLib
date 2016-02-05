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
import com.pheiffware.andpheifflib.sphere.engine.graphics.buffer.PrimitiveIndexBuffer;
import com.pheiffware.andpheifflib.sphere.engine.graphics.program.Program;
import com.pheiffware.andpheifflib.sphere.engine.graphics.program.Shader;
import com.pheiffware.andpheifflib.sphere.fatalError.FatalErrorHandler;

/**
 *
 */
public class TestRenderer1 implements Renderer
{
	private int testProgram;
	private PrimitiveIndexBuffer pb;
	private CombinedVertexBuffer cb;
	private float globalTestColor = 0.0f;
	private float[] projectionMatrix;
	private AssetManager assetManager;
	private int faceTextureHandle;

	public TestRenderer1(AssetManager assetManager)
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
		GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

		try
		{
			int vertexShaderHandle = Shader.createShader(GLES20.GL_VERTEX_SHADER, assetManager, "shaders/test_vertex_matrix_texture_color.glsl");
			int fragmentShaderHandle = Shader
					.createShader(GLES20.GL_FRAGMENT_SHADER, assetManager, "shaders/test_fragment_matrix_texture_color.glsl");
			testProgram = Program.createProgram(vertexShaderHandle, fragmentShaderHandle);
			faceTextureHandle = ImageUtils.loadAssetImageIntoTexture(assetManager, "images/face.png", true);
		}
		catch (GraphicsException exception)
		{
			FatalErrorHandler.handleFatalError(exception);
		}

		pb = new PrimitiveIndexBuffer(2000);

		float x = 1f, y = 1f, z = 1.1f;
		//@formatter:off 
		cb = new CombinedVertexBuffer(testProgram, 2000, 
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

		pb.putIndex(0);
		pb.putIndex(1);
		pb.putIndex(2);
		pb.putIndex(3);
		pb.putIndex(4);
		pb.putIndex(5);
		pb.transfer();

	}

	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 */
	@Override
	public void onDrawFrame(GL10 gl)
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		GLES20.glUseProgram(testProgram);
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(testProgram, "transformViewMatrix"), 1, false, projectionMatrix, 0);
		ImageUtils.uniformTexture2D(testProgram, "texture", faceTextureHandle);
		cb.putDynamicVec4(0, globalTestColor, 0, 0, 0);
		cb.putDynamicVec4(0, 0, globalTestColor, 0, 0);
		cb.putDynamicVec4(0, 0, 0, globalTestColor, 0);
		cb.putDynamicVec4(0, globalTestColor, 0, 0, 0);
		cb.putDynamicVec4(0, 0, 0, globalTestColor, 0);
		cb.putDynamicVec4(0, 0, 0, 0, 0);
		cb.transferDynamic();
		cb.bind();

		pb.draw(6, GLES20.GL_TRIANGLES);
		globalTestColor += 0.01;
	}

	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		Log.i("OPENGL", "Surface changed");
		GLES20.glViewport(0, 0, width, height);
		projectionMatrix = Utils.generateProjectionMatrix(60.0f, width / (float) height, 1, 10, false);
	}

	public final void setAssetManager(AssetManager assetManager)
	{
		this.assetManager = assetManager;
	}
}
