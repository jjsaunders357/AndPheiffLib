package com.pheiffware.lib.examples.andGraphics;

import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

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
import com.pheiffware.lib.graphics.ShadConst;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.Texture;
import com.pheiffware.lib.graphics.managed.engine.BaseGraphicsManager;
import com.pheiffware.lib.graphics.managed.engine.MeshRenderHandle;
import com.pheiffware.lib.graphics.managed.engine.ObjectRenderHandle;
import com.pheiffware.lib.graphics.managed.engine.UniformNameValue;
import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;
import com.pheiffware.lib.graphics.utils.TextureUtils;
import com.pheiffware.lib.utils.MapCounterLong;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Dirty, temporary, class for profiling how different rendering schemes perform.
 */

public class TimedManagedGraphicsExampleFragment extends SimpleGLFragment
{
    public TimedManagedGraphicsExampleFragment()
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
        private MeshRenderHandle redMesh;
        private MeshRenderHandle blueMesh;
        private MeshRenderHandle brownMesh;
        private MeshRenderHandle greyMesh;
        private long startFrameTimeStamp;
        private final MapCounterLong<String> nanoTimes = new MapCounterLong<>();
        private int frameCounter;
        private int logFramePeriod = 100;
        private Texture bbTex;
        private Texture greyTex;
        private Program texProgram;
        private Program colorProgram;

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
        public void onSurfaceCreated(AssetManager am, GLCache GLCache)
        {
            frameCounter = 0;
            nanoTimes.clear();
            try
            {
                FatalErrorHandler.installUncaughtExceptionHandler();
                GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
                GLES20.glEnable(GLES20.GL_DEPTH_TEST);
                GLES20.glCullFace(GLES20.GL_BACK);
                GLES20.glEnable(GLES20.GL_CULL_FACE);
                colorProgram = GLCache.createProgram(am, "colorProgram3D", "shaders/vert_mncl.glsl", "shaders/frag_mncl.glsl");
                texProgram = GLCache.createProgram(am, "texProgram3D", "shaders/vert_mntl.glsl", "shaders/frag_mntl.glsl");
                bbTex = GLCache.createImageTexture(am, "images/brown_brick.jpg", true, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
                greyTex = GLCache.createImageTexture(am, "images/grey_brick.jpg", true, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);


                ColladaFactory colladaFactory = new ColladaFactory(true);
                InputStream inputStream = am.open("meshes/cubes.dae");
                Collada collada = colladaFactory.loadCollada(inputStream);


                //Lookup object from loaded file by "name" (what user named it in editing tool)
                ColladaObject3D red = collada.objects.get("red");
                ColladaObject3D blue = collada.objects.get("blue");
                ColladaObject3D brown = collada.objects.get("brown");
                ColladaObject3D grey = collada.objects.get("grey");

                baseObjectManager = new BaseGraphicsManager(new Program[]{colorProgram, texProgram});
                redMesh = baseObjectManager.addMesh(red.getMesh(0), colorProgram, new UniformNameValue[]{new UniformNameValue(ShadConst.SHININESS_UNIFORM, 30.0f)});
                blueMesh = baseObjectManager.addMesh(blue.getMesh(0), colorProgram, new UniformNameValue[]{new UniformNameValue(ShadConst.SHININESS_UNIFORM, 30.0f)});
                brownMesh = baseObjectManager.addMesh(brown.getMesh(0), texProgram, new UniformNameValue[]{new UniformNameValue(ShadConst.SHININESS_UNIFORM, 30.0f)});
                greyMesh = baseObjectManager.addMesh(grey.getMesh(0), texProgram, new UniformNameValue[]{new UniformNameValue(ShadConst.SHININESS_UNIFORM, 30.0f)});

                baseObjectManager.transfer();

                PheiffGLUtils.assertNoError();
            }
            catch (GraphicsException | XMLParseException | IOException exception)
            {
                FatalErrorHandler.handleFatalError(exception);
            }
        }

        private final void addCount(String key)
        {
            long endTime = System.nanoTime();
            nanoTimes.addCount(key, endTime - startFrameTimeStamp);
            startFrameTimeStamp = System.nanoTime();
        }

        @Override
        protected void onDrawFrame(Matrix4 projectionMatrix, Matrix4 viewMatrix)
        {
            try
            {
                //Default view volume is based on sitting at origin and looking in negative z direction
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

                startFrameTimeStamp = System.nanoTime();

                red50(projectionMatrix, viewMatrix);

                GLES20.glFinish();
                addCount("render");
                frameCounter++;
                logAverages();
                PheiffGLUtils.assertNoError();
            }
            catch (GraphicsException e)
            {
                FatalErrorHandler.handleFatalError(e);
            }
            rotation++;
        }


        private void logAverages()
        {
            if (frameCounter % logFramePeriod == 0)
            {
                for (Map.Entry<String, Long> entry : nanoTimes.entrySet())
                {
                    Log.i("profile", entry.getKey() + ": " + (0.000000001 * entry.getValue() / frameCounter));
                }
            }
        }

        private void red1(Matrix4 projectionMatrix, Matrix4 viewMatrix)
        {
            float[] lightPositionInEyeSpace = viewMatrix.transformFloatVector(lightPosition);
            Matrix4 modelRotate = Matrix4.multiply(Matrix4.newRotate(rotation, 1, 1, 0));

            Matrix4 redTranslation = Matrix4.newTranslation(-3, 2, -5);
            Matrix4 blueTranslation = Matrix4.newTranslation(0, 2, -5);
            Matrix4 brownTranslation = Matrix4.newTranslation(3, 2, -5);
            Matrix4 greyTranslation = Matrix4.newTranslation(0, -2, -5);

            Matrix4 viewModelMatrix;

            baseObjectManager.bindProgram(redMesh);
            viewModelMatrix = Matrix4.multiply(viewMatrix, redTranslation, modelRotate);
            normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
            baseObjectManager.setDefaultUniformValues(redMesh);
            colorProgram.setUniformValues(
                    new String[]{
                            ShadConst.EYE_PROJECTION_MATRIX_UNIFORM,
                            ShadConst.EYE_TRANSFORM_MATRIX_UNIFORM,
                            ShadConst.EYE_NORMAL_MATRIX_UNIFORM,
                            ShadConst.AMBIENT_LIGHTMAT_COLOR_UNIFORM,
                            ShadConst.DIFF_LIGHTMAT_COLOR_UNIFORM,
                            ShadConst.SPEC_LIGHTMAT_COLOR_UNIFORM,
                            ShadConst.LIGHT_POS_EYE_UNIFORM
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
            baseObjectManager.renderIndexBuffer(redMesh);
        }

        private void red50(Matrix4 projectionMatrix, Matrix4 viewMatrix)
        {
            float[] lightPositionInEyeSpace = viewMatrix.transformFloatVector(lightPosition);
            Matrix4 modelRotate = Matrix4.multiply(Matrix4.newRotate(rotation, 1, 1, 0));

            Matrix4 redTranslation = Matrix4.newTranslation(-3, 2, -5);
            Matrix4 blueTranslation = Matrix4.newTranslation(0, 2, -5);
            Matrix4 brownTranslation = Matrix4.newTranslation(3, 2, -5);
            Matrix4 greyTranslation = Matrix4.newTranslation(0, -2, -5);

            Matrix4 viewModelMatrix;

            baseObjectManager.bindProgram(redMesh);
            for (int i = 0; i < 50; i++)
            {
                viewModelMatrix = Matrix4.multiply(viewMatrix, redTranslation, modelRotate);
                normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                baseObjectManager.setDefaultUniformValues(redMesh);
                colorProgram.setUniformValues(
                        new String[]{
                                ShadConst.EYE_PROJECTION_MATRIX_UNIFORM,
                                ShadConst.EYE_TRANSFORM_MATRIX_UNIFORM,
                                ShadConst.EYE_NORMAL_MATRIX_UNIFORM,
                                ShadConst.AMBIENT_LIGHTMAT_COLOR_UNIFORM,
                                ShadConst.DIFF_LIGHTMAT_COLOR_UNIFORM,
                                ShadConst.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                ShadConst.LIGHT_POS_EYE_UNIFORM
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
                baseObjectManager.renderIndexBuffer(redMesh);
            }
        }

        private void red50brown50(Matrix4 projectionMatrix, Matrix4 viewMatrix)
        {
            float[] lightPositionInEyeSpace = viewMatrix.transformFloatVector(lightPosition);
            Matrix4 modelRotate = Matrix4.multiply(Matrix4.newRotate(rotation, 1, 1, 0));

            Matrix4 redTranslation = Matrix4.newTranslation(-3, 2, -5);
            Matrix4 blueTranslation = Matrix4.newTranslation(0, 2, -5);
            Matrix4 brownTranslation = Matrix4.newTranslation(3, 2, -5);
            Matrix4 greyTranslation = Matrix4.newTranslation(0, -2, -5);

            Matrix4 viewModelMatrix;

            baseObjectManager.bindProgram(redMesh);
            for (int i = 0; i < 50; i++)
            {
                viewModelMatrix = Matrix4.multiply(viewMatrix, redTranslation, modelRotate);
                normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                baseObjectManager.setDefaultUniformValues(redMesh);
                colorProgram.setUniformValues(
                        new String[]{
                                ShadConst.EYE_PROJECTION_MATRIX_UNIFORM,
                                ShadConst.EYE_TRANSFORM_MATRIX_UNIFORM,
                                ShadConst.EYE_NORMAL_MATRIX_UNIFORM,
                                ShadConst.AMBIENT_LIGHTMAT_COLOR_UNIFORM,
                                ShadConst.DIFF_LIGHTMAT_COLOR_UNIFORM,
                                ShadConst.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                ShadConst.LIGHT_POS_EYE_UNIFORM
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
                baseObjectManager.renderIndexBuffer(redMesh);
            }
            baseObjectManager.bindProgram(brownMesh);
            for (int i = 0; i < 50; i++)
            {
                viewModelMatrix = Matrix4.multiply(viewMatrix, brownTranslation, modelRotate);
                normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                baseObjectManager.setDefaultUniformValues(brownMesh);
                texProgram.setUniformValues(
                        new String[]{
                                ShadConst.EYE_PROJECTION_MATRIX_UNIFORM,
                                ShadConst.EYE_TRANSFORM_MATRIX_UNIFORM,
                                ShadConst.EYE_NORMAL_MATRIX_UNIFORM,
                                ShadConst.AMBIENT_LIGHT_COLOR_UNIFORM,
                                ShadConst.DIFF_LIGHT_COLOR_UNIFORM,
                                ShadConst.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                ShadConst.LIGHT_POS_EYE_UNIFORM,
                                ShadConst.DIFF_MATERIAL_TEXTURE_UNIFORM
                        },
                        new Object[]{
                                projectionMatrix.m,
                                viewModelMatrix.m,
                                normalTransform.m,
                                new float[]{0.2f, 0.2f, 0.2f, 1.0f},
                                new float[]{0.5f, 0.5f, 0.6f, 1.0f},
                                new float[]{1.0f, 1.0f, 1.0f, 1.0f},
                                lightPositionInEyeSpace,
                                bbTex
                        });
                baseObjectManager.renderIndexBuffer(brownMesh);
            }

        }

        private void redbrown50(Matrix4 projectionMatrix, Matrix4 viewMatrix)
        {
            float[] lightPositionInEyeSpace = viewMatrix.transformFloatVector(lightPosition);
            Matrix4 modelRotate = Matrix4.multiply(Matrix4.newRotate(rotation, 1, 1, 0));

            Matrix4 redTranslation = Matrix4.newTranslation(-3, 2, -5);
            Matrix4 blueTranslation = Matrix4.newTranslation(0, 2, -5);
            Matrix4 brownTranslation = Matrix4.newTranslation(3, 2, -5);
            Matrix4 greyTranslation = Matrix4.newTranslation(0, -2, -5);

            Matrix4 viewModelMatrix;

            baseObjectManager.bindProgram(redMesh);
            for (int i = 0; i < 50; i++)
            {
                viewModelMatrix = Matrix4.multiply(viewMatrix, redTranslation, modelRotate);
                normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                baseObjectManager.render(redMesh,
                        new String[]{
                                ShadConst.EYE_PROJECTION_MATRIX_UNIFORM,
                                ShadConst.EYE_TRANSFORM_MATRIX_UNIFORM,
                                ShadConst.EYE_NORMAL_MATRIX_UNIFORM,
                                ShadConst.AMBIENT_LIGHTMAT_COLOR_UNIFORM,
                                ShadConst.DIFF_LIGHTMAT_COLOR_UNIFORM,
                                ShadConst.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                ShadConst.LIGHT_POS_EYE_UNIFORM
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
                viewModelMatrix = Matrix4.multiply(viewMatrix, brownTranslation, modelRotate);
                normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                baseObjectManager.render(brownMesh,
                        new String[]{
                                ShadConst.EYE_PROJECTION_MATRIX_UNIFORM,
                                ShadConst.EYE_TRANSFORM_MATRIX_UNIFORM,
                                ShadConst.EYE_NORMAL_MATRIX_UNIFORM,
                                ShadConst.AMBIENT_LIGHT_COLOR_UNIFORM,
                                ShadConst.DIFF_LIGHT_COLOR_UNIFORM,
                                ShadConst.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                ShadConst.LIGHT_POS_EYE_UNIFORM,
                                ShadConst.DIFF_MATERIAL_TEXTURE_UNIFORM
                        },
                        new Object[]{
                                projectionMatrix.m,
                                viewModelMatrix.m,
                                normalTransform.m,
                                new float[]{0.2f, 0.2f, 0.2f, 1.0f},
                                new float[]{0.5f, 0.5f, 0.6f, 1.0f},
                                new float[]{1.0f, 1.0f, 1.0f, 1.0f},
                                lightPositionInEyeSpace,
                                bbTex
                        });
            }

        }

        private void brown50(Matrix4 projectionMatrix, Matrix4 viewMatrix)
        {
            float[] lightPositionInEyeSpace = viewMatrix.transformFloatVector(lightPosition);
            Matrix4 modelRotate = Matrix4.multiply(Matrix4.newRotate(rotation, 1, 1, 0));

            Matrix4 redTranslation = Matrix4.newTranslation(-3, 2, -5);
            Matrix4 blueTranslation = Matrix4.newTranslation(0, 2, -5);
            Matrix4 brownTranslation = Matrix4.newTranslation(3, 2, -5);
            Matrix4 greyTranslation = Matrix4.newTranslation(0, -2, -5);

            Matrix4 viewModelMatrix;

            for (int i = 0; i < 50; i++)
            {
                viewModelMatrix = Matrix4.multiply(viewMatrix, brownTranslation, modelRotate);
                normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                baseObjectManager.render(brownMesh,
                        new String[]{
                                ShadConst.EYE_PROJECTION_MATRIX_UNIFORM,
                                ShadConst.EYE_TRANSFORM_MATRIX_UNIFORM,
                                ShadConst.EYE_NORMAL_MATRIX_UNIFORM,
                                ShadConst.AMBIENT_LIGHT_COLOR_UNIFORM,
                                ShadConst.DIFF_LIGHT_COLOR_UNIFORM,
                                ShadConst.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                ShadConst.LIGHT_POS_EYE_UNIFORM,
                                ShadConst.DIFF_MATERIAL_TEXTURE_UNIFORM
                        },
                        new Object[]{
                                projectionMatrix.m,
                                viewModelMatrix.m,
                                normalTransform.m,
                                new float[]{0.2f, 0.2f, 0.2f, 1.0f},
                                new float[]{0.5f, 0.5f, 0.6f, 1.0f},
                                new float[]{1.0f, 1.0f, 1.0f, 1.0f},
                                lightPositionInEyeSpace,
                                bbTex
                        });
            }

        }

        private void brownGrey50(Matrix4 projectionMatrix, Matrix4 viewMatrix)
        {
            float[] lightPositionInEyeSpace = viewMatrix.transformFloatVector(lightPosition);
            Matrix4 modelRotate = Matrix4.multiply(Matrix4.newRotate(rotation, 1, 1, 0));

            Matrix4 redTranslation = Matrix4.newTranslation(-3, 2, -5);
            Matrix4 blueTranslation = Matrix4.newTranslation(0, 2, -5);
            Matrix4 brownTranslation = Matrix4.newTranslation(3, 2, -5);
            Matrix4 greyTranslation = Matrix4.newTranslation(0, -2, -5);

            Matrix4 viewModelMatrix;
            TextureUtils.setActiveTextureUnit(0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bbTex.getHandle());
            TextureUtils.setActiveTextureUnit(1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, greyTex.getHandle());

            baseObjectManager.bindProgram(brownMesh);
            for (int i = 0; i < 50; i++)
            {
                viewModelMatrix = Matrix4.multiply(viewMatrix, brownTranslation, modelRotate);
                normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                baseObjectManager.setDefaultUniformValues(brownMesh);
                texProgram.setUniformValues(
                        new String[]{
                                ShadConst.EYE_PROJECTION_MATRIX_UNIFORM,
                                ShadConst.EYE_TRANSFORM_MATRIX_UNIFORM,
                                ShadConst.EYE_NORMAL_MATRIX_UNIFORM,
                                ShadConst.AMBIENT_LIGHT_COLOR_UNIFORM,
                                ShadConst.DIFF_LIGHT_COLOR_UNIFORM,
                                ShadConst.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                ShadConst.LIGHT_POS_EYE_UNIFORM
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
//                TextureUtils.setActiveTextureUnit(0);
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bbTex.getHandle());
                GLES20.glUniform1i(texProgram.getUniform(ShadConst.DIFF_MATERIAL_TEXTURE_UNIFORM).location, 0);

                baseObjectManager.renderIndexBuffer(brownMesh);

                viewModelMatrix = Matrix4.multiply(viewMatrix, greyTranslation, modelRotate);
                normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                baseObjectManager.setDefaultUniformValues(greyMesh);
                texProgram.setUniformValues(
                        new String[]{
                                ShadConst.EYE_PROJECTION_MATRIX_UNIFORM,
                                ShadConst.EYE_TRANSFORM_MATRIX_UNIFORM,
                                ShadConst.EYE_NORMAL_MATRIX_UNIFORM,
                                ShadConst.AMBIENT_LIGHT_COLOR_UNIFORM,
                                ShadConst.DIFF_LIGHT_COLOR_UNIFORM,
                                ShadConst.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                ShadConst.LIGHT_POS_EYE_UNIFORM
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
//                TextureUtils.setActiveTextureUnit(1);
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, greyTex.getHandle());
                GLES20.glUniform1i(texProgram.getUniform(ShadConst.DIFF_MATERIAL_TEXTURE_UNIFORM).location, 1);

                baseObjectManager.renderIndexBuffer(greyMesh);
            }

        }

        private void brown50grey50(Matrix4 projectionMatrix, Matrix4 viewMatrix)
        {
            float[] lightPositionInEyeSpace = viewMatrix.transformFloatVector(lightPosition);
            Matrix4 modelRotate = Matrix4.multiply(Matrix4.newRotate(rotation, 1, 1, 0));

            Matrix4 redTranslation = Matrix4.newTranslation(-3, 2, -5);
            Matrix4 blueTranslation = Matrix4.newTranslation(0, 2, -5);
            Matrix4 brownTranslation = Matrix4.newTranslation(3, 2, -5);
            Matrix4 greyTranslation = Matrix4.newTranslation(0, -2, -5);

            Matrix4 viewModelMatrix;


            baseObjectManager.bindProgram(brownMesh);
            for (int i = 0; i < 50; i++)
            {
                viewModelMatrix = Matrix4.multiply(viewMatrix, brownTranslation, modelRotate);
                normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                baseObjectManager.setDefaultUniformValues(brownMesh);
                texProgram.setUniformValues(
                        new String[]{
                                ShadConst.EYE_PROJECTION_MATRIX_UNIFORM,
                                ShadConst.EYE_TRANSFORM_MATRIX_UNIFORM,
                                ShadConst.EYE_NORMAL_MATRIX_UNIFORM,
                                ShadConst.AMBIENT_LIGHT_COLOR_UNIFORM,
                                ShadConst.DIFF_LIGHT_COLOR_UNIFORM,
                                ShadConst.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                ShadConst.LIGHT_POS_EYE_UNIFORM,
                                ShadConst.DIFF_MATERIAL_TEXTURE_UNIFORM
                        },
                        new Object[]{
                                projectionMatrix.m,
                                viewModelMatrix.m,
                                normalTransform.m,
                                new float[]{0.2f, 0.2f, 0.2f, 1.0f},
                                new float[]{0.5f, 0.5f, 0.6f, 1.0f},
                                new float[]{1.0f, 1.0f, 1.0f, 1.0f},
                                lightPositionInEyeSpace,
                                bbTex
                        });
                baseObjectManager.renderIndexBuffer(brownMesh);
            }
            baseObjectManager.bindProgram(greyMesh);
            for (int i = 0; i < 50; i++)
            {
                viewModelMatrix = Matrix4.multiply(viewMatrix, brownTranslation, modelRotate);
                normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                baseObjectManager.setDefaultUniformValues(greyMesh);
                texProgram.setUniformValues(
                        new String[]{
                                ShadConst.EYE_PROJECTION_MATRIX_UNIFORM,
                                ShadConst.EYE_TRANSFORM_MATRIX_UNIFORM,
                                ShadConst.EYE_NORMAL_MATRIX_UNIFORM,
                                ShadConst.AMBIENT_LIGHT_COLOR_UNIFORM,
                                ShadConst.DIFF_LIGHT_COLOR_UNIFORM,
                                ShadConst.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                ShadConst.LIGHT_POS_EYE_UNIFORM,
                                ShadConst.DIFF_MATERIAL_TEXTURE_UNIFORM
                        },
                        new Object[]{
                                projectionMatrix.m,
                                viewModelMatrix.m,
                                normalTransform.m,
                                new float[]{0.2f, 0.2f, 0.2f, 1.0f},
                                new float[]{0.5f, 0.5f, 0.6f, 1.0f},
                                new float[]{1.0f, 1.0f, 1.0f, 1.0f},
                                lightPositionInEyeSpace,
                                greyTex
                        });
                baseObjectManager.renderIndexBuffer(greyMesh);
            }
        }
    }
}