package com.pheiffware.lib.physics;

import com.pheiffware.lib.utils.MathUtils;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Steve on 6/14/2017.
 */

public class MathTests
{
    @Test
    public void testCalcEvenBoundary()
    {
        int pos;
        pos = MathUtils.calcNextEvenBoundary(13, 8);
        assertEquals(16, pos);
        pos = MathUtils.calcNextEvenBoundary(8, 8);
        assertEquals(8, pos);
    }
}
