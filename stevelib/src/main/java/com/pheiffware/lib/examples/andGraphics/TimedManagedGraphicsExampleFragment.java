package com.pheiffware.lib.examples.andGraphics;

import android.opengl.GLES20;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLFragment;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.Texture;
import com.pheiffware.lib.graphics.managed.engine.BaseGraphicsManager;
import com.pheiffware.lib.graphics.managed.engine.MeshRenderHandle;
import com.pheiffware.lib.graphics.managed.engine.ObjectRenderHandle;
import com.pheiffware.lib.graphics.managed.program.Program;

/**
 * Dirty, temporary, class for profiling how different rendering schemes perform.
 */

public class TimedManagedGraphicsExampleFragment extends SimpleGLFragment
{
    public TimedManagedGraphicsExampleFragment()
    {
        super(new ExampleRenderer(), FilterQuality.MEDIUM);
    }

    private static class ExampleRenderer extends Base3DExampleRenderer
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
        private Texture bbTex;
        private Texture greyTex;
        private Program texProgram;
        private Program colorProgram;

        public ExampleRenderer()
        {
            super(90f, 1.0f, 100.0f, 0.01f);
        }


        @Override
        public void onSurfaceCreated(AssetLoader al, GLCache GLCache) throws GraphicsException
        {
            super.onSurfaceCreated(al, GLCache);
            /*
            try
            {
                colorProgram = new Program(al, "shaders/vert_mncl.glsl", "shaders/frag_mncl.glsl");
                texProgram = new Program(al, "shaders/vert_mntl.glsl", "shaders/frag_mntl.glsl");
                bbTex = GLCache.createImageTexture("images/brown_brick.jpg", true, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
                greyTex = GLCache.createImageTexture("images/grey_brick.jpg", true, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);


                ColladaFactory colladaFactory = new ColladaFactory(true);
                InputStream inputStream = al.getInputStream("meshes/cubes.dae");
                Collada collada = colladaFactory.loadCollada(inputStream);


                //Lookup object from loaded file by "name" (what user named it in editing tool)
                ColladaObject3D red = collada.objects.get("red");
                ColladaObject3D blue = collada.objects.get("blue");
                ColladaObject3D brown = collada.objects.get("brown");
                ColladaObject3D grey = collada.objects.get("grey");

                baseObjectManager = new BaseGraphicsManager(new Program[]{colorProgram, texProgram});
                redMesh = baseObjectManager.addMesh(red.getMesh(0), colorProgram, new PropertyValue[]{new PropertyValue(StdUniforms.SHININESS_UNIFORM, 30.0f)});
                blueMesh = baseObjectManager.addMesh(blue.getMesh(0), colorProgram, new PropertyValue[]{new PropertyValue(StdUniforms.SHININESS_UNIFORM, 30.0f)});
                brownMesh = baseObjectManager.addMesh(brown.getMesh(0), texProgram, new PropertyValue[]{new PropertyValue(StdUniforms.SHININESS_UNIFORM, 30.0f)});
                greyMesh = baseObjectManager.addMesh(grey.getMesh(0), texProgram, new PropertyValue[]{new PropertyValue(StdUniforms.SHININESS_UNIFORM, 30.0f)});

                baseObjectManager.transfer();
            }
            catch (XMLParseException | IOException exception)
            {
                throw new GraphicsException(exception);
            }*/
        }


