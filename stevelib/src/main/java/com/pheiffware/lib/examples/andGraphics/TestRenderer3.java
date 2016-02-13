/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.examples.andGraphics;

import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.pheiffware.lib.graphics.utils.GraphicsMathUtils;
import com.pheiffware.lib.graphics.FatalGraphicsException;
import com.pheiffware.lib.graphics.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.utils.ProgramUtils;
import com.pheiffware.lib.mesh.Mesh;
import com.pheiffware.lib.fatalError.FatalErrorHandler;

/**
 *
 */
public class TestRenderer3 implements Renderer
{
	private int testProgram;
	private IndexBuffer pb;
	private StaticVertexBuffer sb;
	private Map<String, Mesh> meshes;
	private float[] projectionMatrix;
	private AssetManager assetManager;

	public TestRenderer3(AssetManager assetManager)
	{
		this.assetManager = assetManager;
	}

	@Override
	public void onSurfaceCreated(GL10 gl,
			javax.microedition.khronos.egl.EGLConfig config)
	{
		Log.i("OPENGL", "Surface created");
		FatalErrorHandler.installUncaughtExceptionHandler();
		// Wait for vertical retrace
		GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

		try
		{
			int vertexShaderHandle = ProgramUtils.createShader(
					assetManager, GLES20.GL_VERTEX_SHADER, "shaders/test_vertex_mnc.glsl"
			);
			int fragmentShaderHandle = ProgramUtils.createShader(
					assetManager, GLES20.GL_FRAGMENT_SHADER, "shaders/test_fragment_mnc.glsl"
			);
			testProgram = ProgramUtils.createProgram(vertexShaderHandle,
					fragmentShaderHandle);
			meshes = Mesh.loadMeshes(assetManager, "meshes/spheres.mesh");
		} catch (FatalGraphicsException exception)
		{
			FatalErrorHandler.handleFatalError(exception);
		}
		Mesh sphereMesh = meshes.get("sphere4");
		float[] colors = sphereMesh.generateMultiColorValues();
		pb = new IndexBuffer(sphereMesh.getNumPrimitives());
		pb.putIndices(sphereMesh.primitiveIndices);
		pb.transfer();

		// @formatter:off
		sb = new StaticVertexBuffer(testProgram, sphereMesh.getNumVertices(),
				new String[]
				{ "vertexPosition", "vertexNormal", "vertexColor" }, new int[]
				{ 4, 4, 4 }, new int[]
				{ GLES20.GL_FLOAT, GLES20.GL_FLOAT, GLES20.GL_FLOAT });
		// @formatter:on

		sb.putFloats(0, sphereMesh.vertices);
		sb.putFloats(0, sphereMesh.normals);
		sb.putFloats(2, colors);

		sb.transfer();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.
	 * khronos.opengles.GL10)
	 */
	@Override
	public void onDrawFrame(GL10 gl)
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		GLES20.glUseProgram(testProgram);
		float[] matrix = GraphicsMathUtils.createTranslationMatrix(0, 0, -2);
		matrix = GraphicsMathUtils.multiplyMatrix(projectionMatrix, matrix);
		GLES20.glUniformMatrix4fv(
				GLES20.glGetUniformLocation(testProgram, "transformViewMatrix"),
				1, false, matrix, 0);
		sb.bind();
		pb.drawAll(GLES20.GL_TRIANGLES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition
	 * .khronos.opengles.GL10, int, int)
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		Log.i("OPENGL", "Surface changed");
		GLES20.glViewport(0, 0, width, height);
		projectionMatrix = GraphicsMathUtils.generateProjectionMatrix(60.0f, width
				/ (float) height, 1, 10, false);
	}

	public final void setAssetManager(AssetManager assetManager)
	{
		this.assetManager = assetManager;
	}

}
