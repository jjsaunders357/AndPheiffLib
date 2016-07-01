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
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.techniques.ColorMaterialTechnique;
import com.pheiffware.lib.graphics.managed.vertexBuffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.StaticVertexBuffer;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;

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
        private final Lighting lighting = new Lighting(new float[]{-3, 3, 0, 1}, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
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

                colorVertexBuffer = new StaticVertexBuffer(new VertexAttribute[]{VertexAttribute.POSITION, VertexAttribute.NORMAL});
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
            lighting.calcLightPositionsInEyeSpace(viewMatrix);
            colorTechnique.bind();
            colorVertexBuffer.bind(colorTechnique);
            Matrix4 modelMatrix = Matrix4.multiply(translationMatrix, Matrix4.newRotate(rotation, 1, 1, 0), Matrix4.newScale(1f, 2f, 1f));

            colorTechnique.setProperty(RenderProperty.PROJECTION_MATRIX, projectionMatrix);
            colorTechnique.setProperty(RenderProperty.VIEW_MATRIX, viewMatrix);
            colorTechnique.setProperty(RenderProperty.MODEL_MATRIX, modelMatrix);
            colorTechnique.setProperty(RenderProperty.AMBIENT_LIGHT_COLOR, new float[]{0.2f, 0.2f, 0.2f, 1.0f});

            colorTechnique.setProperty(RenderProperty.LIGHTING, lighting);

            colorTechnique.setProperty(RenderProperty.MAT_COLOR, new float[]{0.0f, 0.6f, 0.9f, 1.0f});
            colorTechnique.setProperty(RenderProperty.SPEC_MAT_COLOR, new float[]{0.75f, 0.85f, 1.0f, 1.0f});
            colorTechnique.setProperty(RenderProperty.SHININESS, 30.0f);
            colorTechnique.applyProperties();

            indexBuffer.drawAll(GLES20.GL_TRIANGLES);
            rotation++;
        }
    }
}