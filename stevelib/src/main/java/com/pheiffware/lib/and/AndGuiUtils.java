package com.pheiffware.lib.and;

import android.graphics.Matrix;
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
     * view.  Rectangle will be centered.
     *
     * @param viewWidth         available width to display in
     * @param viewHeight        available height to display in
     * @param renderAspectRatio the aspect ratio of what should be displayed in the view (width/height).  Sign is ignored.
     * @return a rectangle with maximum width/height with appropriate offset to center it
     */
    public static RectF calcRenderViewRectangle(int viewWidth, int viewHeight, float renderAspectRatio)
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

    public static class ClipTransform
    {
        public final RectF clip;
        public final Matrix renderToViewTransform;

        public ClipTransform(RectF clip, Matrix renderToViewTransform)
        {
            this.clip = clip;
            this.renderToViewTransform = renderToViewTransform;
        }
    }

    /**
     * Given a view with a width/height and a viewing area into something render-able, create a centered clipping region and a view transform.  This can be applied a canvas before
     * rendering to cause all draw commands, with coordinates in the renderRect to be transformed and clipped onto the canvas in a centered view with correct aspect ratio.
     *
     * @param viewWidth  width of the view/canvas being rendered into
     * @param viewHeight height of the view/canvas being rendered into
     * @param renderRect the range of coordinates which will be rendered to
     * @return an object containing the clipping rectangle and matrix transform to apply to a canvas
     */
    public static ClipTransform calcRenderClipTransform(int viewWidth, int viewHeight, RectF renderRect)
    {
        if (viewWidth == 0 || viewHeight == 0 || renderRect.width() == 0 || renderRect.height() == 0)
        {
            return new ClipTransform(new RectF(0, 0, 0, 0), new Matrix());
        }

        RectF clipRectangle = AndGuiUtils.calcRenderViewRectangle(viewWidth, viewHeight, renderRect.width() / renderRect.height());

        Matrix renderToViewTransform = new Matrix();
        //1st: Translate sim so that (renderX,renderY) is at (0,0)
        renderToViewTransform.preTranslate(-renderRect.left, -renderRect.top);

        //2nd: Scale sim to match viewing area (clipRectangle)
        float xScale = clipRectangle.width() / renderRect.width();
        float yScale = clipRectangle.height() / renderRect.height();
        renderToViewTransform.postScale(xScale, yScale);

        //3rd: Translate viewing area to the clipped region
        renderToViewTransform.postTranslate(clipRectangle.left, clipRectangle.top);

        return new ClipTransform(clipRectangle, renderToViewTransform);
    }
}
