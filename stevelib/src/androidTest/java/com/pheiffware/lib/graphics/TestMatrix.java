package com.pheiffware.lib.graphics;


import android.test.InstrumentationTestCase;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

import com.pheiffware.lib.geometry.DecomposedTransform3D;

/**
 * Created by Steve on 3/22/2016.
 */

//TODO: Remove example activity test
//public class TestAct extends ActivityInstrumentationTestCase2
//{
//    public TestAct()
//    {
//        super(TestAct.class);

public class TestMatrix extends InstrumentationTestCase
{
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    @Test
    public void testApplyTransforms4()
    {
        Matrix4 matrix = Matrix4.newTranslation(1, 2, 3);
        float[] vectors = new float[]{0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 1};
        matrix.applyToFloatVectors(vectors);
        assertArrayEquals(new float[]{1, 2, 3, 1, 2, 3, 4, 1, 3, 4, 5, 1}, vectors, 0);
        float[] tVectors = matrix.newTransformedVectors(vectors);
        assertArrayEquals(new float[]{2, 4, 6, 1, 3, 5, 7, 1, 4, 6, 8, 1}, tVectors, 0);
    }

    @Test
    public void testApplyTransforms3()
    {
        Matrix3 matrix = Matrix3.newFromFloats(new float[]{1, 0, 0, 0, 1, 0, 1, 2, 1});
        float[] vectors = new float[]{0, 0, 1, 1, 1, 1, 2, 2, 1};
        matrix.applyToFloatVectors(vectors);
        assertArrayEquals(new float[]{1, 2, 1, 2, 3, 1, 3, 4, 1}, vectors, 0);
        float[] tVectors = matrix.newTransformedVectors(vectors);
        assertArrayEquals(new float[]{2, 4, 1, 3, 5, 1, 4, 6, 1}, tVectors, 0);
    }

    @Test
    public void testDecompose()
    {
        Matrix4 matrix = Matrix4.multiply(Matrix4.newTranslation(1, 2, 3), Matrix4.newRotate(20, 0, 0, 1), Matrix4.newScale(2, 3, 4));
        DecomposedTransform3D decompose = matrix.decompose();
        Matrix4 translation = decompose.getTranslation();
        Matrix4 rotation = decompose.getRotation();
        Matrix4 scale = decompose.getScale();
        assertEquals(1, translation.m[12], 0);
        assertEquals(2, translation.m[13], 0);
        assertEquals(3, translation.m[14], 0);

        assertEquals((float) Math.cos(Math.PI * 20 / 180.0), rotation.m[0], 0.00001f);
        assertEquals((float) Math.sin(Math.PI * 20 / 180.0), rotation.m[1], 0.00001f);
        assertEquals((float) -Math.sin(Math.PI * 20 / 180.0), rotation.m[4], 0.00001f);
        assertEquals((float) Math.cos(Math.PI * 20 / 180.0), rotation.m[5], 0.00001f);

        assertEquals(2f, scale.m[0], 0.00001f);
        assertEquals(3f, scale.m[5], 0.00001f);
        assertEquals(4f, scale.m[10], 0.00001f);

    }
}