package com.pheiffware.lib.examples.andGraphics;

import android.content.res.AssetManager;
import android.opengl.GLES20;

import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLFragment;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.geometry.collada.ColladaMaterial;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.ShadConst;
import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Texture;
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

public class TextureBoxExampleFragment extends SimpleGLFragment
{
    public TextureBoxExampleFragment()
    {
        super(new ExampleRenderer(), FilterQuality.MEDIUM);
    }

    private static class ExampleRenderer extends ExampleRotatingRenderer
    {
        private Texture texture;

        @Override
        protected Program loadProgram(AssetManager am, ManGL manGL) throws GraphicsException
        {
            return manGL.createProgram(am, "testProgram3D", "shaders/vert_mntl.glsl", "shaders/frag_mntl.glsl");
        }

        @Override
        protected StaticVertexBuffer loadBuffers(AssetManager am, ManGL manGL, IndexBuffer indexBuffer, Program program) throws GraphicsException
        {
            ColladaFactory colladaFactory = new ColladaFactory(true);
            InputStream inputStream = null;
            try
            {
                inputStream = am.open("meshes/cubes.dae");
                Collada collada = colladaFactory.loadCollada(inputStream);

                //Lookup material from loaded file by "name" (what user named it in editing tool)
                ColladaMaterial colladaMaterial1 = collada.materialsByName.get("Brown_Brick");
                ColladaMaterial colladaMaterial2 = collada.materialsByName.get("Grey_Brick");
                ColladaMaterial colladaMaterial3 = collada.materialsByName.get("Stripes");
                texture = manGL.createImageTexture(am, "images/" + colladaMaterial1.imageFileName, true, GLES20.GL_REPEAT, GLES20.GL_REPEAT);

                //Lookup object from loaded file by "name" (what user named it in editing tool)
                ColladaObject3D cube1 = collada.objects.get("cube1");
                ColladaObject3D cube2 = collada.objects.get("cube2");
                ColladaObject3D cube3 = collada.objects.get("cube3");


                //From a given object get all meshes which should be rendered with the given material (in this case there is only one mesh which uses the single material defined in the file).
                Mesh mesh = cube3.getMesh(0);
                mesh = mesh.newTransformedMesh(Matrix4.newScale(0.01f, 0.01f, 0.01f));
                indexBuffer.allocate(mesh.getNumIndices());
                indexBuffer.putIndices(mesh.vertexIndices);
                indexBuffer.transfer();

                StaticVertexBuffer vertexBuffer = new StaticVertexBuffer(program,
                        new String[]
                                {ShadConst.VERTEX_POSITION_ATTRIBUTE, ShadConst.VERTEX_NORMAL_ATTRIBUTE, ShadConst.VERTEX_TEXCOORD_ATTRIBUTE});
                vertexBuffer.allocate(mesh.getNumVertices());
                vertexBuffer.putAttributeFloats(ShadConst.VERTEX_POSITION_ATTRIBUTE, mesh.getPositionData(), 0);
                vertexBuffer.putAttributeFloats(ShadConst.VERTEX_NORMAL_ATTRIBUTE, mesh.getNormalData(), 0);
                vertexBuffer.putAttributeFloats(ShadConst.VERTEX_TEXCOORD_ATTRIBUTE, mesh.getTexCoordData(), 0);

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
            return Matrix4.newTranslation(-3, 2, -5);
        }

        @Override
        protected void setUniforms(Program program, Matrix4 projectionMatrix, Matrix4 viewModelMatrix, Matrix3 normalMatrix)
        {
            program.setUniformValue(ShadConst.EYE_PROJECTION_MATRIX_UNIFORM, projectionMatrix.m);
            program.setUniformValue(ShadConst.EYE_TRANSFORM_MATRIX_UNIFORM, viewModelMatrix.m);
            program.setUniformValue(ShadConst.EYE_NORMAL_MATRIX_UNIFORM, normalMatrix.m);
            program.setUniformValue(ShadConst.AMBIENT_LIGHT_COLOR_UNIFORM, new float[]{0.2f, 0.2f, 0.2f, 1.0f});
            program.setUniformValue(ShadConst.DIFF_LIGHT_COLOR_UNIFORM, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
            program.setUniformValue(ShadConst.SPEC_LIGHTMAT_COLOR_UNIFORM, new float[]{0.2f, 0.2f, 0.2f, 1.0f});
            program.setUniformValue(ShadConst.SHININESS_UNIFORM, 3.0f);
            program.setUniformValue(ShadConst.LIGHT_POS_EYE_UNIFORM, new float[]{-3, 3, 0});
            program.setUniformValue(ShadConst.DIFF_MATERIAL_TEXTURE_UNIFORM, texture);
        }
    }
}