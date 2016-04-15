package com.pheiffware.lib.examples.andGraphics;

import android.content.res.AssetManager;
import android.opengl.GLES20;

import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLFragment;
import com.pheiffware.lib.and.gui.graphics.openGL.TouchViewRenderer;
import com.pheiffware.lib.fatalError.FatalErrorHandler;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.geometry.collada.ColladaMaterial;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.managed.engine.ObjectRenderHandle;
import com.pheiffware.lib.graphics.managed.engine.StaticObjectManager;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loads a mesh using the Collada library and displays it on the screen.  Allows the camera to be adjusted using TouchTransform events. Created by Steve on 3/27/2016.
 */

public class StaticManagerExampleFragment extends SimpleGLFragment
{
    public StaticManagerExampleFragment()
    {
        super(new ExampleRenderer(), FilterQuality.MEDIUM);
    }

    private static class ExampleRenderer extends TouchViewRenderer
    {
        private float rotation = 0;
        private Matrix3 normalTransform = Matrix3.newZeroMatrix();
        private float[] lightPosition = new float[]{-3, 3, 0, 1};
        private StaticObjectManager staticObjectManager;
        private ObjectRenderHandle staticMonkey;
        private ObjectRenderHandle staticSphere;
        private ObjectRenderHandle staticCube;

        public ExampleRenderer()
        {
            super(90f, 1.0f, 100.0f, 0.01f);
        }

        @Override
        public int maxMajorGLVersion()
        {
            return 3;
        }

        @Override
        public void onSurfaceCreated(AssetManager am, ManGL manGL)
        {
            try
            {
                FatalErrorHandler.installUncaughtExceptionHandler();
                GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
                GLES20.glEnable(GLES20.GL_DEPTH_TEST);
                GLES20.glCullFace(GLES20.GL_BACK);
                GLES20.glEnable(GLES20.GL_CULL_FACE);
                Program testProgram = manGL.createProgram(am, "testProgram3D", "shaders/vert_mncl.glsl", "shaders/frag_mncl.glsl");
                ColladaFactory colladaFactory = new ColladaFactory(true);
                InputStream inputStream = am.open("meshes/test_render.dae");
                Collada collada = colladaFactory.loadCollada(inputStream);

                //Lookup material from loaded file by "name" (what user named it in editing tool)
                ColladaMaterial colladaMaterial = collada.materialsByName.get("renderMaterial");

                //Lookup object from loaded file by "name" (what user named it in editing tool)

                ColladaObject3D monkey = collada.objects.get("Monkey");
                ColladaObject3D sphere = collada.objects.get("Sphere");
                ColladaObject3D cube = collada.objects.get("Cube");

                staticObjectManager = new StaticObjectManager(testProgram, new String[]
                        {"vertexPosition", "vertexNormal"});
                staticMonkey = staticObjectManager.addMeshGroup(monkey.getMeshGroup());
                staticSphere = staticObjectManager.addMeshGroup(sphere.getMeshGroup());
                staticCube = staticObjectManager.addMeshGroup(cube.getMeshGroup());
                staticObjectManager.transfer();

                PheiffGLUtils.assertNoError();
            }
            catch (GraphicsException | XMLParseException | IOException exception)
            {
                FatalErrorHandler.handleFatalError(exception);
            }
        }

        @Override
        protected void onDrawFrame(Matrix4 projectionMatrix, Matrix4 viewMatrix)
        {
            try
            {
                //Default view volume is based on sitting at origin and looking in negative z direction
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

                float[] lightPositionInEyeSpace = viewMatrix.transformFloatVector(lightPosition);
                Matrix4 modelRotateScale = Matrix4.multiply(Matrix4.newRotate(rotation, 1, 1, 0), Matrix4.newScale(1f, 2f, 1f));

                Matrix4 monkeyTranslation = Matrix4.newTranslation(-3, 2, -5);
                Matrix4 cubeTranslation = Matrix4.newTranslation(0, 2, -5);
                Matrix4 sphereTranslation = Matrix4.newTranslation(3, 2, -5);


                Matrix4 viewModelMatrix;


                Program program = staticObjectManager.getProgram();
                program.bind();

                program.setUniformMatrix4("eyeProjectionMatrix", projectionMatrix.m, false);

                {
                    viewModelMatrix = Matrix4.multiply(viewMatrix, monkeyTranslation, modelRotateScale);
                    normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                    program.setUniformMatrix4("eyeTransformMatrix", viewModelMatrix.m, false);
                    program.setUniformMatrix3("eyeNormalMatrix", normalTransform.m, false);

                    program.setUniformVec4("ambientLightMaterialColor", new float[]{0.2f, 0.2f, 0.2f, 1.0f});
                    program.setUniformVec4("diffuseLightMaterialColor", new float[]{0.8f, 0.0f, 0.65f, 1.0f});
                    program.setUniformVec4("specLightMaterialColor", new float[]{1.0f, 1.0f, 1.0f, 1.0f});
                    program.setUniformFloat("shininess", 30.0f);
                    program.setUniformVec3("lightPositionEyeSpace", lightPositionInEyeSpace);
                    staticObjectManager.render(staticMonkey);
                }

                {
                    viewModelMatrix = Matrix4.multiply(viewMatrix, sphereTranslation, modelRotateScale);
                    normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                    program.setUniformMatrix4("eyeTransformMatrix", viewModelMatrix.m, false);
                    program.setUniformMatrix3("eyeNormalMatrix", normalTransform.m, false);

                    program.setUniformVec4("ambientLightMaterialColor", new float[]{0.2f, 0.2f, 0.2f, 1.0f});
                    program.setUniformVec4("diffuseLightMaterialColor", new float[]{0.6f, 0.8f, 0.3f, 1.0f});
                    program.setUniformVec4("specLightMaterialColor", new float[]{1.0f, 1.0f, 1.0f, 1.0f});
                    program.setUniformFloat("shininess", 30.0f);
                    program.setUniformVec3("lightPositionEyeSpace", lightPositionInEyeSpace);
                    staticObjectManager.render(staticSphere);
                }

                {
                    viewModelMatrix = Matrix4.multiply(viewMatrix, cubeTranslation, modelRotateScale);
                    normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                    program.setUniformMatrix4("eyeTransformMatrix", viewModelMatrix.m, false);
                    program.setUniformMatrix3("eyeNormalMatrix", normalTransform.m, false);

                    program.setUniformVec4("ambientLightMaterialColor", new float[]{0.2f, 0.2f, 0.2f, 1.0f});
                    program.setUniformVec4("diffuseLightMaterialColor", new float[]{0.5f, 0.5f, 0.7f, 1.0f});
                    program.setUniformVec4("specLightMaterialColor", new float[]{1.0f, 1.0f, 1.0f, 1.0f});
                    program.setUniformFloat("shininess", 30.0f);
                    program.setUniformVec3("lightPositionEyeSpace", lightPositionInEyeSpace);
                    staticObjectManager.render(staticCube);
                }
                PheiffGLUtils.assertNoError();
            }
            catch (GraphicsException e)
            {
                FatalErrorHandler.handleFatalError(e);
            }
            rotation++;
        }
    }
}