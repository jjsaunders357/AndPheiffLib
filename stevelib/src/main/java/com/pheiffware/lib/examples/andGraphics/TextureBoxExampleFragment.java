package com.pheiffware.lib.examples.andGraphics;

import android.content.res.AssetManager;
import android.opengl.GLES20;

import com.pheiffware.lib.and.gui.graphics.openGL.SimpleGLFragment;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.ManGL;
import com.pheiffware.lib.graphics.managed.Program;
import com.pheiffware.lib.graphics.managed.Texture;
import com.pheiffware.lib.graphics.managed.buffer.IndexBuffer;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.mesh.Object3D;
import com.pheiffware.lib.utils.dom.XMLParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
                Material material1 = collada.materialsByName.get("Brown_Brick");
                Material material2 = collada.materialsByName.get("Grey_Brick");
                Material material3 = collada.materialsByName.get("Stripes");
                System.out.println(material1.imageFileName);
                texture = manGL.createImageTexture(am, "images/" + material1.imageFileName, true, GLES20.GL_REPEAT, GLES20.GL_REPEAT);

                //Lookup object from loaded file by "name" (what user named it in editing tool)
                Object3D cube1 = collada.objects.get("cube1");
                Object3D cube2 = collada.objects.get("cube2");
                Object3D cube3 = collada.objects.get("cube3");


                //From a given object get all meshes which should be rendered with the given material (in this case there is only one mesh which uses the single material defined in the file).
                List<Mesh> meshList = cube3.getMeshGroup().getMeshes(material1);
                Mesh mesh = meshList.get(0);
                mesh = mesh.newTransformedMesh(Matrix4.newScale(0.01f, 0.01f, 0.01f));
                indexBuffer.allocate(mesh.getNumVertexIndices());
                indexBuffer.putIndices(mesh.vertexIndices);
                indexBuffer.transfer();

                StaticVertexBuffer vertexBuffer = new StaticVertexBuffer(program,
                        new String[]
                                {"vertexPosition", "vertexNormal", "vertexTexCoord"});
                vertexBuffer.allocate(mesh.getNumUniqueVertices());
                vertexBuffer.putAttributeFloats("vertexPosition", mesh.getPositionData(), 0);
                vertexBuffer.putAttributeFloats("vertexNormal", mesh.getNormalData(), 0);
                vertexBuffer.putAttributeFloats("vertexTexCoord", mesh.getTexCoordData(), 0);

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
            program.setUniformMatrix4("eyeProjectionMatrix", projectionMatrix.m, false);
            program.setUniformMatrix4("eyeTransformMatrix", viewModelMatrix.m, false);
            program.setUniformMatrix3("eyeNormalMatrix", normalMatrix.m, false);
            program.setUniformVec4("ambientLightColor", new float[]{0.2f, 0.2f, 0.2f, 1.0f});
            program.setUniformVec4("diffuseLightColor", new float[]{1.0f, 1.0f, 1.0f, 1.0f});
            program.setUniformVec4("specMaterialColor", new float[]{0.5f, 0.5f, 0.5f, 1.0f});
            program.setUniformFloat("shininess", 30.0f);
            program.setUniformVec3("lightPositionEyeSpace", new float[]{-3, 3, 0});
            program.setUniformTexture2D("diffuseMaterialTexture", texture, 0);
        }
    }
}