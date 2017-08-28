package com.pheiffware.libDemo.andGraphics;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.graphics.AndGraphicsUtils;
import com.pheiffware.lib.and.gui.graphics.openGL.BaseGameFragment;
import com.pheiffware.lib.and.gui.graphics.openGL.GameView;
import com.pheiffware.lib.and.gui.graphics.openGL.SystemInfo;
import com.pheiffware.lib.and.input.TouchAnalyzer;
import com.pheiffware.lib.geometry.collada.ColladaMaterial;
import com.pheiffware.lib.graphics.EuclideanCamera;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.Projection;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.Technique;
import com.pheiffware.lib.graphics.managed.engine.ColladaLoader;
import com.pheiffware.lib.graphics.managed.engine.ObjectHandle;
import com.pheiffware.lib.graphics.managed.engine.ObjectManager;
import com.pheiffware.lib.graphics.managed.engine.renderers.CubeDepthRenderer;
import com.pheiffware.lib.graphics.managed.engine.renderers.SimpleRenderer;
import com.pheiffware.lib.graphics.managed.frameBuffer.FrameBuffer;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.GraphicsConfig;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.techniques.Std3DTechnique;
import com.pheiffware.lib.graphics.managed.texture.Texture2D;
import com.pheiffware.lib.graphics.managed.texture.TextureCubeMap;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;
import com.pheiffware.libDemo.Demo3DRenderer;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Steve on 6/19/2017.
 */

