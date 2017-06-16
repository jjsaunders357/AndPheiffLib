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
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.techniques.TextureMaterialTechnique;
import com.pheiffware.lib.graphics.managed.texture.Texture;
import com.pheiffware.lib.graphics.managed.vertexBuffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.StaticVertexBuffer;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loads a mesh using the Collada library and displays it on the screen.  Allows the camera to be adjusted using TouchTransform events. Created by Steve on 3/27/2016.
 */

public class TextureBoxExampleFragment extends BaseGameFragment
{
    @Override
    public TouchTransformGameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new TouchTransformGameView(getContext(), new Renderer(), FilterQuality.MEDIUM, false, true);
    }

    private static class Renderer extends Example3DRenderer
    {
        private final Lighting lighting = new Lighting(new float[]{-3, 3, 0, 1}, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        private TextureMaterialTechnique textureTechnique;
        private IndexBuffer indexBuffer;
        private Matrix4 translationMatrix = Matrix4.newTranslation(-3, 2, -5);
        private Texture texture;
        private int rotation = 0;
        private StaticVertexBuffer vertexBuffer;

        public Renderer()
        {
            super(90f, 1.0f, 100.0f, 0.01f);
        }

        @Override
        public void onSurfaceCreated(AssetLoader al, GLCache glCache, SurfaceMetrics surfaceMetrics) throws GraphicsException
        {
            super.onSurfaceCreated(al, glCache, surfaceMetrics);
            textureTechnique = new TextureMaterialTechnique(al);
            ColladaFactory colladaFactory = new ColladaFactory(true);
            InputStream inputStream = null;
            try
            {
                Collada collada = colladaFactory.loadCollada(al, "meshes/cubes.dae");

                //Lookup material from loaded file by "name" (what user named it in editing tool)
                ColladaMaterial brownBrickMaterial = collada.materialsByName.get("brown_brick");
                texture = glCache.createImageTexture("images/" + brownBrickMaterial.imageFileName, true, GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);

                //Lookup object from loaded file by "name" (what user named it in editing tool)
                ColladaObject3D brickCube = collada.objects.get("brown");


                //From a given object get all meshes which should be rendered with the given material (in this case there is only one mesh which uses the single material defined in the file).
                Mesh mesh = brickCube.getMesh(0);
                indexBuffer = new IndexBuffer(false);
                indexBuffer.allocate(mesh.getNumIndices());
                indexBuffer.putIndices(mesh.getVertexIndices());
                indexBuffer.transfer();

                vertexBuffer = new StaticVertexBuffer(new VertexAttribute[]{VertexAttribute.POSITION4, VertexAttribute.NORMAL, VertexAttribute.TEXCOORD});
                vertexBuffer.allocate(mesh.getNumVertices());
                vertexBuffer.putVertexAttributes(mesh, 0);
                vertexBuffer.transfer();

            }
            catch (IOException | XMLParseException e)
            {
                throw new GraphicsException(e);
            }
        }

        @Override
        public void onDrawFrame(Matrix4 projectionMatrix, Matrix4 viewMatrix) throws GraphicsException
        {
            textureTechnique.bind();
            vertexBuffer.bind(textureTechnique);
            Matrix4 modelMatrix = Matrix4.multiply(translationMatrix, Matrix4.newRotate(rotation, 1, 1, 0), Matrix4.newScale(1f, 2f, 1f));

            textureTechnique.setProperty(RenderProperty.PROJECTION_MATRIX, projectionMatrix);
            textureTechnique.setProperty(RenderProperty.VIEW_MATRIX, viewMatrix);
            textureTechnique.setProperty(RenderProperty.MODEL_MATRIX, modelMatrix);
            textureTechnique.setProperty(RenderProperty.AMBIENT_LIGHT_COLOR, new float[]{0.2f, 0.2f, 0.2f, 1.0f});
            textureTechnique.setProperty(RenderProperty.LIGHTING, lighting);
            textureTechnique.setProperty(RenderProperty.SPEC_MAT_COLOR, new float[]{0.2f, 0.2f, 0.2f, 1.0f});
            textureTechnique.setProperty(RenderProperty.SHININESS, 3.0f);
            textureTechnique.setProperty(RenderProperty.MAT_COLOR_TEXTURE, texture);

            textureTechnique.applyProperties();

            indexBuffer.drawAll(GLES20.GL_TRIANGLES);
            rotation++;

        }
    }
}