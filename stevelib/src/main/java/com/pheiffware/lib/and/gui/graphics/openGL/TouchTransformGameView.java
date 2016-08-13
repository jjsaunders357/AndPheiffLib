/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.and.gui.graphics.openGL;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.pheiffware.lib.and.input.TouchAnalyzer;
import com.pheiffware.lib.examples.andGraphics.Example3DRenderer;
import com.pheiffware.lib.graphics.FilterQuality;

/**
 * A GameView which forwards touch transform events to the renderer
 */
public class TouchTransformGameView extends BaseGameView
{
    private final boolean forwardTouchTransformEvents;
    private final TouchAnalyzer touchAnalyzer;
    private final Example3DRenderer renderer;

    public TouchTransformGameView(Context context, Example3DRenderer renderer, FilterQuality filterQuality, boolean forwardRotationSensorEvents, boolean forwardTouchTransformEvents)
    {
        super(context, renderer, filterQuality, forwardRotationSensorEvents);
        this.renderer = renderer;
        this.forwardTouchTransformEvents = forwardTouchTransformEvents;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        touchAnalyzer = new TouchAnalyzer(metrics.xdpi, metrics.ydpi);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return forwardTouchTransformEvent(event);
    }


    /**
     * Forwards a touch event to the renderer as a touchTransformEvent.
     *
     * @param event
     * @return
     */
    protected boolean forwardTouchTransformEvent(final MotionEvent event)
    {
        if (isSurfaceInitialized() && forwardTouchTransformEvents)
        {
            //Must process event in gui thread as the event object itself is modified (its not safe to pass to another thread).
            final TouchAnalyzer.TouchTransformEvent touchTransformEvent = touchAnalyzer.convertRawTouchEvent(event);

            if (touchTransformEvent != null)
            {
                queueEvent(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        renderer.touchTransformEvent(touchTransformEvent.numPointers, touchTransformEvent.transform);
                    }
                });
            }
            return true;
        }
        return false;
    }


}
