package com.pheiffware.lib.and.input;

import android.view.MotionEvent;

import com.pheiffware.lib.geometry.Transform2D;
import com.pheiffware.lib.geometry.Vec2D;
import com.pheiffware.lib.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Takes in Android multi-touch events and processes them into commonly wanted events:
 * 2D transform: a measure of translation, rotation and scale which occurred and how many pointers, were present, when transform occurred.
 * <p>
 * <p/>
 * Created by Steve on 3/9/2016.
 */
public class TouchAnalyzer
{
    //Map from pointer ID to position
    private final Map<Integer, Vec2D> pointerPositions = new HashMap<>();

    //Map from pointer ID to angle relative to center
    private final Map<Integer, Double> pointerAngles = new HashMap<>();

    //Averaged center of all pointers on screen
    private final Vec2D center = new Vec2D(0, 0);

    //Screen DPI used for conversion from pixels
    private final double xDPI;
    private final double yDPI;

    //The maximum time which can elapse during pointers being added/removed and for it to still be considered a tap event.
    private final double maxTapTime;

    //Averaged radius of circle formed by all pointers on screen squared
    private double averageRadiusSquared;

    //When the 1st pointer was put down
    private long startTapTime;

    //The maximum number of pointers seen during the tap event
    private int maxTapPointersEncountered;

    /**
     * Reports touch transform events in terms of dp.  1 dp = 1 pixel on a 160 DPI screen.
     *  @param xDPI touchTransform translation events specified in units of dp
     * @param yDPI touchTransform translation events specified in units of dp
     * @param maxTapTime
     */
    public TouchAnalyzer(double xDPI, double yDPI, double maxTapTime)
    {
        this.xDPI = xDPI;
        this.yDPI = yDPI;
        this.maxTapTime = maxTapTime;
    }

    /**
     * Describes a transform caused by pointers on the screen or a tap event caused by rapidly tapping the screen with N pointers.
     */
    public static class TouchEvent
    {
        //If this was a transform event, this will contain the transform, otherwise it will be null.
        public TouchTransformEvent touchTransformEvent;

        //If this was a transform event, this will contain the tap information, otherwise it will be null.
        public TouchTapEvent touchTapEvent;

        public TouchEvent(TouchTransformEvent touchTransformEvent, TouchTapEvent touchTapEvent)
        {
            this.touchTransformEvent = touchTransformEvent;
            this.touchTapEvent = touchTapEvent;
        }
    }

    /**
     * Describes the event of N pointers rapidly (faster than maxTapTime) tapping the screen.
     */
    public static class TouchTapEvent
    {
        //Number of pointers involved in the tap
        public final int numPointers;

        //How fast the tap occurred in seconds
        public final double tapTime;

        public TouchTapEvent(int numPointers, double tapTime)
        {
            this.numPointers = numPointers;
            this.tapTime = tapTime;
        }
    }

    /**
     * Describes the motion of pointers in terms of rotation, translation and scaling.  Also reports the number of pointers which were touching to produce the transform.  All
     * translations are expressed in terms of units of dp.  1 dp = 1 pixel on a 160 DPI screen.
     */
    public static class TouchTransformEvent
    {
        //Number of pointers involved in the transform
        public final int numPointers;
        public final Transform2D transform;

        public TouchTransformEvent(int numPointers, Transform2D transform)
        {
            this.numPointers = numPointers;
            this.transform = transform;
        }
    }

