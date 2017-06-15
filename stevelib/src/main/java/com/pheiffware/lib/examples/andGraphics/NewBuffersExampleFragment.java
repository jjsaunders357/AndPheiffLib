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
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.ColladaGraphicsLoader;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.light.Lighting;
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
public class NewBuffersExampleFragment extends BaseGameFragment
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
        private Matrix4 multiCubeTranslation = Matrix4.newTranslation(-3, 2, -5);

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
                Collada collada2 = colladaFactory.loadCollada(al, "meshes/test_render.dae");
                ColladaGraphicsLoader.quickLoadTextures(glCache, "images", collada, GLES20.GL_CLAMP_TO_EDGE);

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

            Matrix4 modelRotate = Matrix4.multiply(Matrix4.newRotate(rotation, 1, 1, 0));
            Matrix4 modelMatrix;
            modelMatrix = Matrix4.multiply(multiCubeTranslation, modelRotate);
            rotation++;
        }
    }
}
