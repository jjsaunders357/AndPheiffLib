package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.graphics.Color4F;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


/**
 * Created by Steve on 2/20/2016.
 */
public class TestCollada
{
    @Test
    public void doesntCrash() throws XMLParseException, IOException, ParserConfigurationException, SAXException
    {
        FileInputStream input = new FileInputStream("src/test/assets/meshes/weird_blender_example.dae");
        ColladaFactory colladaFactory = new ColladaFactory(true);
        colladaFactory.loadCollada(input);
        input = new FileInputStream("src/test/assets/meshes/weird_sketchup_example.dae");
        colladaFactory = new ColladaFactory(true);
        colladaFactory.loadCollada(input);
    }

    @Test
    public void testCompleteLoadBlender() throws XMLParseException, IOException, ParserConfigurationException, SAXException
    {
        FileInputStream input = new FileInputStream("src/test/assets/meshes/test_blender.dae");
        ColladaFactory colladaFactory = new ColladaFactory(true);
        Collada collada = colladaFactory.loadCollada(input);

        //Check materials
        Map<String, ColladaMaterial> materials = collada.materialsByName;
        ColladaMaterial mat1 = materials.get("mat_text1_name");
        ColladaMaterial mat2 = materials.get("mat_text2_name");
        ColladaMaterial matp = materials.get("mat_purple_name");
        ColladaMaterial defaultMat = materials.get("");
        assert mat1 != null;
        assert mat2 != null;
        assert matp != null;
        assert defaultMat != null;

        assertEquals(ColladaFactory.DEFAULT_AMBIENT, mat1.ambientColor);
        assertEquals(ColladaFactory.DEFAULT_DIFFUSE, mat1.diffuseColor);
        assertEquals(ColladaFactory.DEFAULT_SPECULAR, mat1.specularColor);
        assertEquals(ColladaFactory.DEFAULT_SHININESS, mat1.shininess, 0f);
        assertEquals("image1.png", mat1.imageFileName);

        assertEquals(new Color4F(0, 0, 0, 1), mat2.ambientColor);
        assertEquals(ColladaFactory.DEFAULT_DIFFUSE, mat2.diffuseColor);
        assertEquals(new Color4F(0.5f, 0.5f, 0.5f, 1f), mat2.specularColor);
        assertEquals(0.2f, mat2.shininess, 0f);
        assertEquals("image2.png", mat2.imageFileName);

        assertEquals(new Color4F(0, 0, 0, 1), matp.ambientColor);
        assertEquals(new Color4F(0.5f, 0f, 0.5f, 1f), matp.diffuseColor);
        assertEquals(new Color4F(0.5f, 0.5f, 0.5f, 1f), matp.specularColor);
        assertEquals(0.2f, matp.shininess, 0f);
        assert matp.imageFileName == null;

        assertEquals(ColladaFactory.DEFAULT_AMBIENT, defaultMat.ambientColor);
        assertEquals(ColladaFactory.DEFAULT_DIFFUSE, defaultMat.diffuseColor);
        assertEquals(ColladaFactory.DEFAULT_SPECULAR, defaultMat.specularColor);
        assertEquals(ColladaFactory.DEFAULT_SHININESS, defaultMat.shininess, 0f);
        assert defaultMat.imageFileName == null;

        //Check geometry
        Map<String, ColladaGeometry> geometries = colladaFactory.getGeometries();
        ColladaGeometry geo1 = geometries.get("geo1_id");
        assertEquals("mat_text1_id", geo1.materialIDs.get(0));
        assertEquals("mat_text2_id", geo1.materialIDs.get(1));

        Mesh mesh1 = geo1.meshes.get(0);
        assertArrayEquals(new short[]{0, 1, 1, 2, 3, 4, 0, 4, 2}, mesh1.getVertexIndices());
        assertArrayEquals(new float[]{0, 1, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1, 9, 10, 11, 1, 0, 1, 2, 1}, mesh1.getPositionData(), 0);
        assertArrayEquals(new float[]{0, 1, 0, 1, 0, 1, 0, 1, 2, 3}, mesh1.getTexCoordData(), 0);
        assertArrayEquals(new float[]{0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 3, 4, 5}, mesh1.getNormalData(), 0);
        Mesh mesh2 = geo1.meshes.get(1);
        assertArrayEquals(new short[]{0, 1, 1, 2, 3, 2}, mesh2.getVertexIndices());
        assertArrayEquals(new float[]{0, 1, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1, 9, 10, 11, 1}, mesh2.getPositionData(), 0);
        assertArrayEquals(new float[]{0, 1, 0, 1, 0, 1, 0, 1}, mesh2.getTexCoordData(), 0);
        assertArrayEquals(new float[]{0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2}, mesh2.getNormalData(), 0);

        Map<String, ColladaObject3D> objects = colladaFactory.getObjects();
        List<ColladaObject3D> anonymousObjects = colladaFactory.getAnonymousObjects();
        assertEquals(0, anonymousObjects.size());

        Mesh mat1Mesh = objects.get("dual_name").matMeshTO(mat1);
        Mesh mat2Mesh = objects.get("dual_name").matMeshTO(mat2);

        assertEquals(20, mat1Mesh.getAttributeData(VertexAttribute.POSITION).length);
        assertEquals(16, mat2Mesh.getAttributeData(VertexAttribute.POSITION).length);

        ColladaObject3D parent = objects.get("parent_name");
        mat1Mesh = objects.get("parent_name").matMeshTO(mat1);
        mat2Mesh = objects.get("parent_name").matMeshTO(mat2);

        Matrix4 parentMatrix = parent.getInitialMatrix();
        assertArrayEquals(parentMatrix.m, new float[]{1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 2, 1}, 0);

        //1st mesh of each material is from parent.
        //It is untransformed

        //TODO: Test mesh concatenation of indices
        //2nd mesh of each material is from child which refers to same geometry.
        //It has z translated by 3
        assertArrayEquals(mat1Mesh.getPositionData(),
                new float[]{0, 1, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1, 9, 10, 11, 1, 0, 1, 2, 1, //Mesh 1
                        0, 1, 5, 1, 3, 4, 8, 1, 6, 7, 11, 1, 9, 10, 14, 1, 0, 1, 5, 1 //Mesh 2
                }, 0);
        assertArrayEquals(mat2Mesh.getPositionData(),
                new float[]{0, 1, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1, 9, 10, 11, 1, //Mesh 1
                        0, 1, 5, 1, 3, 4, 8, 1, 6, 7, 11, 1, 9, 10, 14, 1     //Mesh 2
                }, 0);

        //It has z translated by 3, but normals are not affected by translation
        assertArrayEquals(mat1Mesh.getNormalData(),
                new float[]{0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 3, 4, 5, //Mesh 1
                        0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 3, 4, 5  //Mesh 2
                }, 0);
        assertArrayEquals(mat2Mesh.getNormalData(),
                new float[]{0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, //Mesh 1
                        0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2  //Mesh 2
                }, 0);


        ColladaObject3D no_mat = objects.get("no_mat_name");
        Mesh noMatMesh = no_mat.matMeshTO(defaultMat);
        assertEquals(16, noMatMesh.getPositionData().length);
    }

