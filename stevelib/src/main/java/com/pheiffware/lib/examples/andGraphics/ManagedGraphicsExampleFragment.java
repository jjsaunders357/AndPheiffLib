package com.pheiffware.lib.examples.andGraphics;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLFragment;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.engine.MeshRenderHandle;
import com.pheiffware.lib.graphics.managed.engine.SingleTechniqueGraphicsManager;
import com.pheiffware.lib.graphics.managed.program.Attribute;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.techniques.ColorMaterialTechnique;
import com.pheiffware.lib.graphics.techniques.PropertyValue;
import com.pheiffware.lib.graphics.techniques.TechniqueProperty;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;

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
        private final float[] lightPosition = new float[]{-3, 3, 0, 1};
        private final float[] ambientLightColor = new float[]{0.2f, 0.2f, 0.2f, 1.0f};
        private final float[] lightColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        private float rotation = 0;
        private SingleTechniqueGraphicsManager graphicsManager;
        private MeshRenderHandle<Technique> monkeyMesh;
        private MeshRenderHandle<Technique> sphereMesh;
        private MeshRenderHandle<Technique> cubeMesh;

        public ExampleRenderer()
        {
            super(90f, 1.0f, 100.0f, 0.01f);
        }


        @Override
        public void onSurfaceCreated(AssetLoader al, GLCache glCache) throws GraphicsException
        {
            super.onSurfaceCreated(al, glCache);
            try
            {
                Technique colorTechnique = new ColorMaterialTechnique(al);
                ColladaFactory colladaFactory = new ColladaFactory(true);
                Collada collada = colladaFactory.loadCollada(al, "meshes/test_render.dae");

                //Lookup object from loaded file by "name" (what user named it in editing tool)
                ColladaObject3D monkey = collada.objects.get("Monkey");
                ColladaObject3D sphere = collada.objects.get("Sphere");
                ColladaObject3D cube = collada.objects.get("Cube");

                StaticVertexBuffer colorBuffer = new StaticVertexBuffer(new Attribute[]{Attribute.POSITION, Attribute.NORMAL});

                graphicsManager = new SingleTechniqueGraphicsManager(new StaticVertexBuffer[]{colorBuffer}, new Technique[]{colorTechnique});
                monkeyMesh = graphicsManager.addMesh(monkey.getMesh(0), colorBuffer, colorTechnique,
                        new PropertyValue[]{
                                new PropertyValue(TechniqueProperty.MAT_COLOR, new float[]{0.0f, 0.6f, 0.9f, 1.0f}),
                                new PropertyValue(TechniqueProperty.SPEC_MAT_COLOR, new float[]{0.75f, 0.85f, 1.0f, 1.0f}),
                                new PropertyValue(TechniqueProperty.SHININESS, 30.0f)
                        });
                cubeMesh = graphicsManager.addMesh(cube.getMesh(0), colorBuffer, colorTechnique,
                        new PropertyValue[]{
                                new PropertyValue(TechniqueProperty.MAT_COLOR, new float[]{0.6f, 0.8f, 0.3f, 1.0f}),
                                new PropertyValue(TechniqueProperty.SPEC_MAT_COLOR, new float[]{1f, 1f, 1f, 1f}), //Is override later
                                new PropertyValue(TechniqueProperty.SHININESS, 2.0f)
                        });
                sphereMesh = graphicsManager.addMesh(sphere.getMesh(0), colorBuffer, colorTechnique,
                        new PropertyValue[]{
                                new PropertyValue(TechniqueProperty.MAT_COLOR, new float[]{0.5f, 0.2f, 0.2f, 1.0f}),
                                new PropertyValue(TechniqueProperty.SPEC_MAT_COLOR, new float[]{1.0f, 0.9f, 0.8f, 1.0f}),
                                new PropertyValue(TechniqueProperty.SHININESS, 5.0f)
                        });
                graphicsManager.transfer();
            }
            catch (XMLParseException | IOException exception)
            {
                throw new GraphicsException(exception);
            }
        }

        @Override
        protected void onDrawFrame(Matrix4 projectionMatrix, Matrix4 viewMatrix) throws GraphicsException
        {
            graphicsManager.resetRender();
            graphicsManager.setDefaultPropertyValues(
                    new TechniqueProperty[]{
                            TechniqueProperty.PROJECTION_MATRIX,
                            TechniqueProperty.VIEW_MATRIX,
                            TechniqueProperty.AMBIENT_LIGHT_COLOR,
                            TechniqueProperty.LIGHT_COLOR,
                            TechniqueProperty.LIGHT_POS,

                    },
                    new Object[]{
                            projectionMatrix,
                            viewMatrix,
                            ambientLightColor,
                            lightColor,
                            lightPosition
                    });

            Matrix4 modelRotate = Matrix4.multiply(Matrix4.newRotate(rotation, 1, 1, 0));
            Matrix4 modelMatrix;


            Matrix4 monkeyTranslation = Matrix4.newTranslation(-3, 2, -5);
            modelMatrix = Matrix4.multiply(monkeyTranslation, modelRotate);
            graphicsManager.submitRender(monkeyMesh,
                    new TechniqueProperty[]{
                            TechniqueProperty.MODEL_MATRIX,
                            TechniqueProperty.LIGHT_POS //Overridden default property value.  The other meshes will use the default value.
                    },
                    new Object[]{
                            modelMatrix,
                            new float[]{3, 3, 0, 1}
                    }
            );

            Matrix4 cubeTranslation = Matrix4.newTranslation(0, 2, -5);
            modelMatrix = Matrix4.multiply(cubeTranslation, modelRotate);
            graphicsManager.submitRender(cubeMesh,
                    new TechniqueProperty[]{
                            TechniqueProperty.MODEL_MATRIX,
                            //Override default to make null
                            TechniqueProperty.SPEC_MAT_COLOR,
                    },
                    new Object[]{
                            modelMatrix,
                            new float[]{0.2f, 0.2f, 0.2f, 1.0f}
                    });


            Matrix4 sphereTranslation = Matrix4.newTranslation(3, 2, -5);
            modelMatrix = Matrix4.multiply(sphereTranslation, modelRotate);
            graphicsManager.submitRender(sphereMesh,
                    new TechniqueProperty[]{
                            TechniqueProperty.MODEL_MATRIX
                    },
                    new Object[]{
                            modelMatrix
                    });

            graphicsManager.render();
            rotation++;
        }
    }
}