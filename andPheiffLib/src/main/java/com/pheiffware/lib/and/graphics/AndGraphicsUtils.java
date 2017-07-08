package com.pheiffware.lib.and.graphics;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;

/**
 * OpenGL graphics utilities which only apply to Android.
 * <p/>
 * Created by Steve on 4/23/2016.
 */
public class AndGraphicsUtils
{
    //3.0
    //Useful reference for remembering the minimum amount openGL has to provide
    public static final int MINMAX_TEXTURE_SIZE = 2048;
    public static final int MINMAX_3D_TEXTURE_SIZE = 256;
    public static final int MINMAX_COLOR_ATTACHMENTS = 4;
    public static final int MINMAX_VERTEX_ATTRIBUTES = 16; //4 elements each (4 floats for example)
    public static final int MINMAX_VERTEX_UNIFORM_VECTORS = 256; //Includes constants and immediates.  This is number of vectors (4 elements).  See special rules about how this works.
    public static final int MINMAX_FRAGMENT_UNIFORM_VECTORS = 224;

    //2.0
    //public static final int MINMAX_VERTEX_ATTRIBUTES = 8; //4 elements each (4 floats for example)

    public static final int GL_VERSION_31 = 0x30001;
    public static final int GL_VERSION_30 = 0x30000;
    public static final int GL_VERSION_20 = 0x20000;
    public static final int GL_VERSION_11 = 0x10001;
    public static final int GL_VERSION_10 = 0x10000;

    public static String glVersionString(int version)
    {
        return (version >> 16) + "." + (version & (0x1111));
    }

    /**
     * Get the major openGL version that is actually available on this device (ie 3).
     *
     * @param context
     * @return
     */
    public static int getDeviceGLMajorVersion(Context context)
    {
        int deviceGLVersion = getDeviceGLVersion(context);
        return deviceGLVersion >> 16;
    }

    /**
     * Gets the openGL version that is actually available on this device (ie GL_VERSION_31).
     *
     * @param context
     * @return
     */
    public static int getDeviceGLVersion(Context context)
    {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager
                .getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion;
    }
}
