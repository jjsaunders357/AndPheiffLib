package com.pheiffware.lib.examples.andGraphics;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.gui.graphics.openGL.BaseGameFragment;
import com.pheiffware.lib.and.gui.graphics.openGL.SurfaceMetrics;
import com.pheiffware.lib.and.gui.graphics.openGL.TouchTransformGameView;
import com.pheiffware.lib.geometry.collada.ColladaMaterial;
import com.pheiffware.lib.graphics.Color4F;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.engine.newEngine.ColladaLoader;
import com.pheiffware.lib.graphics.managed.engine.newEngine.ObjectManager;
import com.pheiffware.lib.graphics.managed.engine.newEngine.renderers.CubeDepthRenderer;
import com.pheiffware.lib.graphics.managed.engine.newEngine.renderers.SimpleRenderer;
import com.pheiffware.lib.graphics.managed.frameBuffer.FrameBuffer;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.techniques.ColorShadowMaterialTechnique;
import com.pheiffware.lib.graphics.managed.techniques.TextureMaterialTechnique;
import com.pheiffware.lib.graphics.managed.texture.Texture2D;
import com.pheiffware.lib.graphics.managed.texture.TextureCubeMap;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Steve on 6/19/2017.
 */

public class Example4CubeFrameFragment extends BaseGameFragment
{
    @Override
    public TouchTransformGameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new TouchTransformGameView(getContext(), new Example4CubeFrameFragment.Renderer(), FilterQuality.MEDIUM, false, true);
    }

    static class ExampleColladaLoader extends ColladaLoader
    {
        private final Technique colorTechnique;
        private final Technique textureTechnique;

        public ExampleColladaLoader(ObjectManager objectManager,
                                    GLCache glCache,
                                    AssetLoader al,
                                    String imageDirectory,
                                    boolean homogenizePositions,
                                    ColladaMaterial defaultColladaMaterial,
                                    Technique colorTechnique,
                                    Technique textureTechnique) throws GraphicsException
        {
            super(objectManager, glCache, al, imageDirectory, homogenizePositions, defaultColladaMaterial);
            this.colorTechnique = colorTechnique;
            this.textureTechnique = textureTechnique;
        }

        @Override
        protected void addMesh(Mesh mesh, ColladaMaterial material, Matrix4 initialMatrix, String name)
        {
            Technique technique;
            RenderPropertyValue[] renderProperties;

            if (material.imageFileName == null)
            {
                technique = colorTechnique;
                renderProperties = new RenderPropertyValue[]
                        {
                                new RenderPropertyValue(RenderProperty.MODEL_MATRIX, initialMatrix),
                                new RenderPropertyValue(RenderProperty.MAT_COLOR, material.diffuseColor.comps),
                                new RenderPropertyValue(RenderProperty.SPEC_MAT_COLOR, material.specularColor.comps),
                                new RenderPropertyValue(RenderProperty.SHININESS, material.shininess)
                        };
            }
            else
            {
                technique = textureTechnique;
                renderProperties = new RenderPropertyValue[]
                        {
                                new RenderPropertyValue(RenderProperty.MODEL_MATRIX, initialMatrix),
                                new RenderPropertyValue(RenderProperty.MAT_COLOR_TEXTURE, glCache.getTexture(getTexturePath(material.imageFileName))),
                                new RenderPropertyValue(RenderProperty.SPEC_MAT_COLOR, material.specularColor.comps),
                                new RenderPropertyValue(RenderProperty.SHININESS, material.shininess)
                        };
            }

            objectManager.addStaticMesh(mesh, technique, renderProperties);
        }


        @Override
        protected Texture2D loadTexture2D(String imagePath) throws GraphicsException
        {
            return glCache.buildImageTex(imagePath).build();
        }
    }

    private static class Renderer extends Example3DRenderer
    {
        private Lighting lighting;
        private ObjectManager manager;
        //private ColorMaterialTechnique colorTechnique;
        private ColorShadowMaterialTechnique colorShadowTechnique;

        private TextureMaterialTechnique textureTechnique;
        private TextureCubeMap cubeDepthTexture;
        private SimpleRenderer simpleRenderer;
        private CubeDepthRenderer cubeRenderer;

        public Renderer()
        {
            super(90f, 1.0f, 100.0f, 0.01f);
        }

        @Override
        public int maxMajorGLVersion()
        {
            return 3;
        }

        @Override
        public void onSurfaceCreated(AssetLoader al, GLCache glCache, SurfaceMetrics surfaceMetrics) throws GraphicsException
        {
            super.onSurfaceCreated(al, glCache, surfaceMetrics);
            colorShadowTechnique = new ColorShadowMaterialTechnique(al);
            textureTechnique = new TextureMaterialTechnique(al);

            cubeDepthTexture = glCache.buildCubeDepthTex(512, 512).build();
            lighting = new Lighting(new float[]{-1.5f, 1, 0, 1}, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
            simpleRenderer = new SimpleRenderer();
            cubeRenderer = new CubeDepthRenderer(al, cubeDepthTexture);

            manager = new ObjectManager();
            ColladaMaterial defaultMaterial = new ColladaMaterial(
                    "default",
                    null,
                    new Color4F(1.0f, 1.0f, 1.0f, 1.0f),
                    new Color4F(1.0f, 1.0f, 1.0f, 1.0f),
                    new Color4F(1.0f, 1.0f, 1.0f, 1.0f), 1.0f);
            ExampleColladaLoader loader = new ExampleColladaLoader(
                    manager,
                    glCache,
                    al,
                    "images", //Where images are located
                    true, //Homogenize coordinates
                    defaultMaterial,
                    colorShadowTechnique,
                    textureTechnique);
            try
            {
                loader.loadCollada("meshes/shadows.dae");
                manager.packAndTransfer();
            }
            catch (XMLParseException | IOException e)
            {
                throw new RuntimeException("Failure", e);
            }
        }

        @Override
        protected void onDrawFrame(Matrix4 projectionMatrix, Matrix4 viewMatrix) throws GraphicsException
        {
            //Render to texture 1st
            float[] absoluteLightPosition = Arrays.copyOfRange(lighting.getPositions(), 0, 4);

            //Extract eye position from current view
            float[] position = new float[]{-viewMatrix.m[12], -viewMatrix.m[13], -viewMatrix.m[14]};

            cubeRenderer.setRenderPosition(absoluteLightPosition);
            cubeRenderer.add(manager.getGroupObjects("main"));
            cubeRenderer.render();

            lighting.calcOnLightPositionsInEyeSpace(viewMatrix);

            colorShadowTechnique.setProperty(RenderProperty.PROJECTION_MATRIX, projectionMatrix);
            colorShadowTechnique.setProperty(RenderProperty.VIEW_MATRIX, viewMatrix);
            colorShadowTechnique.setProperty(RenderProperty.AMBIENT_LIGHT_COLOR, new float[]{0.2f, 0.2f, 0.2f, 1.0f});
            colorShadowTechnique.setProperty(RenderProperty.LIGHTING, lighting);
            colorShadowTechnique.setProperty(RenderProperty.CUBE_DEPTH_TEXTURE, cubeDepthTexture);

            //Remove bindings to frame buffers
            FrameBuffer.main.bind(0, 0, getRenderWidth(), getRenderHeight());

            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            GLES20.glClearDepthf(1);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            simpleRenderer.add(manager.getGroupObjects("main"));
            simpleRenderer.render();
        }
    }
}
