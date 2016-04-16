package com.pheiffware.lib.examples.andGraphics;

import android.content.res.AssetManager;
import android.opengl.GLES20;

import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLFragment;
import com.pheiffware.lib.and.gui.graphics.openGL.TouchViewRenderer;
import com.pheiffware.lib.fatalError.FatalErrorHandler;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.managed.engine.BaseGraphicsManager;
import com.pheiffware.lib.graphics.managed.engine.MeshRenderHandle;
import com.pheiffware.lib.graphics.managed.engine.ObjectRenderHandle;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Demonstrates using managed graphics to:
 * <p/>
 * 1. Load multiple textures 2. Setup multiple objects for rendering, each composed of multiple pieces, using different programs and uniforms. 3. Allowing generic uniforms which
 * apply to everything (view/projection matrices for example) 4. Allow the overriding of uniforms in general and per object.  Example: make all objects render as green. Created by
 * Steve on 3/27/2016.
 */

public class ManagedGraphicsExampleFragment extends SimpleGLFragment
{
    public ManagedGraphicsExampleFragment()
    {
        super(new ExampleRenderer(), FilterQuality.MEDIUM);
    }

    private static class ExampleRenderer extends TouchViewRenderer
    {
        private float rotation = 0;
        private Matrix3 normalTransform = Matrix3.newZeroMatrix();
        private float[] lightPosition = new float[]{-3, 3, 0, 1};
        private BaseGraphicsManager baseObjectManager;
        private ObjectRenderHandle staticMonkey;
        private ObjectRenderHandle staticSphere;
        private ObjectRenderHandle staticCube;
        private MeshRenderHandle monkeyMesh;
        private MeshRenderHandle sphereMesh;
        private MeshRenderHandle cubeMesh;

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


                //TODO: Fix
//Example code: Want to be able to do this
//                baseObjectManager.loadPrograms();
//                collada.loadObjects(baseObjectManager);
//                baseObjectManager.setGlobalUniformMatrix4("eyeTransformMatrix", viewModelMatrix.m, false);
//                baseObjectManager.render(staticMonkey, "shininess",50.0f);


                //Lookup object from loaded file by "name" (what user named it in editing tool)

                ColladaObject3D monkey = collada.objects.get("Monkey");
                ColladaObject3D sphere = collada.objects.get("Sphere");
                ColladaObject3D cube = collada.objects.get("Cube");

                baseObjectManager = new BaseGraphicsManager(new Program[]{testProgram});
                monkeyMesh = baseObjectManager.addMesh(monkey.getMesh(0), testProgram, new String[]{"shininess"}, new Object[]{30.0f});
                sphereMesh = baseObjectManager.addMesh(sphere.getMesh(0), testProgram, new String[]{"shininess"}, new Object[]{30.0f});
                cubeMesh = baseObjectManager.addMesh(cube.getMesh(0), testProgram, new String[]{"shininess"}, new Object[]{30.0f});

                baseObjectManager.transfer();

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
                Matrix4 modelRotate = Matrix4.multiply(Matrix4.newRotate(rotation, 1, 1, 0));

                Matrix4 monkeyTranslation = Matrix4.newTranslation(-3, 2, -5);
                Matrix4 cubeTranslation = Matrix4.newTranslation(0, 2, -5);
                Matrix4 sphereTranslation = Matrix4.newTranslation(3, 2, -5);


                Matrix4 viewModelMatrix;


//TODO: Create "property concept".  Should be able to set "properties" such as ambientLightColor.  This would be combined with ambientMaterialColor and then applied to shader uniform ambientLightMaterialColor.
//TODO: Add global property option

//                program.setUniformMatrix4("eyeProjectionMatrix", projectionMatrix.m, false);


                viewModelMatrix = Matrix4.multiply(viewMatrix, monkeyTranslation, modelRotate);
                normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                baseObjectManager.render(monkeyMesh,
                        new String[]{
                                "eyeProjectionMatrix",
                                "eyeTransformMatrix",
                                "eyeNormalMatrix",
                                "ambientLightMaterialColor",
                                "diffuseLightMaterialColor",
                                "specLightMaterialColor",
                                "lightPositionEyeSpace"
                        },
                        new Object[]{
                                projectionMatrix.m,
                                viewModelMatrix.m,
                                normalTransform.m,
                                new float[]{0.2f, 0.2f, 0.2f, 1.0f},
                                new float[]{0.8f, 0.0f, 0.65f, 1.0f},
                                new float[]{1.0f, 1.0f, 1.0f, 1.0f},
                                lightPositionInEyeSpace
                        });


                viewModelMatrix = Matrix4.multiply(viewMatrix, sphereTranslation, modelRotate);
                normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                baseObjectManager.render(sphereMesh,
                        new String[]{
                                "eyeProjectionMatrix",
                                "eyeTransformMatrix",
                                "eyeNormalMatrix",
                                "ambientLightMaterialColor",
                                "diffuseLightMaterialColor",
                                "specLightMaterialColor",
                                "lightPositionEyeSpace"
                        },
                        new Object[]{
                                projectionMatrix.m,
                                viewModelMatrix.m,
                                normalTransform.m,
                                new float[]{0.2f, 0.2f, 0.2f, 1.0f},
                                new float[]{0.6f, 0.8f, 0.3f, 1.0f},
                                new float[]{1.0f, 1.0f, 1.0f, 1.0f},
                                lightPositionInEyeSpace
                        });


                viewModelMatrix = Matrix4.multiply(viewMatrix, cubeTranslation, modelRotate);
                normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                baseObjectManager.render(cubeMesh,
                        new String[]{
                                "eyeProjectionMatrix",
                                "eyeTransformMatrix",
                                "eyeNormalMatrix",
                                "ambientLightMaterialColor",
                                "diffuseLightMaterialColor",
                                "specLightMaterialColor",
                                "lightPositionEyeSpace"
                        },
                        new Object[]{
                                projectionMatrix.m,
                                viewModelMatrix.m,
                                normalTransform.m,
                                new float[]{0.2f, 0.2f, 0.2f, 1.0f},
                                new float[]{0.5f, 0.5f, 0.6f, 1.0f},
                                new float[]{1.0f, 1.0f, 1.0f, 1.0f},
                                lightPositionInEyeSpace
                        });

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