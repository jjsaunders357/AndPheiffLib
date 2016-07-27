/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * General utilities.
 */
public class Utils
{


    /**
     * Actively destroys a direct buffer. Calling this guarantees that memory is freed immediately. NOTE: Does not work in android!
     */

    public static void deallocateDirectByteBuffer(ByteBuffer directByteBuffer)
    {
        //TODO: Implement, Use the reflection to call java.nio.DirectByteBuffer.free()
    }

    /**
     * Get the time elapsed between an earlier time stamp and now.
     *
     * @param earlierTimeStamp earlier call to System.nanoTime()
     * @return time elapsed in seconds
     */
    public static final double getTimeElapsed(long earlierTimeStamp)
    {
        return getTimeElapsed(earlierTimeStamp, System.nanoTime());
    }

    /**
     * Get time elapsed between 2 nanosecond time stamps.
     *
     * @param earlierTimeStamp
     * @param laterTimeStamp
     * @return
     */
    public static final double getTimeElapsed(long earlierTimeStamp, long laterTimeStamp)
    {
        return (laterTimeStamp - earlierTimeStamp) / 1000000000.0;
    }

    /**
     * Print time elapsed from an early call to System.nanoTime() in seconds, prefixed by a message.
     *
     * @param earlierTimeStamp earlier call to System.nanoTime()
     */
    public static void printTimeElapsed(String message, long earlierTimeStamp)
    {
        System.out.println(message + " " + getTimeElapsed(earlierTimeStamp));
    }

    @SuppressWarnings("unchecked")
    public static <T> T loadObj(String path, Class<T> cls) throws IOException, ClassNotFoundException
    {
        try (InputStream file = new FileInputStream(path);
             InputStream buffer = new BufferedInputStream(file);
             ObjectInput input = new ObjectInputStream(buffer))
        {
            return (T) input.readObject();
        }
    }

    public static void saveObj(String path, Object object) throws IOException
    {

        try (OutputStream file = new FileOutputStream(path);
             OutputStream buffer = new BufferedOutputStream(file);
             ObjectOutput output = new ObjectOutputStream(buffer))
        {
            output.writeObject(object);
        }
    }

    /**
     * Quick and dirty way to copy and object using serialization.  THIS IS NOT EFFICIENT AT ALL.
     *
     * @param object
     * @return
     */
    public static <T> T copyObj(T object)
    {
        try
        {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput output = new ObjectOutputStream(buffer);
            output.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer.toByteArray());
            return (T) new ObjectInputStream(bais).readObject();
        }
        catch (ClassNotFoundException | IOException e)
        {
            throw new RuntimeException("Copy Error", e);
        }

    }

    public static <T> Set<T> setFromArray(T[] array)
    {
        Set<T> set = new HashSet<>(array.length * 2);
        for (T element : array)
        {
            set.add(element);
        }
        return set;
    }

}
