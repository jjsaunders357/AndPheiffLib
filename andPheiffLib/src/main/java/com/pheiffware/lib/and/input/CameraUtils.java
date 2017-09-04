package com.pheiffware.lib.and.input;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Size;
import android.util.SizeF;

import com.pheiffware.lib.utils.MathUtils;

/**
 * Contains several utilities related to cameras.
 * Created by Steve on 9/3/2017.
 */

public class CameraUtils
{

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static CameraCharacteristics getCameraCharacteristics(Context context, int desiredLensFacing) throws CameraAccessException
    {
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        for (String cameraID : cameraManager.getCameraIdList())
        {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraID);
            Integer lensFacing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
            if (lensFacing != null && lensFacing == desiredLensFacing)
            {
                return cameraCharacteristics;
            }
        }
        throw new CameraAccessException(CameraAccessException.CAMERA_ERROR, "Could not find camera");
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static CameraDisplayInfo getCameraDisplayInfo(Context context, int desiredLensFacing) throws CameraAccessException
    {
        return getCameraDisplayInfo(getCameraCharacteristics(context, desiredLensFacing));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static CameraDisplayInfo getCameraDisplayInfo(CameraCharacteristics cameraCharacteristics) throws CameraAccessException
    {
        //All keys available on all devices
        SizeF physicalSize = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        Size fullPixelDims = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
        Rect activePixelDims = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        float[] focalLengths = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
        Integer cameraOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

        float avgFocalLength = MathUtils.average(focalLengths);
        float activeSensorWidthmm = physicalSize.getWidth() * activePixelDims.width() / (float) fullPixelDims.getWidth();
        float activeSensorHeightmm = physicalSize.getHeight() * activePixelDims.height() / (float) fullPixelDims.getHeight();
        float pixelAspect = activePixelDims.width() / (float) activePixelDims.height();
        return new CameraDisplayInfo(avgFocalLength, activeSensorWidthmm, activeSensorHeightmm, pixelAspect, cameraOrientation);
    }
}
