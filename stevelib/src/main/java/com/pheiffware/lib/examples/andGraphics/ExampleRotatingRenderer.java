package com.pheiffware.lib.examples.andGraphics;

import android.content.res.AssetManager;
import android.opengl.GLES20;

import com.pheiffware.lib.and.gui.graphics.openGL.TouchViewRenderer;
import com.pheiffware.lib.fatalError.FatalErrorHandler;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;

/**
 * A renderer which loads an object/program and then proceeds to render it over and over, while rotating the view.  This is used for examples.
 * <p/>
 * Created by Steve on 4/11/2016.
 */
public abstract class ExampleRotatingRenderer extends TouchViewRenderer
{
    private float rotation = 0;
    private Matrix4 translationMatrix;
    private Matrix3 normalMatrix = Matrix3.newZeroMatrix();
    private Program program;
    private IndexBuffer indexBuffer;
    private StaticVertexBuffer vertexBuffer;

    public ExampleRotatingRenderer()
    {
        super(90f, 1.0f, 100.0f, 0.01f);
    }

    @Override
    public int maxMajorGLVersion()
    {
        return 3;
    }

    protected abstract Program loadProgram(AssetManager am, ManGL manGL) throws GraphicsException;

    protected abstract StaticVertexBuffer loadBuffers(AssetManager am, ManGL manGL, IndexBuffer indexBuffer, Program program) throws GraphicsException;

    protected abstract Matrix4 getTranslationMatrix();

    protected abstract void setUniforms(Program program, Matrix4 projectionMatrix, Matrix4 viewModelMatrix, Matrix3 normalMatrix);

    @Override
    public void onSurfaceCreated(AssetManager am, ManGL manGL)
    {
        try
        {
            FatalErrorHandler.installUncaughtExceptionHandler();
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            GLES20.glClearDepthf(1);

            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glCullFace(GLES20.GL_BACK);
            GLES20.glEnable(GLES20.GL_CULL_FACE);


            program = loadProgram(am, manGL);
            indexBuffer = new IndexBuffer(false);
            vertexBuffer = loadBuffers(am, manGL, indexBuffer, program);
            translationMatrix = getTranslationMatrix();
            PheiffGLUtils.assertNoError();
        }
        catch (GraphicsException e)
        {
            FatalErrorHandler.handleFatalError(e);
        }
    }


    @Override
    protected void onDrawFrame(Matrix4 projectionMatrix, Matrix4 viewMatrix)
    {
        try
        {
            //Default view volume is based on sitting at origin and looking in negative z direction
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            program.bind();

            Matrix4 modelMatrix = Matrix4.multiply(translationMatrix, Matrix4.newRotate(rotation, 1, 1, 0), Matrix4.newScale(1f, 2f, 1f));

            Matrix4 viewModelMatrix;
            viewModelMatrix = new Matrix4(viewMatrix);
            viewModelMatrix.multiplyBy(modelMatrix);
            normalMatrix.setNormalTransformFromMatrix4Fast(viewModelMatrix);

            setUniforms(program, projectionMatrix, viewModelMatrix, normalMatrix);
            vertexBuffer.bind();
            indexBuffer.drawAll(GLES20.GL_TRIANGLES);
            rotation++;
            PheiffGLUtils.assertNoError();
        }
        catch (GraphicsException e)
        {
            FatalErrorHandler.handleFatalError(e);
        }
    }

}