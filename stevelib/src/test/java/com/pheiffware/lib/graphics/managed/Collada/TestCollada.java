package com.pheiffware.lib.graphics.managed.Collada;

import com.pheiffware.lib.graphics.GColor;
import com.pheiffware.lib.graphics.managed.collada.Collada;
import com.pheiffware.lib.graphics.managed.collada.ColladaAccessor;
import com.pheiffware.lib.graphics.managed.collada.ColladaGeometry;
import com.pheiffware.lib.graphics.managed.collada.ColladaInput;
import com.pheiffware.lib.graphics.managed.collada.ColladaMeshUncollator;
import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.graphics.managed.mesh.MeshGroup;
import com.pheiffware.lib.utils.dom.XMLParseException;
import com.pheiffware.lib.graphics.managed.collada.ColladaSource;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;

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
 * Created by Steve on 2/15/2016.
 */
public class TestCollada
{
    @Test
    public void testCompleteLoadBlender() throws XMLParseException, IOException, ParserConfigurationException, SAXException
    {
        FileInputStream input = new FileInputStream("src/test/assets/meshes/test_blender.dae");
        Collada collada = new Collada();
        collada.loadCollada(input);

        //Check materials
        Map<String, Material> materials = collada.getMaterialsByName();
        Material mat1 = materials.get("mat_text1_name");
        Material mat2 = materials.get("mat_text2_name");
        Material matp = materials.get("mat_purple_name");
        assert mat1 != null;
        assert mat2 != null;
        assert matp != null;

        assertEquals(Collada.DEFAULT_AMBIENT, mat1.ambientColor);
        assertEquals(Collada.DEFAULT_DIFFUSE_TEXTURE, mat1.diffuseColor);
        assertEquals(Collada.DEFAULT_SPECULAR, mat1.specularColor);
        assertEquals(Collada.DEFAULT_SHININESS, mat1.shininess, 0f);
        assertEquals("image1.png", mat1.imageFileName);

        assertEquals(new GColor(0, 0, 0, 1), mat2.ambientColor);
        assertEquals(Collada.DEFAULT_DIFFUSE_TEXTURE, mat2.diffuseColor);
        assertEquals(new GColor(0.5f, 0.5f, 0.5f, 1f), mat2.specularColor);
        assertEquals(0.2f, mat2.shininess, 0f);
        assertEquals("image2.png", mat2.imageFileName);

        assertEquals(new GColor(0, 0, 0, 1), matp.ambientColor);
        assertEquals(new GColor(0.5f, 0f, 0.5f, 1f), matp.diffuseColor);
        assertEquals(new GColor(0.5f, 0.5f, 0.5f, 1f), matp.specularColor);
        assertEquals(0.2f, matp.shininess, 0f);
        assert matp.imageFileName == null;

        //Check geometry
        Map<String, ColladaGeometry> geometries = collada.getGeometries();
        ColladaGeometry geo1 = geometries.get("geo1_id");
        assertEquals("mat_text1_id", geo1.materialIDs.get(0));
        assertEquals("mat_text2_id", geo1.materialIDs.get(1));

        Mesh mesh1 = geo1.meshes.get(0);
        assertArrayEquals(new short[]{0, 1, 1, 2, 3, 4, 0, 4, 2}, mesh1.primitiveIndices);
        assertArrayEquals(new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 0, 1, 2}, mesh1.data.get("POSITION"), 0);
        assertArrayEquals(new float[]{0, 1, 0, 1, 0, 1, 0, 1, 2, 3}, mesh1.data.get("TEXCOORD"), 0);
        assertArrayEquals(new float[]{0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 3, 4, 5}, mesh1.data.get("NORMAL"), 0);