public class Demo4ShadowsFragment extends BaseGameFragment
{
    @Override
    public GameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new GameView(getContext(), new Renderer(), FilterQuality.MEDIUM, false, true);
    }

    static class DemoColladaLoader extends ColladaLoader
    {
        private final Technique colorTechnique;
        private final Technique textureTechnique;

        public DemoColladaLoader(ObjectManager objectManager,
                                 GLCache glCache,
                                 AssetLoader al,
                                 String imageDirectory,
                                 Technique colorTechnique,
                                 Technique textureTechnique) throws GraphicsException
        {
            super(objectManager, glCache, al, imageDirectory);
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
        private Std3DTechnique colorTechnique;

        private Std3DTechnique textureTechnique;
        private TextureCubeMap[] cubeDepthTextures;
        private SimpleRenderer simpleRenderer;
        private CubeDepthRenderer cubeDepthRenderer;
        private ObjectHandle monkeyHandle;
        private ObjectHandle monkeyHandle2;
        private Matrix4 monkeyTransform;

        public Renderer()
        {
            super(AndGraphicsUtils.GL_VERSION_30, AndGraphicsUtils.GL_VERSION_30, "shaders", 90f, 1.0f, 100.0f, 0.01f);
        }

        @Override
        public void onSurfaceCreated(AssetLoader al, GLCache glCache, SystemInfo systemInfo) throws GraphicsException
        {
            super.onSurfaceCreated(al, glCache, systemInfo);
            PheiffGLUtils.enableAlphaTransparency();

            colorTechnique = glCache.buildTechnique(Std3DTechnique.class, GraphicsConfig.TEXTURED_MATERIAL, false);
            textureTechnique = glCache.buildTechnique(Std3DTechnique.class, GraphicsConfig.TEXTURED_MATERIAL, true);


            getCamera().setPosition(1, 0, 2);
            lighting = new Lighting(
                    new float[]{
                            0.2f, 0.2f, 0.2f, 1.0f},  //Ambient light color
                    new float[]{
                            1.0f, 1.0f, 3, 1,         //Light 1 position
                            1.0f, 2.0f, 0, 1},        //Light 2 position
                    new float[]{
                            0.5f, 0.2f, 0.2f, 1.0f,   //Light 1 color
                            0.2f, 0.2f, 0.5f, 1.0f}); //Light 2 color

            lighting.setCastsCubeShadow(0, 1);
            lighting.setCastsCubeShadow(1, 1);
            lighting.setMaximumDistance(0, maximumLightDistance);
            cubeDepthTextures = new TextureCubeMap[Lighting.numLightsSupported];
            cubeDepthTextures[0] = glCache.buildCubeDepthTex(512, 512).build();
            cubeDepthTextures[1] = glCache.buildCubeDepthTex(512, 512).build();


            simpleRenderer = new SimpleRenderer(colorTechnique, textureTechnique);
            cubeDepthRenderer = new CubeDepthRenderer(glCache);

            manager = new ObjectManager();
            DemoColladaLoader loader = new DemoColladaLoader(
                    manager,
                    glCache,
                    al,
                    "images", //Where images are located
                    colorTechnique,
                    textureTechnique);

            try
            {
                monkeyHandle = loader.loadCollada("meshes/test_render.dae", "other").get("Monkey");
                monkeyHandle2 = monkeyHandle.copy();
                loader.loadCollada("meshes/shadows2.dae");
                monkeyTransform = Matrix4.newTranslation(0.5f, 1f, -1f);
                monkeyTransform.scaleBy(0.1f, 0.1f, 0.1f);
                manager.packAndTransfer();

                //Add objects to depth renderer, excluding the 2 objects standing in for the lights
                cubeDepthRenderer.add(manager.getGroupObjects("main"));

                //Add all objects to general rendering
                simpleRenderer.add(manager.getGroupObjects("main"));
                simpleRenderer.add(monkeyHandle);
                simpleRenderer.add(monkeyHandle2);
            }
            catch (XMLParseException | IOException e)
            {
                throw new RuntimeException("Failure", e);
            }
        }

        @Override
        protected void onDrawFrame(Projection projection, EuclideanCamera camera) throws GraphicsException
        {
            monkeyTransform.rotateBy(1f, 1f, 0f, 1f);
            monkeyHandle.setProperty(RenderProperty.MODEL_MATRIX, monkeyTransform);

            //Render to texture 1st
            float[] absoluteLightPosition = Arrays.copyOfRange(lighting.getPositions(), 0, 4);
            cubeDepthRenderer.setRenderPosition(absoluteLightPosition);
            cubeDepthRenderer.setCubeDepthTexture(cubeDepthTextures[0]);
            cubeDepthRenderer.render();

            absoluteLightPosition = Arrays.copyOfRange(lighting.getPositions(), 4, 8);
            cubeDepthRenderer.setRenderPosition(absoluteLightPosition);
            cubeDepthRenderer.setCubeDepthTexture(cubeDepthTextures[1]);
            cubeDepthRenderer.render();

            //Remove bindings to frame buffers
            FrameBuffer.main.bind(0, 0, getSurfaceWidth(), getSurfaceHeight());
            GLES20.glViewport(0, 0, getSurfaceWidth(), getSurfaceHeight());

            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            GLES20.glClearDepthf(1);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            Matrix4 lightTransform = Matrix4.newTranslation(lighting.getPositions()[0], lighting.getPositions()[1], lighting.getPositions()[2]);
            lightTransform.scaleBy(0.2f, 0.2f, 0.2f);

            monkeyHandle.setProperty(RenderProperty.MODEL_MATRIX, lightTransform);

            lightTransform = Matrix4.newTranslation(lighting.getPositions()[4], lighting.getPositions()[5], lighting.getPositions()[6]);
            lightTransform.scaleBy(0.2f, 0.2f, 0.2f);

            monkeyHandle2.setProperty(RenderProperty.MODEL_MATRIX, lightTransform);

            simpleRenderer.setConstantProperty(RenderProperty.PROJECTION_MATRIX, projection.getProjectionMatrix());
            simpleRenderer.setConstantProperty(RenderProperty.VIEW_MATRIX, camera.getViewMatrix());
            simpleRenderer.setConstantProperty(RenderProperty.LIGHTING, lighting);
            //TODO: Should be part of lighting
            simpleRenderer.setConstantProperty(RenderProperty.CUBE_DEPTH_TEXTURES, cubeDepthTextures);
            simpleRenderer.setConstantProperty(RenderProperty.DEPTH_Z_CONST, cubeDepthRenderer.getProjection().getDepthZConst());
            simpleRenderer.setConstantProperty(RenderProperty.DEPTH_Z_FACTOR, cubeDepthRenderer.getProjection().getDepthZFactor());
            simpleRenderer.applyConstantProperties();
            simpleRenderer.render();
        }

        public void onTouchTapEvent(TouchAnalyzer.TouchTapEvent event)
        {
            if (event.numPointers == 3)
            {

            }
        }
    }
}