    /**
     * Converts a raw touch event into a TouchEvent or returns null if this does not result in a transformation or tap event.
     *
     * @param event
     * @return a TouchEvent or null if this event did not cause a transformation or tap event.
     */
    public TouchEvent convertRawTouchEvent(MotionEvent event)
    {
        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                addPointer(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                TouchEvent touchEvent = removePointer(event);
                if (touchEvent != null)
                {
                    return touchEvent;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Transform2D transform2D = updateStateAndGetTransform(event);
                scaleTransformToDP(transform2D);
                return new TouchEvent(new TouchTransformEvent(pointerPositions.size(), transform2D), null);
        }
        return null;
    }


    /**
     * Scales translation to be in units of dp.  1 dp == 1 pixel on a 160 DPI screen.
     *
     * @param transform2D
     */
    private void scaleTransformToDP(Transform2D transform2D)
    {
        transform2D.translation.x = 160 * transform2D.translation.x / xDPI;
        transform2D.translation.y = 160 * transform2D.translation.y / yDPI;
    }

    /**
     * Starts tracking the given pointer and updates center position based on all pointers.
     * If this is the 1st point, its time stamp is recorded for tap detection purposes.
     *
     * @param event
     */
    private void addPointer(MotionEvent event)
    {
        int index = event.getActionIndex();
        int id = event.getPointerId(index);
        Vec2D position = new Vec2D(event.getX(index), event.getY(index));
        pointerPositions.put(id, position);
        updateCenter();
        updateRadiusAndAngles();
        int numPointers = pointerPositions.size();
        if (numPointers == 1)
        {
            startTapTime = event.getEventTime();
            maxTapPointersEncountered = 1;
        }
        else
        {
            if (numPointers > maxTapPointersEncountered)
            {
                maxTapPointersEncountered = numPointers;
            }
        }
    }

    /**
     * Stops tracking the given pointer and updates center position based on remaining pointers.
     *
     * @param event
     */
    private TouchEvent removePointer(MotionEvent event)
    {
        int index = event.getActionIndex();
        int id = event.getPointerId(index);
        pointerPositions.remove(id);
        pointerAngles.remove(id);
        updateCenter();
        updateRadiusAndAngles();
        if (pointerPositions.size() == 0)
        {
            double tapTime = (event.getEventTime() - startTapTime) / 1000.0;
            if (tapTime < maxTapTime)
            {
                return new TouchEvent(null, new TouchTapEvent(maxTapPointersEncountered, tapTime));
            }
        }
        return null;
    }

    /**
     * Updates the positions of all pointer based on the state of the event
     *
     * @param event
     */
    private void updatePointerPositions(MotionEvent event)
    {
        for (int i = 0; i < event.getPointerCount(); i++)
        {
            int pointerID = event.getPointerId(i);
            Vec2D position = pointerPositions.get(pointerID);
            position.x = event.getX(i);
            position.y = event.getY(i);
        }
    }

    /**
     * Updates the averaged "center" of all pointer positions and an average radius squared.
     */
    private void updateCenter()
    {
        center.x = 0;
        center.y = 0;
        if (pointerPositions.size() > 0)
        {
            for (Vec2D point : pointerPositions.values())
            {
                center.addTo(point);
            }
            center.scaleBy(1.0 / pointerPositions.size());
        }
    }

    /**
     * Updates the angle of all pointers around the center.  Returns the weighted change in angle of all pointers.  Requires updated pointerPositions and center.
     */
    private double updateRadiusAndAngles()
    {
        averageRadiusSquared = 0;
        double weightedRotation = 0.0;
        if (pointerPositions.size() > 1)
        {
            for (int id : pointerPositions.keySet())
            {
                Vec2D position = pointerPositions.get(id);
                //Vector from center to pointer position
                double centerDiffX = position.x - center.x;
                double centerDiffY = position.y - center.y;
                double magnitudeSquared = centerDiffX * centerDiffX + centerDiffY * centerDiffY;
                averageRadiusSquared += magnitudeSquared;
                double angle = Math.atan2(centerDiffY, centerDiffX);
                Double oldAngle = pointerAngles.put(id, angle);
                if (oldAngle != null)
                {
                    //Weight of rotation determined by distance of point from center
                    weightedRotation += MathUtils.angleDiff(angle, oldAngle) * magnitudeSquared;
                }
            }
            averageRadiusSquared /= pointerPositions.size();
            weightedRotation /= averageRadiusSquared;
        }
        return weightedRotation;
    }


    /**
     * Updates the pointer's center, angles and average radius squared.  Returns composite transform.
     *
     * @param event
     */
    private Transform2D updateStateAndGetTransform(MotionEvent event)
    {
        //Calculate how the center moved (translation)
        double prevX = center.x;
        double prevY = center.y;
        updatePointerPositions(event);
        updateCenter();
        Vec2D translation = new Vec2D(center.x - prevX, center.y - prevY);

        //Remember current radius squared
        double prevAverageRadiusSquared = averageRadiusSquared;

        //Update radius squared, angles and calc weighted angular change
        double weightedAngularChange = updateRadiusAndAngles();

        Vec2D scaleVector = calculateScaleVector(prevAverageRadiusSquared);

        //The difference between the current state and the last update
        return new Transform2D(translation, weightedAngularChange, scaleVector);
    }

    /**
     * Calculate the change in scale given the previous average radius squared and current radius squared.  Returns uniform scale vector (x and y always scale by the same
     * magnitude).
     *
     * @param prevAverageRadiusSquared
     * @return
     */
    private Vec2D calculateScaleVector(double prevAverageRadiusSquared)
    {

        double uniformScale;
        //In theory if 2 pointers could be at the same location then don't calculate scale
        if (averageRadiusSquared != 0 && prevAverageRadiusSquared != 0)
        {
            uniformScale = Math.sqrt(averageRadiusSquared / prevAverageRadiusSquared);
        }
        else
        {
            uniformScale = 1;
        }
        return new Vec2D(uniformScale, uniformScale);
    }
}
