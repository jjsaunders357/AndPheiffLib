package com.pheiffware.lib.and;

import android.graphics.RectF;

/**
 * Various utilities related to Android GUI development.
 * <p/>
 * Created by Steve on 4/3/2016.
 */
public class AndGuiUtils
{
    /**
     * Given a viewing area with a given width/height and something to render with a given aspect ratio, calculate the largest rectangle with the renderAspectRatio that fits in the
     * view.
     *
     * @param viewWidth         available width to display in
     * @param viewHeight        available height to display in
     * @param renderAspectRatio the aspect ratio of what should be displayed in the view (width/height).  Sign is ignored.
     * @return a rectangle with maximum width/height with appropriate offset to center it
     */
    public static RectF getRenderViewRectangle(int viewWidth, int viewHeight, float renderAspectRatio)
    {
        boolean widthConstrained;
        if (viewHeight == 0)
        {
            widthConstrained = true;
        }
        else
        {
            float viewAspect = viewWidth / (float) viewHeight;
            widthConstrained = Math.abs(renderAspectRatio) > Math.abs(viewAspect);
        }
        float width, height;
        float x, y;
        if (widthConstrained)
        {
            //We are width constrained (we need to use all of the width, but only some of the height)
            width = viewWidth;
            x = 0;
            height = viewWidth / renderAspectRatio;
            y = (viewHeight - height) / 2;
        }
        else
        {
            //We are height constrained (we need to use all of the height, but only some of the width)
            height = viewHeight;
            y = 0;
            width = viewHeight * renderAspectRatio;
            x = (viewWidth - width) / 2;
        }
        return new RectF(x, y, x + width, y + height);
    }
}