        Mesh mesh2 = geo1.meshes.get(1);
        assertArrayEquals(new short[]{0, 1, 1, 2, 3, 2}, mesh2.primitiveIndices);
        assertArrayEquals(new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, mesh2.data.get("POSITION"), 0);
        assertArrayEquals(new float[]{0, 1, 0, 1, 0, 1, 0, 1}, mesh2.data.get("TEXCOORD"), 0);
        assertArrayEquals(new float[]{0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2}, mesh2.data.get("NORMAL"), 0);



    }

    @Test
    public void testCompleteLoadSketchup() throws XMLParseException, IOException, ParserConfigurationException, SAXException
    {
        FileInputStream input = new FileInputStream("src/test/assets/meshes/test_sketchup.dae");
        Collada collada = new Collada();
        collada.loadCollada(input);

        //Check materials
        Map<String, Material> materials = collada.getMaterialsByName();
        Material mat1 = materials.get("mat_text1_name");
        Material mat2 = materials.get("mat_text2_name");
        Material matp = materials.get("mat_purple_name");
        assert mat1 != null;
        assert mat2 != null;
        assert matp != null;

        assertEquals(Collada.DEFAULT_AMBIENT, mat1.ambientColor);
        assertEquals(Collada.DEFAULT_DIFFUSE_TEXTURE, mat1.diffuseColor);
        assertEquals(Collada.DEFAULT_SPECULAR, mat1.specularColor);
        assertEquals(Collada.DEFAULT_SHININESS, mat1.shininess, 0f);
        assertEquals("image1.png", mat1.imageFileName);

        assertEquals(new GColor(0, 0, 0, 1), mat2.ambientColor);
        assertEquals(Collada.DEFAULT_DIFFUSE_TEXTURE, mat2.diffuseColor);
        assertEquals(new GColor(0.5f, 0.5f, 0.5f, 1f), mat2.specularColor);
        assertEquals(0.2f, mat2.shininess, 0f);
        assertEquals("image2.png", mat2.imageFileName);

        assertEquals(new GColor(0, 0, 0, 1), matp.ambientColor);
        assertEquals(new GColor(0.5f, 0f, 0.5f, 1f), matp.diffuseColor);
        assertEquals(new GColor(0.5f, 0.5f, 0.5f, 1f), matp.specularColor);
        assertEquals(0.2f, matp.shininess, 0f);
        assert matp.imageFileName == null;

        //Check geometry
        Map<String, ColladaGeometry> geometries = collada.getGeometries();
        ColladaGeometry geo1 = geometries.get("geo1_id");

        //Sketchup just makes stuff up when it assigns materials to geometries.  Ignore this!
        assertEquals("completely made up id", geo1.materialIDs.get(0));

        Mesh mesh1 = geo1.meshes.get(0);
        assertArrayEquals(new short[]{0, 1, 1, 2, 3, 4, 0, 4, 2}, mesh1.primitiveIndices);
        assertArrayEquals(new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 0, 1, 2}, mesh1.data.get("POSITION"), 0);
        assertArrayEquals(new float[]{0, 1, 0, 1, 0, 1, 0, 1, 2, 3}, mesh1.data.get("TEXCOORD"), 0);
        assertArrayEquals(new float[]{0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 3, 4, 5}, mesh1.data.get("NORMAL"), 0);

        //Other mesh data is ignored as they are just repeats of the same data
        Mesh mesh2 = geometries.get("geo2_id").meshes.get(0);
        Mesh mesh3 = geometries.get("geo3_id").meshes.get(0);

        //Check all the library meshGroups to make sure they were combined properly
        Map<String, MeshGroup> libMeshGroups = collada.getLibraryMeshGroups();
        MeshGroup lib_node1 = libMeshGroups.get("lib_node1_id");
        MeshGroup lib_node2 = libMeshGroups.get("lib_node2_id");
        MeshGroup lib_node3 = libMeshGroups.get("lib_node3_id");
        MeshGroup lib_node_comp = libMeshGroups.get("lib_node_comp_id");
        MeshGroup lib_node_comp_group = libMeshGroups.get("lib_node_comp_group_id");
        MeshGroup groupCompSubNode1 = libMeshGroups.get("groupCompSubNode1_id");
        MeshGroup groupCompSubNode2 = libMeshGroups.get("groupCompSubNode2_id");
        MeshGroup groupCompSubNode3 = libMeshGroups.get("groupCompSubNode3_id");

        //mesh1, has a 1 for the 2nd element in the position data, mesh2 has 2 and so on.
        assertEquals(lib_node1.getMesh(mat1).get(0).data.get("POSITION")[1], 1.0, 0.0);
        assertEquals(lib_node2.getMesh(mat2).get(0).data.get("POSITION")[1], 1.0, 0.0);
        assertEquals(lib_node3.getMesh(mat1).get(0).data.get("POSITION")[1], 2.0, 0.0);
        assertEquals(lib_node_comp.getMesh(mat1).get(0).data.get("POSITION")[1], 2.0, 0.0);
        assertEquals(lib_node_comp.getMesh(mat2).get(0).data.get("POSITION")[1], 3.0, 0.0);

        //TODO: Once matrix transformations have been applied, the signatures of these will change by factors of 1,2 and 3 (scale y)
        assertEquals(lib_node_comp_group.getMesh(mat1).get(0).data.get("POSITION")[1], 1.0, 0.0);
        assertEquals(lib_node_comp_group.getMesh(mat1).get(1).data.get("POSITION")[1], 2.0, 0.0);
        assertEquals(lib_node_comp_group.getMesh(mat2).get(0).data.get("POSITION")[1], 1.0, 0.0);

        //These should NOT be visible at top-level as they are intermediate nodes
        assert groupCompSubNode1 == null;
        assert groupCompSubNode2 == null;
        assert groupCompSubNode3 == null;

        Map<String, MeshGroup> meshGroups = collada.getMeshGroups();
        List<MeshGroup> annonymousMeshGroups = collada.getAnnonymousMeshGroups();
        assertEquals(annonymousMeshGroups.get(0).getMesh(mat1).get(0).data.get("POSITION")[1], 2.0, 0.0);
        MeshGroup groupOfGroups = meshGroups.get("groupOfGroups_id");
        assertEquals(groupOfGroups.getMesh(mat1).get(0).data.get("POSITION")[1], 1.0, 0.0);
        assertEquals(groupOfGroups.getMesh(mat2).get(0).data.get("POSITION")[1], 2.0, 0.0);
        MeshGroup reference = meshGroups.get("reference_id");
        assertEquals(reference.getMesh(mat1).get(0).data.get("POSITION")[1], 1.0, 0.0);
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
    public void testColladaCollator()
    {
        //2 interleaved sets of index data.  1st set is for input1 {0,1,1,2,3,0,0,0,2}.  2nd set is shared by input2 and input3 {0,0,0,0,0,1,0,1,0}
        short[] collatedIndices = new short[]{0, 0, 1, 0, 1, 0, 2, 0, 3, 0, 0, 1, 0, 0, 0, 1, 2, 0};

        //There are a total of 9 indices defined, 5 are unique {0,0} , {1,0}, {2,0}, {3,0}, {0,1}
        int vertexCount = 9;

        Map<String, ColladaInput> inputs = new HashMap<>();
        //Input 1 has 4 items each size 3 {0,1,2}, {3,4,5}, {6,7,8}, {9,10,11} and offset is 0
        inputs.put("input1", new ColladaInput("input1", new ColladaSource(4, 3, new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}), 0));
        //Input 2 has 2 items each size 2 {0,1}, {2,3} and offset is 1
        inputs.put("input2", new ColladaInput("input2", new ColladaSource(2, 2, new float[]{0, 1, 2, 3}), 1));
        //Input 3 has 3 items each size 3 {0,1,2}, {3,4,5}, {6,7,8} and offset is 1
        inputs.put("input3", new ColladaInput("input3", new ColladaSource(3, 3, new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8}), 1));

        ColladaMeshUncollator colladaMeshUncollator = new ColladaMeshUncollator(inputs, collatedIndices, vertexCount);
        Mesh mesh = colladaMeshUncollator.createMesh();
        assertArrayEquals(new short[]{0, 1, 1, 2, 3, 4, 0, 4, 2}, mesh.primitiveIndices);
        assertArrayEquals(new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 0, 1, 2}, mesh.data.get("input1"), 0);
        assertArrayEquals(new float[]{0, 1, 0, 1, 0, 1, 0, 1, 2, 3}, mesh.data.get("input2"), 0);
        assertArrayEquals(new float[]{0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 3, 4, 5}, mesh.data.get("input3"), 0);
    }


}
