package com.pheiffware.lib.examples.andGraphics;

import android.opengl.GLES20;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLFragment;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.geometry.collada.ColladaMaterial;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.ColladaGraphicsLoader;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.SingleTechniqueGraphicsManager;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.engine.ObjectRenderHandle;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.techniques.ColorMaterialTechnique;
import com.pheiffware.lib.graphics.techniques.TextureMaterialTechnique;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;

/**
 * Demonstrates using a ColladaLoader to load objects directly into a GraphicsManager.
 * <p/>
 * Created by Steve on 4/25/2016.
 */
public class ColladaLoaderExampleFragment extends SimpleGLFragment
{
    public ColladaLoaderExampleFragment()
    {
        super(new ColladaGraphicsExample(), FilterQuality.MEDIUM);
    }

    private static class ColladaGraphicsExample extends Base3DExampleRenderer
    {
        private final float[] lightPosition = new float[]{-3, 3, 0, 1};
        private final float[] ambientLightColor = new float[]{0.2f, 0.2f, 0.2f, 1.0f};
        private final float[] lightColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        private float rotation = 0;
        private ObjectRenderHandle<Technique> multiCubeHandle;
        private Matrix4 multiCubeTranslation = Matrix4.newTranslation(-3, 2, -5);
        private SingleTechniqueGraphicsManager graphicsManager;
        public ColladaGraphicsExample()
        {
            super(90f, 1.0f, 100.0f, 0.01f);
        }

        @Override
        public void onSurfaceCreated(AssetLoader al, final GLCache glCache) throws GraphicsException
        {
            super.onSurfaceCreated(al, glCache);
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
                ColladaGraphicsLoader<Technique> colladaGraphicsLoader = new ColladaGraphicsLoader<Technique>(graphicsManager)
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

                ColladaObject3D multiCube = collada.objects.get("multi");
                multiCubeHandle = colladaGraphicsLoader.addColladaObject(multiCube);
                graphicsManager.transfer();

                //TODO: Texture management should be part of rendering system
                glCache.getTexture("stripes.jpg").bindToSampler(0);
                glCache.getTexture("grey_brick.jpg").bindToSampler(1);
                glCache.getTexture("brown_brick.jpg").bindToSampler(2);

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
                    new RenderProperty[]{
                            RenderProperty.PROJECTION_MATRIX,
                            RenderProperty.VIEW_MATRIX,
                            RenderProperty.AMBIENT_LIGHT_COLOR,
                            RenderProperty.LIGHT_COLOR,
                            RenderProperty.LIGHT_POS,

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
            modelMatrix = Matrix4.multiply(multiCubeTranslation, modelRotate);
            graphicsManager.submitRender(multiCubeHandle,
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
