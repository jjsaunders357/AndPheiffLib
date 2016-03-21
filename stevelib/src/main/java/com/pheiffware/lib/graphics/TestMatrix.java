package com.pheiffware.lib.graphics;

import com.pheiffware.lib.geometry.DecomposedTransform3D;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by Steve on 3/20/2016.
 */
public class TestMatrix
{
    @Test
    public void testApplyTransforms4()
    {
        Matrix4 matrix = Matrix4.newTranslation(1, 2, 3);
        float[] vectors = new float[]{0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 1};
        matrix.applyToFloatVectors(vectors);
        assertArrayEquals(new float[]{1, 2, 3, 1, 2, 3, 4, 1, 3, 4, 5, 1}, vectors, 0);
        float[] tvectors = matrix.newTransformedVectors(vectors);
        assertArrayEquals(new float[]{2, 4, 6, 1, 3, 5, 7, 1, 4, 6, 8, 1}, tvectors, 0);
    }

    @Test
    public void testApplyTransforms3()
    {
        Matrix3 matrix = Matrix3.newFromFloats(new float[]{1, 0, 0, 0, 1, 0, 1, 2, 1});
        float[] vectors = new float[]{0, 0, 1, 1, 1, 1, 2, 2, 1};
        matrix.applyToFloatVectors(vectors);
        assertArrayEquals(new float[]{1, 2, 1, 2, 3, 1, 3, 4, 1}, vectors, 0);
        float[] tvectors = matrix.newTransformedVectors(vectors);
        assertArrayEquals(new float[]{2, 4, 1, 3, 5, 1, 4, 6, 1}, tvectors, 0);
    }
//    @Test
//    public void testDecompose()
//    {
//        Matrix4 matrix = Matrix4.multiply(Matrix4.newTranslation(1,2,3),Matrix4.newRotate(20, 0, 0, 1),Matrix4.newScale(2, 3, 4));
//        DecomposedTransform3D decompose = matrix.decompose();
//        Matrix4 translation = decompose.getTranslation();
//        Matrix4 rotation = decompose.getRotation();
//        Matrix4 scale = decompose.getScale();
//        assertEquals(translation.m[3],1,0);
//        assertEquals(translation.m[7],2,0);
//        assertEquals(translation.m[11],3,0);
//
//        assertEquals(rotation.m[0],Math.cos(Math.PI*20/180.0),0);
//        assertEquals(rotation.m[1],-Math.sin(Math.PI*20/180.0),0);
//        assertEquals(rotation.m[4],Math.sin(Math.PI*20/180.0),0);
//        assertEquals(rotation.m[5],Math.cos(Math.PI*20/180.0),0);
//
//        assertEquals(scale.m[0],2,0);
//        assertEquals(scale.m[5],3,0);
//        assertEquals(scale.m[10],4,0);
//
//    }
}
