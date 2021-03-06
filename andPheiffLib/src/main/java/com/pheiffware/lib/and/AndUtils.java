package com.pheiffware.lib.and;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Android specific utility methods.
 * <p/>
 * Created by Steve on 4/23/2016.
 */
public class AndUtils
{
    /**
     * Loads the contents of a file in the assets directory as a string. Use '/' as separator.
     *
     * @param assetManager  assetManager
     * @param assetFileName assetFileName (relative path to asset file from assets folder)
     * @return The string contents of the asset
     * @throws IOException Any problem loading asset
     */
    public static String loadAssetAsString(AssetManager assetManager, String assetFileName) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(assetFileName), "UTF-8"));
        int character;
        StringBuilder builder = new StringBuilder();
        do
        {
            character = reader.read();
            if (character == -1)
            {
                break;
            }
            else
            {
                builder.append((char) character);
            }
        } while (true);
        return builder.toString();
    }

    public static URL getAssetURL(String assetPath) throws MalformedURLException
    {
        return new URL("file:///android_asset/" + assetPath);
    }

    /**
     * Logs a life cycle method for a given object.  Log message is debug and will include class name.
     *
     * @param object
     * @param lifeCycleMethodName
     */
    public static void logLC(Object object, String lifeCycleMethodName)
    {
        Log.d("LifeC - " + object.getClass().getSimpleName(), lifeCycleMethodName);
    }

    /**
     * Load an image from an asset file.
     *
     * @param assetManager
     * @param imageAssetPath Path to image
     * @return image object
     * @throws IOException
     */
    public static Bitmap loadBitmapAsset(AssetManager assetManager, String imageAssetPath) throws IOException
    {
        return BitmapFactory.decodeStream(assetManager.open(imageAssetPath));
    }

    /**
     * Get the screen's current brightness level.
     *
     * @param window
     * @return
     */
    public static float getBrightness(Window window)
    {
        WindowManager.LayoutParams layout = window.getAttributes();
        return layout.screenBrightness;
    }

    /**
     * Set the screen's brightness level.
     *
     * @param window
     * @param brightness
     */
    public static void setBrightness(Window window, float brightness)
    {
        WindowManager.LayoutParams layout = window.getAttributes();
        layout.screenBrightness = brightness;
        window.setAttributes(layout);
    }

    /**
     * Get the constant describing the rotation of the screen.
     *
     * @param context
     * @return
     */
    public static int getDisplayRotation(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getRotation();
    }

    /**
     * Untested, taken from stack-overflow.
     *
     * @param activity
     */
    public static void disableScreenOrientationChange(Activity activity)
    {
        final int orientation = activity.getResources().getConfiguration().orientation;
        final int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90)
        {
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            else if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
        else if (rotation == Surface.ROTATION_180 || rotation == Surface.ROTATION_270)
        {
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            }
            else if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
        }
    }

    /**
     * Untested, taken from stack-overflow.
     *
     * @param activity
     */
    public static void enableScreenOrientationChange(Activity activity)
    {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