        @Override
        protected void onDrawFrame(Matrix4 projectionMatrix, Matrix4 viewMatrix) throws GraphicsException
        {
            //red50(projectionMatrix, viewMatrix);

            GLES20.glFinish();
            rotation++;
        }

/*
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
                            StdUniforms.PROJECTION_MATRIX_UNIFORM,
                            StdUniforms.VIEW_MODEL_MATRIX_UNIFORM,
                            StdUniforms.NORMAL_MATRIX_UNIFORM,
                            StdUniforms.AMBIENT_LIGHTMAT_COLOR_UNIFORM,
                            StdUniforms.DIFF_LIGHTMAT_COLOR_UNIFORM,
                            StdUniforms.SPEC_LIGHTMAT_COLOR_UNIFORM,
                            StdUniforms.LIGHT_POS_EYE_UNIFORM
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
                                StdUniforms.PROJECTION_MATRIX_UNIFORM,
                                StdUniforms.VIEW_MODEL_MATRIX_UNIFORM,
                                StdUniforms.NORMAL_MATRIX_UNIFORM,
                                StdUniforms.AMBIENT_LIGHTMAT_COLOR_UNIFORM,
                                StdUniforms.DIFF_LIGHTMAT_COLOR_UNIFORM,
                                StdUniforms.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_POS_EYE_UNIFORM
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
                                StdUniforms.PROJECTION_MATRIX_UNIFORM,
                                StdUniforms.VIEW_MODEL_MATRIX_UNIFORM,
                                StdUniforms.NORMAL_MATRIX_UNIFORM,
                                StdUniforms.AMBIENT_LIGHTMAT_COLOR_UNIFORM,
                                StdUniforms.DIFF_LIGHTMAT_COLOR_UNIFORM,
                                StdUniforms.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_POS_EYE_UNIFORM
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
                                StdUniforms.PROJECTION_MATRIX_UNIFORM,
                                StdUniforms.VIEW_MODEL_MATRIX_UNIFORM,
                                StdUniforms.NORMAL_MATRIX_UNIFORM,
                                StdUniforms.AMBIENT_LIGHT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_COLOR_UNIFORM,
                                StdUniforms.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_POS_EYE_UNIFORM,
                                StdUniforms.MATERIAL_SAMPLER_UNIFORM
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
                                StdUniforms.PROJECTION_MATRIX_UNIFORM,
                                StdUniforms.VIEW_MODEL_MATRIX_UNIFORM,
                                StdUniforms.NORMAL_MATRIX_UNIFORM,
                                StdUniforms.AMBIENT_LIGHTMAT_COLOR_UNIFORM,
                                StdUniforms.DIFF_LIGHTMAT_COLOR_UNIFORM,
                                StdUniforms.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_POS_EYE_UNIFORM
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
                                StdUniforms.PROJECTION_MATRIX_UNIFORM,
                                StdUniforms.VIEW_MODEL_MATRIX_UNIFORM,
                                StdUniforms.NORMAL_MATRIX_UNIFORM,
                                StdUniforms.AMBIENT_LIGHT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_COLOR_UNIFORM,
                                StdUniforms.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_POS_EYE_UNIFORM,
                                StdUniforms.MATERIAL_SAMPLER_UNIFORM
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
                                StdUniforms.PROJECTION_MATRIX_UNIFORM,
                                StdUniforms.VIEW_MODEL_MATRIX_UNIFORM,
                                StdUniforms.NORMAL_MATRIX_UNIFORM,
                                StdUniforms.AMBIENT_LIGHT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_COLOR_UNIFORM,
                                StdUniforms.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_POS_EYE_UNIFORM,
                                StdUniforms.MATERIAL_SAMPLER_UNIFORM
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
                                StdUniforms.PROJECTION_MATRIX_UNIFORM,
                                StdUniforms.VIEW_MODEL_MATRIX_UNIFORM,
                                StdUniforms.NORMAL_MATRIX_UNIFORM,
                                StdUniforms.AMBIENT_LIGHT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_COLOR_UNIFORM,
                                StdUniforms.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_POS_EYE_UNIFORM
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
                GLES20.glUniform1i(texProgram.getUniform(StdUniforms.MATERIAL_SAMPLER_UNIFORM).location, 0);

                baseObjectManager.renderIndexBuffer(brownMesh);

                viewModelMatrix = Matrix4.multiply(viewMatrix, greyTranslation, modelRotate);
                normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
                baseObjectManager.setDefaultUniformValues(greyMesh);
                texProgram.setUniformValues(
                        new String[]{
                                StdUniforms.PROJECTION_MATRIX_UNIFORM,
                                StdUniforms.VIEW_MODEL_MATRIX_UNIFORM,
                                StdUniforms.NORMAL_MATRIX_UNIFORM,
                                StdUniforms.AMBIENT_LIGHT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_COLOR_UNIFORM,
                                StdUniforms.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_POS_EYE_UNIFORM
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
                GLES20.glUniform1i(texProgram.getUniform(StdUniforms.MATERIAL_SAMPLER_UNIFORM).location, 1);

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
                                StdUniforms.PROJECTION_MATRIX_UNIFORM,
                                StdUniforms.VIEW_MODEL_MATRIX_UNIFORM,
                                StdUniforms.NORMAL_MATRIX_UNIFORM,
                                StdUniforms.AMBIENT_LIGHT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_COLOR_UNIFORM,
                                StdUniforms.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_POS_EYE_UNIFORM,
                                StdUniforms.MATERIAL_SAMPLER_UNIFORM
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
                                StdUniforms.PROJECTION_MATRIX_UNIFORM,
                                StdUniforms.VIEW_MODEL_MATRIX_UNIFORM,
                                StdUniforms.NORMAL_MATRIX_UNIFORM,
                                StdUniforms.AMBIENT_LIGHT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_COLOR_UNIFORM,
                                StdUniforms.SPEC_LIGHTMAT_COLOR_UNIFORM,
                                StdUniforms.LIGHT_POS_EYE_UNIFORM,
                                StdUniforms.MATERIAL_SAMPLER_UNIFORM
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
        }*/
    }
}