package com.pheiffware.lib.examples.andGraphics;

import android.content.res.AssetManager;

import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLFragment;
import com.pheiffware.lib.geometry.DecomposedTransform3D;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.ShadConst;
import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.program.Program;
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

    private static class ExampleRenderer extends ExampleRotatingRenderer
    {
        private Matrix4 translationMatrix;

        @Override
        protected Program loadProgram(AssetManager am, ManGL manGL) throws GraphicsException
        {
            return manGL.createProgram(am, "testProgram3D", "shaders/vert_mncl.glsl", "shaders/frag_mncl.glsl");
        }

        @Override
        protected StaticVertexBuffer loadBuffers(AssetManager am, ManGL manGL, IndexBuffer indexBuffer, Program program) throws GraphicsException
        {
            ColladaFactory colladaFactory = new ColladaFactory(true);
            InputStream inputStream = null;
            try
            {
                inputStream = am.open("meshes/test_render.dae");
                Collada collada = colladaFactory.loadCollada(inputStream);

                //Lookup object from loaded file by "name" (what user named it in editing tool)
                ColladaObject3D monkey = collada.objects.get("Monkey");

                //From a given object get all meshes which should be rendered with the given material (in this case there is only one mesh which uses the single material defined in the file).
                Mesh mesh = monkey.getMesh(0);

                //Extract the translation aspect of the transform
                DecomposedTransform3D decomposedTransform = monkey.getInitialMatrix().decompose();
                translationMatrix = decomposedTransform.getTranslation();

                indexBuffer.allocate(mesh.getNumIndices());
                indexBuffer.putIndices(mesh.vertexIndices);
                indexBuffer.transfer();

                StaticVertexBuffer vertexBuffer = new StaticVertexBuffer(program,
                        new String[]
                                {ShadConst.VERTEX_POSITION_ATTRIBUTE, ShadConst.VERTEX_NORMAL_ATTRIBUTE});
                vertexBuffer.allocate(mesh.getNumVertices());
                vertexBuffer.putAttributeFloats(ShadConst.VERTEX_POSITION_ATTRIBUTE, mesh.getPositionData(), 0);
                vertexBuffer.putAttributeFloats(ShadConst.VERTEX_NORMAL_ATTRIBUTE, mesh.getNormalData(), 0);

                vertexBuffer.transfer();
                return vertexBuffer;
            }
            catch (IOException e)
            {
                throw new GraphicsException(e);
            }
            catch (XMLParseException e)
            {
                throw new GraphicsException(e);
            }
        }

        @Override
        protected Matrix4 getTranslationMatrix()
        {
            return translationMatrix;
        }

        @Override
        protected void setUniforms(Program program, Matrix4 projectionMatrix, Matrix4 viewModelMatrix, Matrix3 normalMatrix)
        {
            program.setUniformValue(ShadConst.EYE_PROJECTION_MATRIX_UNIFORM, projectionMatrix.m);
            program.setUniformValue(ShadConst.EYE_TRANSFORM_MATRIX_UNIFORM, viewModelMatrix.m);
            program.setUniformValue(ShadConst.EYE_NORMAL_MATRIX_UNIFORM, normalMatrix.m);
            program.setUniformValue(ShadConst.AMBIENT_LIGHTMAT_COLOR_UNIFORM, new float[]{0.2f, 0.2f, 0.2f, 1.0f});
            program.setUniformValue(ShadConst.DIFF_LIGHTMAT_COLOR_UNIFORM, new float[]{0.0f, 0.6f, 0.9f, 1.0f});
            program.setUniformValue(ShadConst.SPEC_LIGHTMAT_COLOR_UNIFORM, new float[]{0.75f, 0.85f, 1.0f, 1.0f});
            program.setUniformValue(ShadConst.SHININESS_UNIFORM, 30.0f);
            program.setUniformValue(ShadConst.LIGHT_POS_EYE_UNIFORM, new float[]{-3, 3, 0});
        }
    }
}