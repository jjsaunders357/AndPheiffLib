package com.pheiffware.lib.and.touch;

import android.view.MotionEvent;

import com.pheiffware.lib.geometry.Transform2D;
import com.pheiffware.lib.geometry.Vec2D;
import com.pheiffware.lib.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Takes in Android multi-touch events and processes them into Transform2 events.
 * Created by Steve on 3/9/2016.
 */
public class TouchAnalyzer
{
    //The registered listener
    private final TouchTransformListener listener;

    //Map from pointer ID to position
    private final Map<Integer, Vec2D> pointerPositions = new HashMap<>();

    //Map from pointer ID to angle relative to center
    private final Map<Integer, Double> pointerAngles = new HashMap<>();

    //Averaged center of all pointers on screen
    private final Vec2D center = new Vec2D(0, 0);

    //Averaged radius of circle formed by all pointers on screen squared
    private double averageRadiusSquared;
    private final double minimumTranslationSquared;
    private final double minimumRotation;
    private final double minimumLogScale;


    /**
     * @param minimumTranslation     Any translation with a magnitude smaller than this, will be ignored
     * @param minimumRotationDegrees Any rotation less than this (in degrees) will be ignored
     * @param minimumScale           The smallest fraction increase/decrease to not ignore.  For example 0.02 mean ignore any change in scale smaller than 2%.
     * @param listener
     */
    public TouchAnalyzer(double minimumTranslation, double minimumRotationDegrees, double minimumScale, TouchTransformListener listener)
    {
        this.minimumTranslationSquared = minimumTranslation * minimumTranslation;
        this.minimumRotation = Math.PI * minimumRotationDegrees / 180.0;
        this.minimumLogScale = Math.abs(Math.log(1 + minimumScale));
        this.listener = listener;
    }

    public void interpretRawEvent(MotionEvent event)
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
                for (int pointerID : pointerPositions.keySet())
                {
                    index = event.findPointerIndex(pointerID);
                    Vec2D position = pointerPositions.get(pointerID);
                    position.x = event.getX(index);
                    position.y = event.getY(index);
                }
                Transform2D transform2D = updateStateAndGetTransform();
                fireEvent(transform2D);
                break;
        }
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
     * Updates the averaged "center" of all pointer positions and an average radius squared.
     */
    private void updateRadiusAndAngles()
    {
        averageRadiusSquared = 0;
        if (pointerPositions.size() > 1)
        {
            for (int id : pointerPositions.keySet())
            {
                Vec2D position = pointerPositions.get(id);
                //Vector from center to pointer position
                Vec2D centerDiff = Vec2D.sub(position, center);
                double magnitudeSquared = centerDiff.magnitudeSquared();
                averageRadiusSquared += magnitudeSquared;
                double angle = centerDiff.getAngle();
                pointerAngles.put(id, angle);
            }
            averageRadiusSquared /= pointerPositions.size();
        }
    }


    /**
     * Updates the pointer's center, angles and average radius squared.  Returns composite transform.
     */
    private Transform2D updateStateAndGetTransform()
    {
        double oldX = center.x;
        double oldY = center.y;
        updateCenter();

        double oldAverageRadiusSquared = averageRadiusSquared;
        averageRadiusSquared = 0.0;
        double weightedRotation = 0.0;
        if (pointerPositions.size() > 1)
        {
            for (int id : pointerPositions.keySet())
            {
                Vec2D position = pointerPositions.get(id);
                //Vector from center to pointer position
                Vec2D centerDiff = Vec2D.sub(position, center);
                double magnitudeSquared = centerDiff.magnitudeSquared();
                averageRadiusSquared += magnitudeSquared;
                double angle = centerDiff.getAngle();
                double oldAngle = pointerAngles.put(id, angle);
                //Weight of rotation determined by distance of point from center
                weightedRotation += MathUtils.angleDiff(angle, oldAngle) * magnitudeSquared;
            }
            averageRadiusSquared /= pointerPositions.size();
            weightedRotation /= averageRadiusSquared;
        }

        double uniformScale;
        //In theory if 2 pointers could be at the same location then don't calculate scale
        if (averageRadiusSquared != 0 && oldAverageRadiusSquared != 0)
        {
            uniformScale = Math.sqrt(averageRadiusSquared / oldAverageRadiusSquared);
        }
        else
        {
            uniformScale = 1;
        }
        Vec2D translation = new Vec2D(center.x - oldX, center.y - oldY);
        //The difference between the current state and the last update
        return new Transform2D(translation, weightedRotation, new Vec2D(uniformScale, uniformScale));
    }

    private void fireEvent(Transform2D transform2D)
    {
        if (listener != null)
        {
            listener.touchTransformEvent(transform2D);
        }
    }

    /**
     * If any aspect of the given transform (translation, rotation, scale) is less than the minimum allowed, it is adjusted to 0 (1 for scale).
     *
     * @param transform2D The transform to adjust based on minimum sensitivity
     * @return Does the transform have any change worth reporting?  If false, then it won't be reported as an event.
     */
    private boolean adjustTransformForMinimumSensitivity(Transform2D transform2D)
    {
        boolean isSensitiveEnough = false;
        if (transform2D.translation.magnitudeSquared() < minimumTranslationSquared)
        {
            transform2D.translation.x = 0;
            transform2D.translation.y = 0;
        }
        else
        {
            isSensitiveEnough = true;
        }
        if (Math.abs(transform2D.rotation) < minimumRotation)
        {
            transform2D.rotation = 0;
        }
        else
        {
            isSensitiveEnough = true;
        }
        if (Math.abs(Math.log(transform2D.scale.x)) < minimumLogScale)
        {
            transform2D.scale.x = 1;
            transform2D.scale.y = 1;
        }
        else
        {
            isSensitiveEnough = true;
        }
        return isSensitiveEnough;
    }
}
