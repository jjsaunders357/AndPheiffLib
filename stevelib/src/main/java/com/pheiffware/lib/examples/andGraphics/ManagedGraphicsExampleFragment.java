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
import com.pheiffware.lib.graphics.managed.SingleTechniqueGraphicsManager;
import com.pheiffware.lib.graphics.managed.engine.MeshRenderHandle;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.techniques.ColorMaterialTechnique;
import com.pheiffware.lib.graphics.managed.vertexBuffer.StaticVertexBuffer;
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
        private final Lighting lighting = new Lighting(new float[]{-3, 3, 0, 1, 3, 3, 0, 1}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.1f, 0.1f, 1.0f});
        private final Lighting alternateLighting = new Lighting(new float[]{3, 3, 0, 1}, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        private final float[] ambientLightColor = new float[]{0.2f, 0.2f, 0.2f, 1.0f};
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

                StaticVertexBuffer colorBuffer = new StaticVertexBuffer(new VertexAttribute[]{VertexAttribute.POSITION, VertexAttribute.NORMAL});

                graphicsManager = new SingleTechniqueGraphicsManager(new StaticVertexBuffer[]{colorBuffer}, new Technique[]{colorTechnique});
                monkeyMesh = graphicsManager.addTransferMesh(monkey.getMesh(0), colorBuffer, colorTechnique,
                        new RenderPropertyValue[]{
                                new RenderPropertyValue(RenderProperty.MAT_COLOR, new float[]{0.0f, 0.6f, 0.9f, 1.0f}),
                                new RenderPropertyValue(RenderProperty.SPEC_MAT_COLOR, new float[]{0.75f, 0.85f, 1.0f, 1.0f}),
                                new RenderPropertyValue(RenderProperty.SHININESS, 30.0f)
                        });
                cubeMesh = graphicsManager.addTransferMesh(cube.getMesh(0), colorBuffer, colorTechnique,
                        new RenderPropertyValue[]{
                                new RenderPropertyValue(RenderProperty.MAT_COLOR, new float[]{0.6f, 0.8f, 0.3f, 1.0f}),
                                new RenderPropertyValue(RenderProperty.SPEC_MAT_COLOR, new float[]{1f, 1f, 1f, 1f}), //Is override later
                                new RenderPropertyValue(RenderProperty.SHININESS, 2.0f)
                        });
                sphereMesh = graphicsManager.addTransferMesh(sphere.getMesh(0), colorBuffer, colorTechnique,
                        new RenderPropertyValue[]{
                                new RenderPropertyValue(RenderProperty.MAT_COLOR, new float[]{0.5f, 0.2f, 0.2f, 1.0f}),
                                new RenderPropertyValue(RenderProperty.SPEC_MAT_COLOR, new float[]{1.0f, 0.9f, 0.8f, 1.0f}),
                                new RenderPropertyValue(RenderProperty.SHININESS, 5.0f)
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
            //Turn 2nd light on and off every 180 degrees
            lighting.setOnState(1, (rotation % 360) < 180);
            lighting.calcLightPositionsInEyeSpace(viewMatrix);
            alternateLighting.calcLightPositionsInEyeSpace(viewMatrix);
            graphicsManager.resetRender();
            graphicsManager.setDefaultPropertyValues(
                    new RenderProperty[]{
                            RenderProperty.PROJECTION_MATRIX,
                            RenderProperty.VIEW_MATRIX,
                            RenderProperty.AMBIENT_LIGHT_COLOR,
                            RenderProperty.LIGHTING,

                    },
                    new Object[]{
                            projectionMatrix,
                            viewMatrix,
                            ambientLightColor,
                            lighting
                    });

            Matrix4 modelRotate = Matrix4.multiply(Matrix4.newRotate(rotation, 1, 1, 0));
            Matrix4 modelMatrix;


            Matrix4 monkeyTranslation = Matrix4.newTranslation(-3, 2, -5);
            modelMatrix = Matrix4.multiply(monkeyTranslation, modelRotate);
            graphicsManager.submitRender(monkeyMesh,
                    new RenderProperty[]{
                            RenderProperty.MODEL_MATRIX
                    },
                    new Object[]{
                            modelMatrix
                    }
            );

            Matrix4 cubeTranslation = Matrix4.newTranslation(0, 2, -5);
            modelMatrix = Matrix4.multiply(cubeTranslation, modelRotate);
            graphicsManager.submitRender(cubeMesh,
                    new RenderProperty[]{
                            RenderProperty.MODEL_MATRIX,
                            RenderProperty.LIGHTING //Overridden default property value.  The other meshes will use the default value.
                    },
                    new Object[]{
                            modelMatrix,
                            alternateLighting
                    });


            Matrix4 sphereTranslation = Matrix4.newTranslation(3, 2, -5);
            modelMatrix = Matrix4.multiply(sphereTranslation, modelRotate);
            graphicsManager.submitRender(sphereMesh,
                    new RenderProperty[]{
                            RenderProperty.MODEL_MATRIX
                    },
                    new Object[]{
                            modelMatrix
                    });

            graphicsManager.render();
            rotation++;
        }
    }
}