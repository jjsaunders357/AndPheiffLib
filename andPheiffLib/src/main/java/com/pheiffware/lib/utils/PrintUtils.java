package com.pheiffware.lib.utils;

import java.nio.ByteBuffer;

/**
 * Created by Steve on 6/15/2017.
 */

public class PrintUtils
{

    public static void printShorts(ByteBuffer byteBuffer)
    {
        for (int i = 0; i < byteBuffer.position(); i += 2)
        {
            System.out.print(byteBuffer.getShort(i));
            System.out.print(", ");
        }
        System.out.println();
    }

    public static void printFloats(ByteBuffer byteBuffer)
    {
        for (int i = 0; i < byteBuffer.position(); i += 4)
        {
            System.out.print(byteBuffer.getFloat(i));
            System.out.print(", ");
        }
        System.out.println();
    }
}
