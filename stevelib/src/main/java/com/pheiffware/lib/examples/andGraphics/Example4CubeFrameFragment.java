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
import com.pheiffware.lib.graphics.Camera;
import com.pheiffware.lib.graphics.Color4F;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.engine.newEngine.ColladaLoader;
import com.pheiffware.lib.graphics.managed.engine.newEngine.ObjectHandle;
import com.pheiffware.lib.graphics.managed.engine.newEngine.ObjectManager;
import com.pheiffware.lib.graphics.managed.engine.newEngine.renderers.SimpleRenderer;
import com.pheiffware.lib.graphics.managed.engine.newEngine.renderers.TechniqueRenderer;
import com.pheiffware.lib.graphics.managed.frameBuffer.FrameBuffer;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.techniques.ColorMaterialTechnique;
import com.pheiffware.lib.graphics.managed.techniques.ColorTexture2DTechnique;
import com.pheiffware.lib.graphics.managed.techniques.DepthAsColorTechnique;
import com.pheiffware.lib.graphics.managed.techniques.DepthTechnique;
import com.pheiffware.lib.graphics.managed.techniques.Texture2DTechnique;
import com.pheiffware.lib.graphics.managed.techniques.TextureMaterialTechnique;
import com.pheiffware.lib.graphics.managed.texture.Texture;
import com.pheiffware.lib.graphics.utils.MeshGenUtils;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;
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
        protected void loadTexture(String imageFileName) throws GraphicsException
        {
            glCache.createImageTexture(imageFileName, imageFileName, true, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
        }
    }

    private static class Renderer extends Example3DRenderer
    {
        private Lighting lighting;
        private ObjectManager manager;
        private ObjectHandle monkeyHandle;
        private float rotation = 0;
        private SimpleRenderer simpleRenderer;
        private ColorMaterialTechnique colorTechnique;
        private TextureMaterialTechnique textureTechnique;
        private FrameBuffer frameBuffer;
        //private TextureCubeMap cubeTexture;
        private Texture depthTexture;
        private Texture depthColorTexture;
        private DepthTechnique depthTechnique;
        private Texture2DTechnique texture2DTechnique;
        private TechniqueRenderer techniqueRenderer;
        private DepthAsColorTechnique depthAsColorTechnique;
        private Matrix4 ortho2DMatrix;
        private Texture faceTexture;
        private ColorTexture2DTechnique colorTexture2DTechnique;

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

            faceTexture = glCache.createImageTexture("images/face.png", true, FilterQuality.MEDIUM, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
            frameBuffer = new FrameBuffer();
            colorTechnique = new ColorMaterialTechnique(al);

            textureTechnique = new TextureMaterialTechnique(al);
            texture2DTechnique = new Texture2DTechnique(al);
            colorTexture2DTechnique = new ColorTexture2DTechnique(al);
            depthTechnique = new DepthTechnique(al);
            depthAsColorTechnique = new DepthAsColorTechnique(al);
            depthTexture = glCache.createDepthRenderTexture("depth", 512, 512, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
            depthColorTexture = glCache.createColorRenderTexture("depthColor", 512, 512, false, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
            //cubeTexture = glCache.createCubeDepthRenderTexture("shadow", 512, 512);
            lighting = new Lighting(new float[]{-3, 3, 0, 1}, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
            simpleRenderer = new SimpleRenderer();
            techniqueRenderer = new TechniqueRenderer(depthAsColorTechnique);
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
                    colorTechnique,
                    textureTechnique);
            try
            {
                loader.loadCollada("meshes/shadows.dae");
                manager.startObject("display");
                manager.addStaticMesh(
                        MeshGenUtils.genSingleQuadMeshTexOnly(-0.8f, -0.6f, 0.2f, VertexAttribute.POSITION4),
                        texture2DTechnique,
                        new RenderPropertyValue[]{
                                new RenderPropertyValue(RenderProperty.MAT_COLOR_TEXTURE, depthColorTexture)
                        });
                manager.endObject();
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
            float[] lightPosition = Arrays.copyOfRange(lighting.getPositions(), 0, 4);

            //Extract eye position from current view
            float[] position = new float[]{-viewMatrix.m[12], -viewMatrix.m[13], -viewMatrix.m[14]};
            //GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z

            renderViewToTexture(position);

            lighting.calcOnLightPositionsInEyeSpace(viewMatrix);
            Matrix4 monkeyTranslation = Matrix4.newTranslation(0, 0, -4);
            //Matrix4 modelMatrix = Matrix4.multiply(monkeyTranslation, Matrix4.newRotate(rotation, 1, 1, 0), Matrix4.newScale(1f, 1f, 1f));
            //Increase rotation for next frame
            rotation++;

            //monkeyHandle.setProperty(RenderProperty.MODEL_MATRIX, modelMatrix);

            colorTechnique.setProperty(RenderProperty.PROJECTION_MATRIX, projectionMatrix);
            colorTechnique.setProperty(RenderProperty.VIEW_MATRIX, viewMatrix);
            colorTechnique.setProperty(RenderProperty.AMBIENT_LIGHT_COLOR, new float[]{0.2f, 0.2f, 0.2f, 1.0f});
            colorTechnique.setProperty(RenderProperty.LIGHTING, lighting);
            texture2DTechnique.setProperty(RenderProperty.PROJECTION_MATRIX, ortho2DMatrix);
            texture2DTechnique.setProperty(RenderProperty.VIEW_MATRIX, Matrix4.newIdentity());
            //Remove bindings to frame buffers
            PheiffGLUtils.bindFrameBuffer(0, -1, -1);

            //TODO: Make frame buffers remember and put this back glGet(GL_VIEWPORT)
            setViewPortToFullWindow();

//            simpleRenderer.add(manager.getGroupObjects("main"));
            simpleRenderer.add(manager.getObjects());
            simpleRenderer.render();


//            simpleRenderer.add(manager.getGroupObjects("display"));
//            simpleRenderer.render();
        }

        @Override
        public void onSurfaceResize(int width, int height)
        {
            super.onSurfaceResize(width, height);
            ortho2DMatrix = Matrix4.newOrtho2D(width / (float) height);
        }

        private void renderViewToTexture(float[] renderPosition)
        {
            //A camera at the render position
            Camera lightCamera = new Camera(90, 1, 0.1f, 100, false);
            lightCamera.setPosition(renderPosition[0], renderPosition[1], renderPosition[2]);

            //All calculations are done in absolute space without any transformations.
            //This prevents recalculation of lighting when objects are static.


            //camera.rotate(); - no need to rotated for this plane

            //Set to render depth to cube face
            //cubeTexture.setAttachFace(cubeFace);

            frameBuffer.bind();
            frameBuffer.attachColor(0, depthColorTexture);
            frameBuffer.attachDepth(depthTexture);
            GLES20.glViewport(0, 0, 512, 512);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            depthAsColorTechnique.setProperty(RenderProperty.PROJECTION_MATRIX, lightCamera.getProjectionMatrix());
            depthAsColorTechnique.setProperty(RenderProperty.VIEW_MATRIX, lightCamera.getViewMatrix());

            techniqueRenderer.add(manager.getGroupObjects("main"));
            techniqueRenderer.render();
        }
    }
}
