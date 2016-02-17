package com.pheiffware.lib.graphics.managed.Collada;

import com.pheiffware.lib.graphics.managed.collada.Collada;
import com.pheiffware.lib.graphics.managed.collada.ColladaAccessor;
import com.pheiffware.lib.graphics.managed.collada.ColladaInput;
import com.pheiffware.lib.graphics.managed.collada.ColladaMeshUncollator;
import com.pheiffware.lib.utils.dom.XMLParseException;
import com.pheiffware.lib.graphics.managed.collada.ColladaSource;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
/**
 * Created by Steve on 2/15/2016.
 */
public class TestCollada
{
    @Test
    public void testc() throws XMLParseException, FileNotFoundException
    {
        FileInputStream input = new FileInputStream("src/main/assets/meshes/test_sketch.dae");
        Collada c = new Collada();
        c.loadCollada(input);
        System.out.println("Done");
    }

    @Test
    public void testColladaSource()
    {
        ColladaAccessor colladaAccessor = new ColladaAccessor(3, 2, new boolean[]{true, false, true}, 3);
        float[] output = colladaAccessor.collateData(new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8});
        assertArrayEquals(new float[]{0, 2, 3, 5, 6, 8}, output, 0.0f);
    }


    /**
     * Tests uncollation.
     */
    @Test
    public void testColladaCollator()
    {
        //2 interleaved sets of index data.  1st set is for input1 {0,1,1,2,3,0,0,0}.  2nd set is shared by input2 and input3 {0,0,0,0,0,1,0,1}
        short[] collatedIndices = new short[]{0, 0, 1, 0, 1, 0, 2, 0, 3, 0, 0, 1, 0, 0, 0, 1};

        //There are a total of 8 indices defined, 5 are unique {0,0} , {1,0}, {2,0}, {3,0}, {0,1}
        int vertexCount = 8;

        Map<String, ColladaInput> inputs = new HashMap<>();
        //Input 1 has 4 items each size 3 {0,1,2}, {3,4,5}, {6,7,8}, {9,10,11}
        inputs.put("input1", new ColladaInput("input1", new ColladaSource(4, 3, new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}), 0));
        //Input 2 has 2 items each size 2 {0,1}, {2,3}
        inputs.put("input2", new ColladaInput("input2", new ColladaSource(2, 2, new float[]{0, 1, 2, 3}), 1));
        //Input 3 has 3 items each size 3 {0,1,2}, {3,4,5}, {6,7,8}
        inputs.put("input3", new ColladaInput("input3", new ColladaSource(3, 3, new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8}), 1));

        ColladaMeshUncollator colladaMeshUncollator = new ColladaMeshUncollator(inputs, collatedIndices, vertexCount);
        Mesh mesh = colladaMeshUncollator.createMesh();
        assertArrayEquals(new short[]{0, 1, 1, 2, 3, 4, 0, 4}, mesh.primitiveIndices);
        assertArrayEquals(new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 0, 1, 2}, mesh.data.get("input1"), 0);
        assertArrayEquals(new float[]{0, 1, 0, 1, 0, 1, 0, 1, 2, 3}, mesh.data.get("input2"), 0);
        assertArrayEquals(new float[]{0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 3, 4, 5}, mesh.data.get("input3"), 0);
    }
}
