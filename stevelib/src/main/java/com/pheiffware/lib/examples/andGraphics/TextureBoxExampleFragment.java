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
import com.pheiffware.lib.graphics.managed.Texture;
import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Attribute;
import com.pheiffware.lib.graphics.techniques.TechniqueProperty;
import com.pheiffware.lib.graphics.techniques.TextureMaterialTechnique;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loads a mesh using the Collada library and displays it on the screen.  Allows the camera to be adjusted using TouchTransform events. Created by Steve on 3/27/2016.
 */

public class TextureBoxExampleFragment extends SimpleGLFragment
{
    public TextureBoxExampleFragment()
    {
        super(new ExampleRenderer(), FilterQuality.MEDIUM);
    }

    private static class ExampleRenderer extends Base3DExampleRenderer
    {
        private TextureMaterialTechnique textureTechnique;
        private IndexBuffer indexBuffer;
        private Matrix4 translationMatrix = Matrix4.newTranslation(-3, 2, -5);
        private Texture texture;
        private int rotation = 0;
        private StaticVertexBuffer vertexBuffer;

        public ExampleRenderer()
        {
            super(90f, 1.0f, 100.0f, 0.01f);
        }

        @Override
        public void onSurfaceCreated(AssetLoader al, GLCache glCache) throws GraphicsException
        {
            super.onSurfaceCreated(al, glCache);
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
                indexBuffer.putIndices(mesh.vertexIndices);
                indexBuffer.transfer();

                vertexBuffer = new StaticVertexBuffer(new Attribute[]{Attribute.POSITION, Attribute.NORMAL, Attribute.TEXCOORD});
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
            vertexBuffer.bind(textureTechnique.getProgram());
            Matrix4 modelMatrix = Matrix4.multiply(translationMatrix, Matrix4.newRotate(rotation, 1, 1, 0), Matrix4.newScale(1f, 2f, 1f));

            textureTechnique.setProperty(TechniqueProperty.PROJECTION_MATRIX, projectionMatrix);
            textureTechnique.setProperty(TechniqueProperty.VIEW_MATRIX, viewMatrix);
            textureTechnique.setProperty(TechniqueProperty.MODEL_MATRIX, modelMatrix);
            textureTechnique.setProperty(TechniqueProperty.AMBIENT_LIGHT_COLOR, new float[]{0.2f, 0.2f, 0.2f, 1.0f});
            textureTechnique.setProperty(TechniqueProperty.LIGHT_POS, new float[]{-3, 3, 0, 1});
            textureTechnique.setProperty(TechniqueProperty.LIGHT_COLOR, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
            textureTechnique.setProperty(TechniqueProperty.SPEC_MAT_COLOR, new float[]{0.2f, 0.2f, 0.2f, 1.0f});
            textureTechnique.setProperty(TechniqueProperty.SHININESS, 3.0f);
            texture.bindToSampler(2);
            textureTechnique.setProperty(TechniqueProperty.MAT_COLOR_TEXTURE, texture);

            textureTechnique.applyProperties();

            indexBuffer.drawAll(GLES20.GL_TRIANGLES);
            rotation++;

        }
    }
}