    @Test
    public void testCompleteLoadSketchup() throws XMLParseException, IOException, ParserConfigurationException, SAXException
    {
        FileInputStream input = new FileInputStream("src/test/assets/meshes/test_sketchup.dae");
        ColladaFactory colladaFactory = new ColladaFactory(true);
        Collada collada = colladaFactory.loadCollada(input);

        //Check materials
        Map<String, ColladaMaterial> materials = collada.materialsByName;
        ColladaMaterial mat1 = materials.get("mat_text1_name");
        ColladaMaterial mat2 = materials.get("mat_text2_name");
        ColladaMaterial matp = materials.get("mat_purple_name");
        ColladaMaterial defaultMat = materials.get("");
        assert mat1 != null;
        assert mat2 != null;
        assert matp != null;
        assert defaultMat != null;

        assertEquals(ColladaFactory.DEFAULT_AMBIENT, mat1.ambientColor);
        assertEquals(ColladaFactory.DEFAULT_DIFFUSE, mat1.diffuseColor);
        assertEquals(ColladaFactory.DEFAULT_SPECULAR, mat1.specularColor);
        assertEquals(ColladaFactory.DEFAULT_SHININESS, mat1.shininess, 0f);
        assertEquals("image1.png", mat1.imageFileName);

        assertEquals(new Color4F(0, 0, 0, 1), mat2.ambientColor);
        assertEquals(ColladaFactory.DEFAULT_DIFFUSE, mat2.diffuseColor);
        assertEquals(new Color4F(0.5f, 0.5f, 0.5f, 1f), mat2.specularColor);
        assertEquals(0.2f, mat2.shininess, 0f);
        assertEquals("image2.png", mat2.imageFileName);

        assertEquals(new Color4F(0, 0, 0, 1), matp.ambientColor);
        assertEquals(new Color4F(0.5f, 0f, 0.5f, 1f), matp.diffuseColor);
        assertEquals(new Color4F(0.5f, 0.5f, 0.5f, 1f), matp.specularColor);
        assertEquals(0.2f, matp.shininess, 0f);
        assert matp.imageFileName == null;

        //Check geometry
        Map<String, ColladaGeometry> geometries = colladaFactory.getGeometries();
        ColladaGeometry geo1 = geometries.get("geo1_id");

        //Sketchup just makes stuff up when it assigns materials to geometries.  Ignore this!
        assertEquals("completely made up id", geo1.materialIDs.get(0));

        Mesh mesh1 = geo1.meshes.get(0);
        assertArrayEquals(new short[]{0, 1, 1, 2, 3, 4, 0, 4, 2}, mesh1.getVertexIndices());
        assertArrayEquals(new float[]{0, 1, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1, 9, 10, 11, 1, 0, 1, 2, 1}, mesh1.getPositionData(), 0);
        assertArrayEquals(new float[]{0, 1, 0, 1, 0, 1, 0, 1, 2, 3}, mesh1.getTexCoordData(), 0);
        assertArrayEquals(new float[]{0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 3, 4, 5}, mesh1.getNormalData(), 0);

        //Other mesh data is ignored as they are just repeats of the same data
        Mesh mesh2 = geometries.get("geo2_id").meshes.get(0);
        Mesh mesh3 = geometries.get("geo3_id").meshes.get(0);

        //Check all the library meshGroups to make sure they were combined properly
        Map<String, MeshGroup> libMeshGroups = colladaFactory.getLibraryMeshGroups();
        MeshGroup lib_node_transformed = libMeshGroups.get("lib_node_transformed_id");
        MeshGroup lib_node1 = libMeshGroups.get("lib_node1_id");
        MeshGroup lib_node2 = libMeshGroups.get("lib_node2_id");
        MeshGroup lib_node3 = libMeshGroups.get("lib_node3_id");
        MeshGroup lib_node_comp = libMeshGroups.get("lib_node_comp_id");
        MeshGroup lib_node_comp_group = libMeshGroups.get("lib_node_comp_group_id");
        MeshGroup groupCompSubNode1 = libMeshGroups.get("groupCompSubNode1_id");
        MeshGroup groupCompSubNode2 = libMeshGroups.get("groupCompSubNode2_id");
        MeshGroup groupCompSubNode3 = libMeshGroups.get("groupCompSubNode3_id");

        //mesh1, has a 1 for the 2nd element in the position data, mesh2 has 2 and so on.
        assertEquals(1, lib_node1.getMeshes(mat1).get(0).getPositionData()[1], 0.0);
        assertEquals(1, lib_node2.getMeshes(mat2).get(0).getPositionData()[1], 0.0);
        assertEquals(2, lib_node3.getMeshes(mat1).get(0).getPositionData()[1], 0.0);
        assertEquals(2, lib_node_comp.getMeshes(mat1).get(0).getPositionData()[1], 0.0);
        assertEquals(3, lib_node_comp.getMeshes(mat2).get(0).getPositionData()[1], 0.0);

        //This one was scaled by factor of 2
        assertEquals(2, lib_node_transformed.getMeshes(mat1).get(0).getPositionData()[1], 0.0);

        //geo1 - untransformed
        assertEquals(1, lib_node_comp_group.getMeshes(mat1).get(0).getPositionData()[1], 0.0);
        //geo2 - y stretched by 3 (originally 2)
        assertEquals(6, lib_node_comp_group.getMeshes(mat1).get(1).getPositionData()[1], 0.0);
        //geo1 - y stretched by 2 (originally 1)
        assertEquals(2, lib_node_comp_group.getMeshes(mat2).get(0).getPositionData()[1], 0.0);
        //geo1 - normal transformation (y stretched by 1/2)
        assertEquals(0.5, lib_node_comp_group.getMeshes(mat2).get(0).getNormalData()[1], 0.0);

        //These should NOT be visible at top-level as they are intermediate nodes
        assert groupCompSubNode1 == null;
        assert groupCompSubNode2 == null;
        assert groupCompSubNode3 == null;

        Map<String, ColladaObject3D> objects = colladaFactory.getObjects();
        List<ColladaObject3D> anonymousObjects = colladaFactory.getAnonymousObjects();
        ColladaObject3D ana0 = anonymousObjects.get(0);

        assertEquals(2.0, ana0.matMeshTO(mat1).getPositionData()[1], 0.0);
        ColladaObject3D groupOfGroups = objects.get("groupOfGroups_name");

        //geo1 - y stretched by 3 (originally 1)
        assertEquals(3, groupOfGroups.matMeshTO(mat1).getPositionData()[1], 0.0);
        //geo2 - y stretched by 4 (originally 2)
        assertEquals(8, groupOfGroups.matMeshTO(mat2).getPositionData()[1], 0.0);
        ColladaObject3D reference = objects.get("reference_name");
        assertEquals(1.0, reference.matMeshTO(mat1).getPositionData()[1], 0.0);

        ColladaObject3D no_mat = objects.get("no_mat_name");
        assertEquals(2.0, no_mat.matMeshTO(defaultMat).getPositionData()[1], 0.0);
    }

