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
import com.pheiffware.lib.graphics.techniques.ColorMaterialTechnique;
import com.pheiffware.lib.graphics.techniques.PropConst;
import com.pheiffware.lib.graphics.techniques.ShadConst;
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
        private StaticVertexBuffer vertexBuffer;
        private Matrix4 translationMatrix;

        public ExampleRenderer()
        {
            super(90f, 1.0f, 100.0f, 0.01f);
        }

        @Override
        public void onSurfaceCreated(AssetLoader al, GLCache GLCache) throws GraphicsException
        {
            super.onSurfaceCreated(al, GLCache);
            colorTechnique = new ColorMaterialTechnique(al);
            ColladaFactory colladaFactory = new ColladaFactory(true);
            InputStream inputStream = null;
            try
            {
                inputStream = al.getInputStream("meshes/test_render.dae");
                Collada collada = colladaFactory.loadCollada(inputStream);

                //Lookup object from loaded file by "name" (what user named it in editing tool)
                ColladaObject3D monkey = collada.objects.get("Monkey");

                //From a given object get all meshes which should be rendered with the given material (in this case there is only one mesh which uses the single material defined in the file).
                Mesh mesh = monkey.getMesh(0);

                //Extract the translation aspect of the transform
                DecomposedTransform3D decomposedTransform = monkey.getInitialMatrix().decompose();
                translationMatrix = decomposedTransform.getTranslation();

                indexBuffer = new IndexBuffer(false);
                indexBuffer.allocate(mesh.getNumIndices());
                indexBuffer.putIndices(mesh.vertexIndices);
                indexBuffer.transfer();

                vertexBuffer = new StaticVertexBuffer(colorTechnique.getProgram(),
                        new String[]
                                {ShadConst.VERTEX_POSITION_ATTRIBUTE, ShadConst.VERTEX_NORMAL_ATTRIBUTE});
                vertexBuffer.allocate(mesh.getNumVertices());
                vertexBuffer.putAttributeFloats(ShadConst.VERTEX_POSITION_ATTRIBUTE, mesh.getPositionData(), 0);
                vertexBuffer.putAttributeFloats(ShadConst.VERTEX_NORMAL_ATTRIBUTE, mesh.getNormalData(), 0);

                vertexBuffer.transfer();
            }
            catch (IOException | XMLParseException e)
            {
                throw new GraphicsException(e);
            }
        }


        @Override
        protected void onDrawFrame(Matrix4 projectionMatrix, Matrix4 viewMatrix) throws GraphicsException
        {
            //Default view volume is based on sitting at origin and looking in negative z direction
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            colorTechnique.bind();

            Matrix4 modelMatrix = Matrix4.multiply(translationMatrix, Matrix4.newRotate(rotation, 1, 1, 0), Matrix4.newScale(1f, 2f, 1f));
            Matrix4 viewModelMatrix;
            viewModelMatrix = new Matrix4(viewMatrix);
            viewModelMatrix.multiplyBy(modelMatrix);

            colorTechnique.setProperty(PropConst.PROJECTION_MATRIX_PROPERTY, projectionMatrix);
            colorTechnique.setProperty(PropConst.VIEW_MATRIX_PROPERTY, viewMatrix);
            colorTechnique.setProperty(PropConst.MODEL_MATRIX_PROPERTY, modelMatrix);
            colorTechnique.setProperty(PropConst.AMBIENT_LIGHT_COLOR_PROPERTY, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
            colorTechnique.setProperty(PropConst.LIGHT_COLOR_PROPERTY, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
            colorTechnique.setProperty(PropConst.LIGHT_POS_PROPERTY, new float[]{-3, 3, 0, 1});

            colorTechnique.setProperty(PropConst.AMBIENT_MAT_COLOR_PROPERTY, new float[]{0.2f, 0.2f, 0.2f, 1.0f});
            colorTechnique.setProperty(PropConst.DIFF_MAT_COLOR_PROPERTY, new float[]{0.0f, 0.6f, 0.9f, 1.0f});
            colorTechnique.setProperty(PropConst.SPEC_MAT_COLOR_PROPERTY, new float[]{0.75f, 0.85f, 1.0f, 1.0f});
            colorTechnique.setProperty(PropConst.SHININESS_PROPERTY, 30.0f);
            colorTechnique.applyProperties();
            vertexBuffer.bind();
            indexBuffer.drawAll(GLES20.GL_TRIANGLES);
            rotation++;
        }
    }
}