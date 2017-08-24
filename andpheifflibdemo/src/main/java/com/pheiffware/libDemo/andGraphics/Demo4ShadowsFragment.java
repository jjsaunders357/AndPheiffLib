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
import com.pheiffware.lib.graphics.managed.program.Technique;
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
        private TextureCubeMap cubeDepthTexture;
        private SimpleRenderer simpleRenderer;
        private CubeDepthRenderer cubeRenderer;
        private ObjectHandle monkeyHandle;
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
            glCache.setConfigProperty(GraphicsConfig.ENABLE_SHADOWS, true);


            colorTechnique = glCache.buildTechnique(Std3DTechnique.class, GraphicsConfig.TEXTURED_MATERIAL, false);
            textureTechnique = glCache.buildTechnique(Std3DTechnique.class, GraphicsConfig.TEXTURED_MATERIAL, true);

            cubeDepthTexture = glCache.buildCubeDepthTex(512, 512).build();

            getCamera().setPosition(1, 0, 2);
            lighting = new Lighting(new float[]{0.2f, 0.2f, 0.2f, 1.0f}, new float[]{1.0f, 1.0f, 3, 1}, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
            lighting.setMaximumDistance(0, maximumLightDistance);
            simpleRenderer = new SimpleRenderer();
            cubeRenderer = new CubeDepthRenderer(glCache, cubeDepthTexture);

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
                loader.loadCollada("meshes/shadows2.dae");
                monkeyTransform = Matrix4.newTranslation(0.5f, 1f, -1f);
                monkeyTransform.scaleBy(0.1f, 0.1f, 0.1f);
                manager.packAndTransfer();
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
            cubeRenderer.setRenderPosition(absoluteLightPosition);
            cubeRenderer.add(manager.getGroupObjects("main"));
            //cubeRenderer.add(monkeyHandle);
            cubeRenderer.render();

            colorTechnique.setProperty(RenderProperty.PROJECTION_LINEAR_DEPTH, projection.getLinearDepth());
            colorTechnique.setProperty(RenderProperty.VIEW_MATRIX, camera.getViewMatrix());
            colorTechnique.setProperty(RenderProperty.LIGHTING, lighting);
            colorTechnique.setProperty(RenderProperty.CUBE_DEPTH_TEXTURE, cubeDepthTexture);
            colorTechnique.setProperty(RenderProperty.SHADOW_PROJECTION_MAX_DEPTH, cubeRenderer.getProjection().getLinearDepth().maxDepth);
            //colorTechnique.setProperty(RenderProperty.DEPTH_Z_CONST, cubeRenderer.getProjection().getDepthZConst());
            //colorTechnique.setProperty(RenderProperty.DEPTH_Z_FACTOR, cubeRenderer.getProjection().getDepthZFactor());
            colorTechnique.applyConstantProperties();


            //Remove bindings to frame buffers
            FrameBuffer.main.bind(0, 0, getSurfaceWidth(), getSurfaceHeight());
            GLES20.glViewport(0, 0, getSurfaceWidth(), getSurfaceHeight());

            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            GLES20.glClearDepthf(1);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            Matrix4 lightTransform = Matrix4.newTranslation(lighting.getPositions()[0], lighting.getPositions()[1], lighting.getPositions()[2]);
            lightTransform.scaleBy(0.1f, 0.1f, 0.1f);

            monkeyHandle.setProperty(RenderProperty.MODEL_MATRIX, lightTransform);
            simpleRenderer.add(manager.getGroupObjects("main"));
            simpleRenderer.add(monkeyHandle);

            GLES20.glCullFace(GLES20.GL_BACK);
            simpleRenderer.render();
        }

        public void onTouchTapEvent(TouchAnalyzer.TouchTapEvent event)
        {
            if (event.numPointers == 3)
            {
                try
                {
                    getGlCache().setConfigProperty(GraphicsConfig.ENABLE_SHADOWS, !getGlCache().getConfigProperty(GraphicsConfig.ENABLE_SHADOWS, Boolean.class));
                }
                catch (GraphicsException e)
                {
                    throw new RuntimeException("Failed to change graphics configuration", e);
                }

            }
        }
    }
}
