package com.pheiffware.lib.and.gui.graphics;
/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.Region;
import android.view.View;

import com.pheiffware.lib.and.AndGuiUtils;

import java.util.Timer;
import java.util.TimerTask;

import static com.pheiffware.lib.and.AndGuiUtils.ClipTransform;

/**
 * A view which provides 2 keys features:
 * <p/>
 * 1. The view is invalidated at a configurable period.  This causes onDraw() to be called over and over for the purposes of rendering something in real time.
 * <p/>
 * 2. When rendering in draw, all positions can be specified in "render space".  The specified visibleRenderArea in this space is displayed in the largest possible rectangle in the
 * view which maintains a consistent aspect ratio AND which is centered and clipped.
 */
public class RenderView extends View
{
    //Timer to trigger invalidate() which forces the view to redraw.
    private Timer timer = null;

    //The time between redraws of the view in ms
    private int renderPeriodMS;

    //The area to "render".  All calls to Canvas.draw() methods will map coordinates in this area into centered rectangle in the view with maximum size and the same aspect ratio.
    RectF visibleRenderArea;

    //The clip and transform applied to canvas on each draw.
    private ClipTransform clipTransform = new ClipTransform(new RectF(0, 0, 0, 0), new Matrix());

    /**
     * @param context
     */
    public RenderView(Context context, int renderPeriodMS, RectF visibleRenderArea)
    {
        super(context);
        this.renderPeriodMS = renderPeriodMS;
        setRenderView(visibleRenderArea);
    }

    /**
     * Enables/disables the view invalidation thread as it becomes visible/invisible.
     *
     * @param visibility
     */
    protected void onWindowVisibilityChanged(int visibility)
    {
        if (visibility == VISIBLE)
        {
            if (timer == null)
            {
                timer = new Timer();
            }
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    postInvalidate();
                }
            }, 0, renderPeriodMS);
        }
        else
        {
            if (timer != null)
            {
                timer.cancel();
                timer.purge();
            }
            timer = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.clipRect(clipTransform.clip, Region.Op.REPLACE);
        canvas.concat(clipTransform.renderToViewTransform);
    }

    /**
     * Set the area of what is being rendered.  Transform and clipping regions will be setup to properly map the given area onto a portion of the view.
     *
     * @param visibleRenderArea
     */
    public void setRenderView(RectF visibleRenderArea)
    {
        this.visibleRenderArea = visibleRenderArea;
        clipTransform = AndGuiUtils.calcRenderClipTransform(getWidth(), getHeight(), this.visibleRenderArea);

    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight)
    {
        clipTransform = AndGuiUtils.calcRenderClipTransform(getWidth(), getHeight(), visibleRenderArea);
    }
}
