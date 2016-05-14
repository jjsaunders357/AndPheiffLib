package com.pheiffware.lib.examples.andGraphics;

import android.opengl.GLES20;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLFragment;
import com.pheiffware.lib.geometry.DecomposedTransform3D;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Attribute;
import com.pheiffware.lib.graphics.techniques.ColorMaterialTechnique;
import com.pheiffware.lib.graphics.techniques.TechniqueProperty;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loads a mesh using the Collada library and displays it on the screen.  Allows the camera to be adjusted using TouchTransform events. Created by Steve on 3/27/2016.
 */

public class MeshExampleFragment extends SimpleGLFragment
{
    public MeshExampleFragment()
    {
        super(new ExampleRenderer(), FilterQuality.MEDIUM);
    }

    private static class ExampleRenderer extends Base3DExampleRenderer
    {
        private float rotation = 0;

        private ColorMaterialTechnique colorTechnique;
        private IndexBuffer indexBuffer;
        private Matrix4 translationMatrix;
        private StaticVertexBuffer colorVertexBuffer;

        public ExampleRenderer()
        {
            super(90f, 1.0f, 100.0f, 0.01f);
        }

        @Override
        public void onSurfaceCreated(AssetLoader al, GLCache glCache) throws GraphicsException
        {
            super.onSurfaceCreated(al, glCache);
            colorTechnique = new ColorMaterialTechnique(al);
            ColladaFactory colladaFactory = new ColladaFactory(true);
            InputStream inputStream = null;
            try
            {
                Collada collada = colladaFactory.loadCollada(al, "meshes/test_render.dae");

                //Lookup object from loaded file by "name" (what user named it in editing tool)
                ColladaObject3D monkey = collada.objects.get("Monkey");

                //From a given object get all meshes which should be rendered with the given material (in this case there is only one mesh which uses the single material defined in the file).
                Mesh mesh = monkey.getMesh(0);

                //Extract the translation aspect of the transform
                DecomposedTransform3D decomposedTransform = monkey.getInitialMatrix().decompose();
                translationMatrix = decomposedTransform.getTranslation();

                indexBuffer = new IndexBuffer(false);
                indexBuffer.allocate(mesh.getNumIndices());
                indexBuffer.putIndices(mesh.getVertexIndices());
                indexBuffer.transfer();

                colorVertexBuffer = new StaticVertexBuffer(new Attribute[]{Attribute.POSITION, Attribute.NORMAL});
                colorVertexBuffer.allocate(mesh.getNumVertices());
                colorVertexBuffer.putVertexAttributes(mesh, 0);
                colorVertexBuffer.transfer();
            }
            catch (IOException | XMLParseException e)
            {
                throw new GraphicsException(e);
            }
        }


        @Override
        protected void onDrawFrame(Matrix4 projectionMatrix, Matrix4 viewMatrix) throws GraphicsException
        {
            colorTechnique.bind();
            colorVertexBuffer.bind(colorTechnique.getProgram());
            Matrix4 modelMatrix = Matrix4.multiply(translationMatrix, Matrix4.newRotate(rotation, 1, 1, 0), Matrix4.newScale(1f, 2f, 1f));

            colorTechnique.setProperty(TechniqueProperty.PROJECTION_MATRIX, projectionMatrix);
            colorTechnique.setProperty(TechniqueProperty.VIEW_MATRIX, viewMatrix);
            colorTechnique.setProperty(TechniqueProperty.MODEL_MATRIX, modelMatrix);
            colorTechnique.setProperty(TechniqueProperty.AMBIENT_LIGHT_COLOR, new float[]{0.2f, 0.2f, 0.2f, 1.0f});
            colorTechnique.setProperty(TechniqueProperty.LIGHT_COLOR, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
            colorTechnique.setProperty(TechniqueProperty.LIGHT_POS, new float[]{-3, 3, 0, 1});

            colorTechnique.setProperty(TechniqueProperty.MAT_COLOR, new float[]{0.0f, 0.6f, 0.9f, 1.0f});
            colorTechnique.setProperty(TechniqueProperty.SPEC_MAT_COLOR, new float[]{0.75f, 0.85f, 1.0f, 1.0f});
            colorTechnique.setProperty(TechniqueProperty.SHININESS, 30.0f);
            colorTechnique.applyProperties();

            indexBuffer.drawAll(GLES20.GL_TRIANGLES);
            rotation++;
        }
    }
}