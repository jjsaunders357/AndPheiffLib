package com.pheiffware.lib.examples.andGraphics;

import android.opengl.GLES20;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLFragment;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.ShadConst;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.engine.BaseGraphicsManager;
import com.pheiffware.lib.graphics.managed.engine.MeshRenderHandle;
import com.pheiffware.lib.graphics.managed.engine.ObjectRenderHandle;
import com.pheiffware.lib.graphics.managed.engine.UniformNameValue;
import com.pheiffware.lib.graphics.managed.program.Program;
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

    private static class ExampleRenderer extends Base3DExampleRenderer
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
        public void onSurfaceCreated(AssetLoader al, GLCache GLCache) throws GraphicsException
        {
            super.onSurfaceCreated(al, GLCache);
            try
            {
                Program testProgram = new Program(al, "shaders/vert_mncl.glsl", "shaders/frag_mncl.glsl");
                ColladaFactory colladaFactory = new ColladaFactory(true);
                InputStream inputStream = al.getInputStream("meshes/test_render.dae");
                Collada collada = colladaFactory.loadCollada(inputStream);

                //Lookup object from loaded file by "name" (what user named it in editing tool)

                ColladaObject3D monkey = collada.objects.get("Monkey");
                ColladaObject3D sphere = collada.objects.get("Sphere");
                ColladaObject3D cube = collada.objects.get("Cube");

                baseObjectManager = new BaseGraphicsManager(new Program[]{testProgram});
                monkeyMesh = baseObjectManager.addMesh(monkey.getMesh(0), testProgram, new UniformNameValue[]{new UniformNameValue(ShadConst.SHININESS_UNIFORM, 30.0f)});
                sphereMesh = baseObjectManager.addMesh(sphere.getMesh(0), testProgram, new UniformNameValue[]{new UniformNameValue(ShadConst.SHININESS_UNIFORM, 30.0f)});
                cubeMesh = baseObjectManager.addMesh(cube.getMesh(0), testProgram, new UniformNameValue[]{new UniformNameValue(ShadConst.SHININESS_UNIFORM, 30.0f)});

                baseObjectManager.transfer();

                //TODO: Fix
//Example code: Want to be able to do this
//                collada.loadObjects(baseObjectManager);
//                baseObjectManager.setGlobalUniformMatrix4("eyeTransformMatrix", viewModelMatrix.m, false);
//                baseObjectManager.render(staticMonkey, "shininess",50.0f);



            }
            catch (XMLParseException | IOException exception)
            {
                throw new GraphicsException(exception);
            }
        }

        @Override
        protected void onDrawFrame(Matrix4 projectionMatrix, Matrix4 viewMatrix) throws GraphicsException
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


            viewModelMatrix = Matrix4.multiply(viewMatrix, sphereTranslation, modelRotate);
            normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
            baseObjectManager.render(sphereMesh,
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
                            new float[]{0.6f, 0.8f, 0.3f, 1.0f},
                            new float[]{1.0f, 1.0f, 1.0f, 1.0f},
                            lightPositionInEyeSpace
                    });


            viewModelMatrix = Matrix4.multiply(viewMatrix, cubeTranslation, modelRotate);
            normalTransform.setNormalTransformFromMatrix4Fast(viewModelMatrix);
            baseObjectManager.render(cubeMesh,
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
                            new float[]{0.5f, 0.5f, 0.6f, 1.0f},
                            new float[]{1.0f, 1.0f, 1.0f, 1.0f},
                            lightPositionInEyeSpace
                    });

            rotation++;
        }
    }
}