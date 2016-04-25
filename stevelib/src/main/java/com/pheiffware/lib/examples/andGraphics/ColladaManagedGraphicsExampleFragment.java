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
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.engine.ColladaGraphicsManager;
import com.pheiffware.lib.graphics.managed.engine.ObjectRenderHandle;
import com.pheiffware.lib.graphics.managed.engine.PropertyValue;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.techniques.ColorMaterialTechnique;
import com.pheiffware.lib.graphics.techniques.TechniqueProperty;
import com.pheiffware.lib.graphics.techniques.TextureMaterialTechnique;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;

/**
 * TODO: Comment me!
 * <p/>
 * Created by Steve on 4/25/2016.
 */
public class ColladaManagedGraphicsExampleFragment extends SimpleGLFragment
{
    public ColladaManagedGraphicsExampleFragment()
    {
        super(new ColladaGraphicsExample(), FilterQuality.MEDIUM);
    }

    private static class ColladaGraphicsExample extends Base3DExampleRenderer
    {
        private final float[] lightPosition = new float[]{-3, 3, 0, 1};
        private final float[] ambientLightColor = new float[]{0.2f, 0.2f, 0.2f, 1.0f};
        private final float[] lightColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        private float rotation = 0;
        private ColladaGraphicsManager colladaGraphicsManager;
        private ObjectRenderHandle multiCubeHandle;
        Matrix4 multiCubeTranslation = Matrix4.newTranslation(-3, 2, -5);

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
                ColladaFactory colladaFactory = new ColladaFactory(true);

                Collada collada = colladaFactory.loadCollada(al, "meshes/cubes.dae");
                ColladaGraphicsManager.quickLoadTextures(glCache, "images", collada, GLES20.GL_CLAMP_TO_EDGE);

                ColladaObject3D multiCube = collada.objects.get("multi");
                colladaGraphicsManager = new ColladaGraphicsManager(new Technique[]{colorTechnique, textureTechnique})
                {
                    @Override
                    protected Technique getTechniqueForMesh(String objectName, Mesh mesh)
                    {
                        if (mesh.getTexCoordData() == null)
                        {
                            return colorTechnique;
                        }
                        else
                        {
                            return textureTechnique;
                        }
                    }

                    @Override
                    protected PropertyValue[] getPropertyValuesForMaterial(String objectName, ColladaMaterial material)
                    {
                        if (material.imageFileName == null)
                        {
                            return new PropertyValue[]{
                                    new PropertyValue(TechniqueProperty.MAT_COLOR, material.diffuseColor.comps),
                                    new PropertyValue(TechniqueProperty.SHININESS, material.shininess),
                                    new PropertyValue(TechniqueProperty.SPEC_MAT_COLOR, material.specularColor.comps)};
                        }
                        else
                        {
                            return new PropertyValue[]{
                                    new PropertyValue(TechniqueProperty.MAT_COLOR_TEXTURE, glCache.getTexture(material.imageFileName)),
                                    new PropertyValue(TechniqueProperty.SHININESS, material.shininess),
                                    new PropertyValue(TechniqueProperty.SPEC_MAT_COLOR, material.specularColor.comps)};

                        }
                    }
                };

                //TODO: Texture management should be part of rendering system
                glCache.getTexture("stripes.jpg").bindToSampler(0);
                glCache.getTexture("grey_brick.jpg").bindToSampler(1);
                glCache.getTexture("brown_brick.jpg").bindToSampler(2);

                multiCubeHandle = colladaGraphicsManager.addColladaObject(collada.objects.get("multi"));
                colladaGraphicsManager.transfer();
            }
            catch (XMLParseException | IOException exception)
            {
                throw new GraphicsException(exception);
            }
        }

        @Override
        protected void onDrawFrame(Matrix4 projectionMatrix, Matrix4 viewMatrix) throws GraphicsException
        {
            colladaGraphicsManager.setDefaultPropertyValues(
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
            modelMatrix = Matrix4.multiply(multiCubeTranslation, modelRotate);
            colladaGraphicsManager.renderNow(multiCubeHandle,
                    new TechniqueProperty[]{
                            TechniqueProperty.MODEL_MATRIX
                    },
                    new Object[]{
                            modelMatrix
                    });
            rotation++;
        }
    }
}
