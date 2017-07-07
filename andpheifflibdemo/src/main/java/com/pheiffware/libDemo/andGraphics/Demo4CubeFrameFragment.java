package com.pheiffware.libDemo.andGraphics;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.gui.graphics.openGL.BaseGameFragment;
import com.pheiffware.lib.and.gui.graphics.openGL.SurfaceMetrics;
import com.pheiffware.lib.geometry.collada.ColladaMaterial;
import com.pheiffware.lib.graphics.Camera;
import com.pheiffware.lib.graphics.Color4F;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.engine.ColladaLoader;
import com.pheiffware.lib.graphics.managed.engine.ObjectHandle;
import com.pheiffware.lib.graphics.managed.engine.ObjectManager;
import com.pheiffware.lib.graphics.managed.engine.renderers.CubeDepthRenderer;
import com.pheiffware.lib.graphics.managed.engine.renderers.SimpleRenderer;
import com.pheiffware.lib.graphics.managed.frameBuffer.FrameBuffer;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.techniques.ColorShadowMaterialTechnique;
import com.pheiffware.lib.graphics.managed.techniques.TextureMaterialTechnique;
import com.pheiffware.lib.graphics.managed.texture.Texture2D;
import com.pheiffware.lib.graphics.managed.texture.TextureCubeMap;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;
import com.pheiffware.libDemo.Demo3DRenderer;
import com.pheiffware.libDemo.DemoGameView;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Steve on 6/19/2017.
 */

public class Demo4CubeFrameFragment extends BaseGameFragment
{
    @Override
    public DemoGameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new DemoGameView(getContext(), new Demo4CubeFrameFragment.Renderer(), FilterQuality.MEDIUM, false, true);
    }

    static class DemoColladaLoader extends ColladaLoader
    {
        private final Technique colorTechnique;
        private final Technique textureTechnique;

        public DemoColladaLoader(ObjectManager objectManager,
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

    private static class Renderer extends Demo3DRenderer
    {
        private final float maximumLightDistance = 25.0f;
        private Lighting lighting;
        private ObjectManager manager;
        private ColorShadowMaterialTechnique colorShadowTechnique;

        private TextureMaterialTechnique textureTechnique;
        private TextureCubeMap cubeDepthTexture;
        private SimpleRenderer simpleRenderer;
        private CubeDepthRenderer cubeRenderer;
        private ObjectHandle monkeyHandle;
        private Matrix4 monkeyTransform;

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

            PheiffGLUtils.enableAlphaTransparency();
            colorShadowTechnique = new ColorShadowMaterialTechnique(al);
            textureTechnique = new TextureMaterialTechnique(al);

            cubeDepthTexture = glCache.buildCubeDepthTex(512, 512).build();
            lighting = new Lighting(new float[]{0.2f, 0.2f, 0.2f, 1.0f}, new float[]{1, 1, 2, 1}, new float[]{1.0f, 1.0f, 1.0f, 1.0f});

            //Left Cube: -2,0,-2
            //Right Cube: 0,0,0
            //Screen: -3,-3,-7


            lighting.setMaximumDistance(0, maximumLightDistance);
            simpleRenderer = new SimpleRenderer();
            cubeRenderer = new CubeDepthRenderer(al, cubeDepthTexture);

            manager = new ObjectManager();
            ColladaMaterial defaultMaterial = new ColladaMaterial(
                    "default",
                    null,
                    new Color4F(1.0f, 1.0f, 1.0f, 1.0f),
                    new Color4F(1.0f, 1.0f, 1.0f, 1.0f),
                    new Color4F(1.0f, 1.0f, 1.0f, 1.0f), 1.0f);
            DemoColladaLoader loader = new DemoColladaLoader(
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
                monkeyHandle = loader.loadCollada("meshes/test_render.dae", "other").get("Monkey");
                loader.loadCollada("meshes/shadows.dae");
                monkeyTransform = Matrix4.newTranslation(0.5f, 1f, -2f);
                monkeyTransform.scaleBy(0.7f, 0.7f, 0.7f);
                manager.packAndTransfer();
            }
            catch (XMLParseException | IOException e)
            {
                throw new RuntimeException("Failure", e);
            }
        }

        @Override
        protected void onDrawFrame(Camera camera) throws GraphicsException
        {
            monkeyTransform.rotateBy(1f, 1f, 0f, 1f);
            monkeyHandle.setProperty(RenderProperty.MODEL_MATRIX, monkeyTransform);

            //Render to texture 1st
            float[] absoluteLightPosition = Arrays.copyOfRange(lighting.getPositions(), 0, 4);
            cubeRenderer.setRenderPosition(absoluteLightPosition);
            cubeRenderer.add(manager.getGroupObjects("main"));
            cubeRenderer.add(monkeyHandle);
            cubeRenderer.render();

            colorShadowTechnique.setProperty(RenderProperty.PROJECTION_LINEAR_DEPTH, camera.getProjectionLinearDepth());
            colorShadowTechnique.setProperty(RenderProperty.VIEW_MATRIX, camera.getViewMatrix());
            colorShadowTechnique.setProperty(RenderProperty.LIGHTING, lighting);
            colorShadowTechnique.setProperty(RenderProperty.CUBE_DEPTH_TEXTURE, cubeDepthTexture);
            colorShadowTechnique.applyConstantProperties();


            //Remove bindings to frame buffers
            FrameBuffer.main.bind(0, 0, getRenderWidth(), getRenderHeight());

            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            GLES20.glClearDepthf(1);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            simpleRenderer.add(manager.getGroupObjects("main"));
            simpleRenderer.add(monkeyHandle);
            simpleRenderer.render();
        }
    }
}
