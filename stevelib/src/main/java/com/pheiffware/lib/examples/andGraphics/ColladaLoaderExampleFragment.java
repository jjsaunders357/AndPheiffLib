package com.pheiffware.lib.examples.andGraphics;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.gui.graphics.openGL.BaseGameFragment;
import com.pheiffware.lib.and.gui.graphics.openGL.SurfaceMetrics;
import com.pheiffware.lib.and.gui.graphics.openGL.TouchTransformGameView;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.geometry.collada.ColladaMaterial;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.ColladaGraphicsLoader;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.SingleTechniqueGraphicsManager;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.techniques.ColorMaterialTechnique;
import com.pheiffware.lib.graphics.managed.techniques.TextureMaterialTechnique;
import com.pheiffware.lib.graphics.managed.vertexBuffer.StaticVertexBuffer;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;

/**
 * Demonstrates using a ColladaLoader to load objects directly into a GraphicsManager.
 * <p/>
 * Created by Steve on 4/25/2016.
 */
public class ColladaLoaderExampleFragment extends BaseGameFragment
{
    @Override
    public TouchTransformGameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new TouchTransformGameView(getContext(), new Renderer(), FilterQuality.MEDIUM, false, true);
    }

    private static class Renderer extends Example3DRenderer
    {
        private final Lighting lighting = new Lighting(new float[]{-3, 3, 0, 1}, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        private final float[] ambientLightColor = new float[]{0.2f, 0.2f, 0.2f, 1.0f};
        private float rotation = 0;
        //        private ObjectRenderHandle<Technique> multiCubeHandle;
        private Matrix4 multiCubeTranslation = Matrix4.newTranslation(-3, 2, -5);
        private SingleTechniqueGraphicsManager graphicsManager;
        private ColladaGraphicsLoader<Technique> colladaGraphicsLoader;

        public Renderer()
        {
            super(90f, 1.0f, 100.0f, 0.01f);
        }

        @Override
        public void onSurfaceCreated(AssetLoader al, final GLCache glCache, SurfaceMetrics surfaceMetrics) throws GraphicsException
        {
            super.onSurfaceCreated(al, glCache, surfaceMetrics);
            try
            {
                final Technique textureTechnique = new TextureMaterialTechnique(al);
                final Technique colorTechnique = new ColorMaterialTechnique(al);
                final StaticVertexBuffer colorBuffer = new StaticVertexBuffer(new VertexAttribute[]{VertexAttribute.POSITION, VertexAttribute.NORMAL});
                final StaticVertexBuffer textureBuffer = new StaticVertexBuffer(new VertexAttribute[]{VertexAttribute.POSITION, VertexAttribute.NORMAL, VertexAttribute.TEXCOORD});

                ColladaFactory colladaFactory = new ColladaFactory(true);

                Collada collada = colladaFactory.loadCollada(al, "meshes/cubes.dae");
                ColladaGraphicsLoader.quickLoadTextures(glCache, "images", collada, GLES20.GL_CLAMP_TO_EDGE);

                graphicsManager = new SingleTechniqueGraphicsManager(
                        new StaticVertexBuffer[]
                                {
                                        colorBuffer,
                                        textureBuffer
                                },
                        new Technique[]{
                                colorTechnique,
                                textureTechnique
                        });
                colladaGraphicsLoader = new ColladaGraphicsLoader<Technique>(graphicsManager)
                {
                    @Override
                    protected BufferAndMaterial<Technique> getRenderMaterial(String objectName, Mesh mesh, ColladaMaterial colladaMaterial)
                    {
                        Technique technique;
                        StaticVertexBuffer vertexBuffer;
                        RenderPropertyValue[] renderPropertyValues;
                        if (mesh.getTexCoordData() == null)
                        {
                            technique = colorTechnique;
                            vertexBuffer = colorBuffer;
                        }
                        else
                        {
                            technique = textureTechnique;
                            vertexBuffer = textureBuffer;
                        }
                        if (colladaMaterial.imageFileName == null)
                        {
                            renderPropertyValues = new RenderPropertyValue[]{
                                    new RenderPropertyValue(RenderProperty.MAT_COLOR, colladaMaterial.diffuseColor.comps),
                                    new RenderPropertyValue(RenderProperty.SHININESS, colladaMaterial.shininess),
                                    new RenderPropertyValue(RenderProperty.SPEC_MAT_COLOR, colladaMaterial.specularColor.comps)};
                        }
                        else
                        {
                            renderPropertyValues = new RenderPropertyValue[]{
                                    new RenderPropertyValue(RenderProperty.MAT_COLOR_TEXTURE, glCache.getTexture(colladaMaterial.imageFileName)),
                                    new RenderPropertyValue(RenderProperty.SHININESS, colladaMaterial.shininess),
                                    new RenderPropertyValue(RenderProperty.SPEC_MAT_COLOR, colladaMaterial.specularColor.comps)};

                        }
                        return new BufferAndMaterial<>(vertexBuffer, technique, renderPropertyValues);
                    }
                };
                colladaGraphicsLoader.addColladaObjects(collada);
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
            lighting.calcOnLightPositionsInEyeSpace(viewMatrix);
            graphicsManager.resetRender();
            graphicsManager.setDefaultPropertyValues(
                    new RenderProperty[]{
                            RenderProperty.PROJECTION_MATRIX,
                            RenderProperty.VIEW_MATRIX,
                            RenderProperty.AMBIENT_LIGHT_COLOR,
                            RenderProperty.LIGHTING
                    },
                    new Object[]{
                            projectionMatrix,
                            viewMatrix,
                            ambientLightColor,
                            lighting
                    });

//                ColladaObject3D multiCube = collada.objects.get("multi");
//                multiCubeHandle = colladaGraphicsLoader.addColladaObject(multiCube);

            Matrix4 modelRotate = Matrix4.multiply(Matrix4.newRotate(rotation, 1, 1, 0));
            Matrix4 modelMatrix;
            modelMatrix = Matrix4.multiply(multiCubeTranslation, modelRotate);
            graphicsManager.submitRender(colladaGraphicsLoader.getNamedObject("multi"),
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
