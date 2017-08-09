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

    //Averaged radius of circle formed by all pointers on screen squared
    private double averageRadiusSquared;

    /**
     * Reports touch transform events in terms of dp.  1 dp = 1 pixel on a 160 DPI screen.
     *
     * @param xDPI touchTransform translation events specified in units of dp
     * @param yDPI touchTransform translation events specified in units of dp
     */
    public TouchAnalyzer(double xDPI, double yDPI)
    {
        this.xDPI = xDPI;
        this.yDPI = yDPI;
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
     * Converts a raw touch event into a TouchTransformEvent or returns null if this does not result in a transformation (adding/removing pointers does not cause a
     * transformation).
     *
     * @param event
     * @return a TouchTransformEvent or null if this event did not cause a transformation.
     */
    public TouchTransformEvent convertRawTouchEvent(MotionEvent event)
    {
        int index;
        int id;
        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                index = event.getActionIndex();
                id = event.getPointerId(index);
                addPointer(id, event.getX(index), event.getY(index));
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                index = event.getActionIndex();
                id = event.getPointerId(index);
                removePointer(id);
                break;
            case MotionEvent.ACTION_MOVE:
                Transform2D transform2D = updateStateAndGetTransform(event);
                scaleTransformToDP(transform2D);
                return new TouchTransformEvent(pointerPositions.size(), transform2D);
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
     *
     * @param id
     */
    private void addPointer(int id, float x, float y)
    {
        Vec2D position = new Vec2D(x, y);
        pointerPositions.put(id, position);
        updateCenter();
        updateRadiusAndAngles();
    }


    /**
     * Stops tracking the given pointer and updates center position based on remaining pointers.
     *
     * @param id
     */
    private void removePointer(int id)
    {
        pointerPositions.remove(id);
        pointerAngles.remove(id);
        updateCenter();
        updateRadiusAndAngles();
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