    @Test
    public void testColladaSource()
    {
        ColladaAccessor colladaAccessor = new ColladaAccessor(3, 2, new boolean[]{true, false, true}, 3);
        float[] output = colladaAccessor.removeUnusedData(new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8});
        assertArrayEquals(new float[]{0, 2, 3, 5, 6, 8}, output, 0.0f);
    }


    /**
     * Tests uncollation.
     */
    @Test
    public void testColladaMeshNormalizer()
    {
        //2 interleaved sets of index data.  1st set is for input1 {0,1,1,2,3,0,0,0,2}.  2nd set is shared by input2 and input3 {0,0,0,0,0,1,0,1,0}
        short[] interleavedIndices = new short[]{0, 0, 1, 0, 1, 0, 2, 0, 3, 0, 0, 1, 0, 0, 0, 1, 2, 0};

        //There are a total of 9 indices defined, 5 are unique {0,0} , {1,0}, {2,0}, {3,0}, {0,1}
        int vertexCount = 9;

        Map<String, ColladaInput> inputs = new HashMap<>();
        //Input 1 has 4 items each size 3 {0,1,2}, {3,4,5}, {6,7,8}, {9,10,11} and offset is 0
        inputs.put(Collada.COLLADA_VERTEX_POSITION, new ColladaInput(Collada.COLLADA_VERTEX_POSITION, new ColladaSource(4, 3, new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}), 0));
        //Input 2 has 2 items each size 2 {0,1}, {2,3} and offset is 1
        inputs.put(Collada.COLLADA_VERTEX_TEXCOORD, new ColladaInput(Collada.COLLADA_VERTEX_TEXCOORD, new ColladaSource(2, 2, new float[]{0, 1, 2, 3}), 1));
        //Input 3 has 3 items each size 3 {0,1,2}, {3,4,5}, {6,7,8} and offset is 1
        inputs.put(Collada.COLLADA_VERTEX_NORMAL, new ColladaInput(Collada.COLLADA_VERTEX_NORMAL, new ColladaSource(3, 3, new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8}), 1));

        ColladaMesh colladaMesh = new ColladaMesh(inputs, interleavedIndices, vertexCount);
        ColladaMeshNormalizer colladaMeshNormalizer = new ColladaMeshNormalizer(colladaMesh, true);
        Mesh mesh = colladaMeshNormalizer.generateMesh();
        assertArrayEquals(new short[]{0, 1, 1, 2, 3, 4, 0, 4, 2}, mesh.getVertexIndices());
        assertArrayEquals(new float[]{0, 1, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1, 9, 10, 11, 1, 0, 1, 2, 1}, mesh.getAttributeData(VertexAttribute.POSITION), 0);
        assertArrayEquals(new float[]{0, 1, 0, 1, 0, 1, 0, 1, 2, 3}, mesh.getAttributeData(VertexAttribute.TEXCOORD), 0);
        assertArrayEquals(new float[]{0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 3, 4, 5}, mesh.getAttributeData(VertexAttribute.NORMAL), 0);
    }
}
