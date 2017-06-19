package com.pheiffware.lib.examples.andGraphics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.gui.graphics.openGL.BaseGameFragment;
import com.pheiffware.lib.and.gui.graphics.openGL.SurfaceMetrics;
import com.pheiffware.lib.and.gui.graphics.openGL.TouchTransformGameView;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.engine.newEngine.ObjectHandle;
import com.pheiffware.lib.graphics.managed.engine.newEngine.ObjectManager;
import com.pheiffware.lib.graphics.managed.engine.newEngine.renderers.SimpleRenderer;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.techniques.ColorMaterialTechnique;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;

/**
 * Created by Steve on 6/19/2017.
 */

public class Example3ManagedRenderingFragment extends BaseGameFragment
{
    @Override
    public TouchTransformGameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new TouchTransformGameView(getContext(), new Example3ManagedRenderingFragment.Renderer(), FilterQuality.MEDIUM, false, true);
    }


    private static class Renderer extends Example3DRenderer
    {
        private Lighting lighting;
        private ColorMaterialTechnique colorTechnique;
        private ObjectManager manager;
        private ObjectHandle monkeyHandle;
        private float rotation = 0;
        private SimpleRenderer simpleRenderer;

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
            lighting = new Lighting(new float[]{-3, 3, 0, 1}, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
            colorTechnique = new ColorMaterialTechnique(al);
            ColladaFactory colladaFactory = new ColladaFactory(true);
            try
            {
                Collada collada = colladaFactory.loadCollada(al, "meshes/test_render.dae");

                //Lookup object from loaded file by "name" (what user named it in editing tool)
                ColladaObject3D monkey = collada.objects.get("Monkey");

                //From a given object get all meshes which should be rendered with the given material (in this case there is only one mesh which uses the single material defined in the file).
                Mesh monkeyMesh = monkey.getMesh(0);

                manager = new ObjectManager();
                monkeyHandle = manager.startObject();
                manager.addStaticMesh(monkeyMesh, colorTechnique, new RenderPropertyValue[]
                        {
                                new RenderPropertyValue(RenderProperty.MAT_COLOR, new float[]{0.0f, 0.6f, 0.9f, 1.0f}),
                                new RenderPropertyValue(RenderProperty.SPEC_MAT_COLOR, new float[]{0.75f, 0.85f, 1.0f, 1.0f}),
                                new RenderPropertyValue(RenderProperty.SHININESS, 30.0f)
                        });
                manager.endObject();
                manager.packAndTransfer();
                simpleRenderer = new SimpleRenderer();
            }
            catch (IOException | XMLParseException e)
            {
                throw new GraphicsException(e);
            }
        }

        @Override
        protected void onDrawFrame(Matrix4 projectionMatrix, Matrix4 viewMatrix) throws GraphicsException
        {
            lighting.calcOnLightPositionsInEyeSpace(viewMatrix);

            Matrix4 monkeyTranslation = Matrix4.newTranslation(0, 0, -4);
            Matrix4 modelMatrix = Matrix4.multiply(monkeyTranslation, Matrix4.newRotate(rotation, 1, 1, 0), Matrix4.newScale(1f, 1f, 1f));
            //Increase rotation for next frame
            rotation++;

            monkeyHandle.setProperty(RenderProperty.MODEL_MATRIX, modelMatrix);

            colorTechnique.setProperty(RenderProperty.PROJECTION_MATRIX, projectionMatrix);
            colorTechnique.setProperty(RenderProperty.VIEW_MATRIX, viewMatrix);
            colorTechnique.setProperty(RenderProperty.AMBIENT_LIGHT_COLOR, new float[]{0.2f, 0.2f, 0.2f, 1.0f});
            colorTechnique.setProperty(RenderProperty.LIGHTING, lighting);

            simpleRenderer.add(monkeyHandle);
            simpleRenderer.render();
        }
    }
}
