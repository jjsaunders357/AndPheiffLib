package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.graphics.GColor;
import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.managed.mesh.MeshGroup;
import com.pheiffware.lib.graphics.managed.mesh.Object3D;
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
        FileInputStream input = new FileInputStream("src/main/assets/meshes/weird_blender_example.dae");
        ColladaFactory colladaFactory = new ColladaFactory(true);
        colladaFactory.loadCollada(input);
        input = new FileInputStream("src/main/assets/meshes/weird_sketchup_example.dae");
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
        Map<String, Material> materials = collada.materialsByName;
        Material mat1 = materials.get("mat_text1_name");
        Material mat2 = materials.get("mat_text2_name");
        Material matp = materials.get("mat_purple_name");
        Material defaultMat = materials.get("");
        assert mat1 != null;
        assert mat2 != null;
        assert matp != null;
        assert defaultMat != null;

        assertEquals(ColladaFactory.DEFAULT_AMBIENT, mat1.ambientColor);
        assertEquals(ColladaFactory.DEFAULT_DIFFUSE, mat1.diffuseColor);
        assertEquals(ColladaFactory.DEFAULT_SPECULAR, mat1.specularColor);
        assertEquals(ColladaFactory.DEFAULT_SHININESS, mat1.shininess, 0f);
        assertEquals("image1.png", mat1.imageFileName);

        assertEquals(new GColor(0, 0, 0, 1), mat2.ambientColor);
        assertEquals(ColladaFactory.DEFAULT_DIFFUSE, mat2.diffuseColor);
        assertEquals(new GColor(0.5f, 0.5f, 0.5f, 1f), mat2.specularColor);
        assertEquals(0.2f, mat2.shininess, 0f);
        assertEquals("image2.png", mat2.imageFileName);

        assertEquals(new GColor(0, 0, 0, 1), matp.ambientColor);
        assertEquals(new GColor(0.5f, 0f, 0.5f, 1f), matp.diffuseColor);
        assertEquals(new GColor(0.5f, 0.5f, 0.5f, 1f), matp.specularColor);
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
        assertArrayEquals(new short[]{0, 1, 1, 2, 3, 4, 0, 4, 2}, mesh1.vertexIndices);
        assertArrayEquals(new float[]{0, 1, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1, 9, 10, 11, 1, 0, 1, 2, 1}, mesh1.uniqueVertexData.get("POSITION"), 0);
        assertArrayEquals(new float[]{0, 1, 0, 1, 0, 1, 0, 1, 2, 3}, mesh1.uniqueVertexData.get("TEXCOORD"), 0);
        assertArrayEquals(new float[]{0, 1, 2, 0, 0, 1, 2, 0, 0, 1, 2, 0, 0, 1, 2, 0, 3, 4, 5, 0}, mesh1.uniqueVertexData.get("NORMAL"), 0);
        Mesh mesh2 = geo1.meshes.get(1);
        assertArrayEquals(new short[]{0, 1, 1, 2, 3, 2}, mesh2.vertexIndices);
        assertArrayEquals(new float[]{0, 1, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1, 9, 10, 11, 1}, mesh2.uniqueVertexData.get("POSITION"), 0);
        assertArrayEquals(new float[]{0, 1, 0, 1, 0, 1, 0, 1}, mesh2.uniqueVertexData.get("TEXCOORD"), 0);
        assertArrayEquals(new float[]{0, 1, 2, 0, 0, 1, 2, 0, 0, 1, 2, 0, 0, 1, 2, 0}, mesh2.uniqueVertexData.get("NORMAL"), 0);

        Map<String, Object3D> objects = colladaFactory.getObjects();
        List<Object3D> anonymousMeshGroups = colladaFactory.getAnonymousObjects();
        assertEquals(0, anonymousMeshGroups.size());

        MeshGroup dual = objects.get("dual_name").getMeshGroup();
        assertEquals(20, dual.getMeshes(mat1).get(0).uniqueVertexData.get("POSITION").length);
        assertEquals(16, dual.getMeshes(mat2).get(0).uniqueVertexData.get("POSITION").length);

        MeshGroup parent = objects.get("parent_name").getMeshGroup();
        assertEquals(20, parent.getMeshes(mat1).get(0).uniqueVertexData.get("POSITION").length);
        assertEquals(16, parent.getMeshes(mat2).get(0).uniqueVertexData.get("POSITION").length);

        MeshGroup no_mat = objects.get("no_mat_name").getMeshGroup();
        assertEquals(16, no_mat.getMeshes(defaultMat).get(0).uniqueVertexData.get("POSITION").length);
    }

    @Test
    public void testCompleteLoadSketchup() throws XMLParseException, IOException, ParserConfigurationException, SAXException
    {
        FileInputStream input = new FileInputStream("src/test/assets/meshes/test_sketchup.dae");
        ColladaFactory colladaFactory = new ColladaFactory(true);
        Collada collada = colladaFactory.loadCollada(input);

        //Check materials
        Map<String, Material> materials = collada.materialsByName;
        Material mat1 = materials.get("mat_text1_name");
        Material mat2 = materials.get("mat_text2_name");
        Material matp = materials.get("mat_purple_name");
        Material defaultMat = materials.get("");
        assert mat1 != null;
        assert mat2 != null;
        assert matp != null;
        assert defaultMat != null;

        assertEquals(ColladaFactory.DEFAULT_AMBIENT, mat1.ambientColor);
        assertEquals(ColladaFactory.DEFAULT_DIFFUSE, mat1.diffuseColor);
        assertEquals(ColladaFactory.DEFAULT_SPECULAR, mat1.specularColor);
        assertEquals(ColladaFactory.DEFAULT_SHININESS, mat1.shininess, 0f);
        assertEquals("image1.png", mat1.imageFileName);

        assertEquals(new GColor(0, 0, 0, 1), mat2.ambientColor);
        assertEquals(ColladaFactory.DEFAULT_DIFFUSE, mat2.diffuseColor);
        assertEquals(new GColor(0.5f, 0.5f, 0.5f, 1f), mat2.specularColor);
        assertEquals(0.2f, mat2.shininess, 0f);
        assertEquals("image2.png", mat2.imageFileName);

        assertEquals(new GColor(0, 0, 0, 1), matp.ambientColor);
        assertEquals(new GColor(0.5f, 0f, 0.5f, 1f), matp.diffuseColor);
        assertEquals(new GColor(0.5f, 0.5f, 0.5f, 1f), matp.specularColor);
        assertEquals(0.2f, matp.shininess, 0f);
        assert matp.imageFileName == null;

        //Check geometry
        Map<String, ColladaGeometry> geometries = colladaFactory.getGeometries();
        ColladaGeometry geo1 = geometries.get("geo1_id");

        //Sketchup just makes stuff up when it assigns materials to geometries.  Ignore this!
        assertEquals("completely made up id", geo1.materialIDs.get(0));

        Mesh mesh1 = geo1.meshes.get(0);
        assertArrayEquals(new short[]{0, 1, 1, 2, 3, 4, 0, 4, 2}, mesh1.vertexIndices);
        assertArrayEquals(new float[]{0, 1, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1, 9, 10, 11, 1, 0, 1, 2, 1}, mesh1.uniqueVertexData.get("POSITION"), 0);
        assertArrayEquals(new float[]{0, 1, 0, 1, 0, 1, 0, 1, 2, 3}, mesh1.uniqueVertexData.get("TEXCOORD"), 0);
        assertArrayEquals(new float[]{0, 1, 2, 0, 0, 1, 2, 0, 0, 1, 2, 0, 0, 1, 2, 0, 3, 4, 5, 0}, mesh1.uniqueVertexData.get("NORMAL"), 0);

        //Other mesh data is ignored as they are just repeats of the same data
        Mesh mesh2 = geometries.get("geo2_id").meshes.get(0);
        Mesh mesh3 = geometries.get("geo3_id").meshes.get(0);

        //Check all the library meshGroups to make sure they were combined properly
        Map<String, MeshGroup> libMeshGroups = colladaFactory.getLibraryMeshGroups();
        MeshGroup lib_node1 = libMeshGroups.get("lib_node1_id");
        MeshGroup lib_node2 = libMeshGroups.get("lib_node2_id");
        MeshGroup lib_node3 = libMeshGroups.get("lib_node3_id");
        MeshGroup lib_node_comp = libMeshGroups.get("lib_node_comp_id");
        MeshGroup lib_node_comp_group = libMeshGroups.get("lib_node_comp_group_id");
        MeshGroup groupCompSubNode1 = libMeshGroups.get("groupCompSubNode1_id");
        MeshGroup groupCompSubNode2 = libMeshGroups.get("groupCompSubNode2_id");
        MeshGroup groupCompSubNode3 = libMeshGroups.get("groupCompSubNode3_id");

        //mesh1, has a 1 for the 2nd element in the position data, mesh2 has 2 and so on.
        assertEquals(lib_node1.getMeshes(mat1).get(0).uniqueVertexData.get("POSITION")[1], 1.0, 0.0);
        assertEquals(lib_node2.getMeshes(mat2).get(0).uniqueVertexData.get("POSITION")[1], 1.0, 0.0);
        assertEquals(lib_node3.getMeshes(mat1).get(0).uniqueVertexData.get("POSITION")[1], 2.0, 0.0);
        assertEquals(lib_node_comp.getMeshes(mat1).get(0).uniqueVertexData.get("POSITION")[1], 2.0, 0.0);
        assertEquals(lib_node_comp.getMeshes(mat2).get(0).uniqueVertexData.get("POSITION")[1], 3.0, 0.0);

        assertEquals(lib_node_comp_group.getMeshes(mat1).get(0).uniqueVertexData.get("POSITION")[1], 1.0, 0.0);
        assertEquals(lib_node_comp_group.getMeshes(mat1).get(1).uniqueVertexData.get("POSITION")[1], 2.0, 0.0);
        assertEquals(lib_node_comp_group.getMeshes(mat2).get(0).uniqueVertexData.get("POSITION")[1], 1.0, 0.0);

        //These should NOT be visible at top-level as they are intermediate nodes
        assert groupCompSubNode1 == null;
        assert groupCompSubNode2 == null;
        assert groupCompSubNode3 == null;

        Map<String, Object3D> objects = colladaFactory.getObjects();
        List<Object3D> anonymousObjects = colladaFactory.getAnonymousObjects();
        assertEquals(anonymousObjects.get(0).getMeshGroup().getMeshes(mat1).get(0).uniqueVertexData.get("POSITION")[1], 2.0, 0.0);
        MeshGroup groupOfGroups = objects.get("groupOfGroups_name").getMeshGroup();
        assertEquals(groupOfGroups.getMeshes(mat1).get(0).uniqueVertexData.get("POSITION")[1], 1.0, 0.0);
        assertEquals(groupOfGroups.getMeshes(mat2).get(0).uniqueVertexData.get("POSITION")[1], 2.0, 0.0);
        MeshGroup reference = objects.get("reference_name").getMeshGroup();
        assertEquals(reference.getMeshes(mat1).get(0).uniqueVertexData.get("POSITION")[1], 1.0, 0.0);

        MeshGroup no_mat = objects.get("no_mat_name").getMeshGroup();
        assertEquals(no_mat.getMeshes(defaultMat).get(0).uniqueVertexData.get("POSITION")[1], 2.0, 0.0);
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
        inputs.put("POSITION", new ColladaInput("POSITION", new ColladaSource(4, 3, new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}), 0));
        //Input 2 has 2 items each size 2 {0,1}, {2,3} and offset is 1
        inputs.put("TEXCOORD", new ColladaInput("TEXCOORD", new ColladaSource(2, 2, new float[]{0, 1, 2, 3}), 1));
        //Input 3 has 3 items each size 3 {0,1,2}, {3,4,5}, {6,7,8} and offset is 1
        inputs.put("NORMAL", new ColladaInput("NORMAL", new ColladaSource(3, 3, new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8}), 1));

        //Homogenize output
        ColladaMeshNormalizer colladaMeshNormalizer = new ColladaMeshNormalizer(inputs, interleavedIndices, vertexCount, true);
        Mesh mesh = colladaMeshNormalizer.generateMesh();
        assertArrayEquals(new short[]{0, 1, 1, 2, 3, 4, 0, 4, 2}, mesh.vertexIndices);
        assertArrayEquals(new float[]{0, 1, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1, 9, 10, 11, 1, 0, 1, 2, 1}, mesh.uniqueVertexData.get("POSITION"), 0);
        assertArrayEquals(new float[]{0, 1, 0, 1, 0, 1, 0, 1, 2, 3}, mesh.uniqueVertexData.get("TEXCOORD"), 0);
        assertArrayEquals(new float[]{0, 1, 2, 0, 0, 1, 2, 0, 0, 1, 2, 0, 0, 1, 2, 0, 3, 4, 5, 0}, mesh.uniqueVertexData.get("NORMAL"), 0);
    }
}
