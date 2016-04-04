/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.examples.physics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Region;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;

import com.pheiffware.lib.and.AndGuiUtils;
import com.pheiffware.lib.geometry.Vec3D;
import com.pheiffware.lib.geometry.shapes.LineSegment;
import com.pheiffware.lib.physics.entity.Entity;
import com.pheiffware.lib.physics.entity.physicalEntity.physicalEntities.LineSegmentEntity;
import com.pheiffware.lib.physics.entity.physicalEntity.physicalEntities.PolygonEntity;
import com.pheiffware.lib.physics.entity.physicalEntity.physicalEntities.SphereEntity;
import com.pheiffware.lib.simulation.SimulationRunner;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//TODO: parent base class with transform/clipping code

/**
 * TODO: Comment
 */
public class TestPhysicsView extends View
{
    private static final Paint fillPaint = new Paint();
    private static final Paint outlinePaint = new Paint();

    {
        fillPaint.setStyle(Style.FILL);
        fillPaint.setColor(Color.rgb(255, 0, 0));
        outlinePaint.setStyle(Style.STROKE);
        outlinePaint.setColor(Color.rgb(255, 0, 0));
    }

    private Timer timer = null;

    //The area of the sim world to view (lower left and dimensions)
    private float simX;
    private float simY;
    private float simWidth;
    private float simHeight;

    private RectF clipRectangle = new RectF(0, 0, 0, 0);
    private Matrix simToViewTransform = new Matrix();

    private SimulationRunner<List<Entity>> simulationRunner;

    /**
     * @param context
     * @param simulationRunner
     */
    public TestPhysicsView(Context context, SimulationRunner<List<Entity>> simulationRunner, float simX, float simY, float simWidth, float simHeight)
    {
        super(context);
        setSimView(simX, simY, simWidth, simHeight);
        this.simulationRunner = simulationRunner;
    }

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
            }, 0, 16);
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
        canvas.clipRect(clipRectangle, Region.Op.REPLACE);
        canvas.drawColor(Color.BLACK);
        canvas.concat(simToViewTransform);
        List<Entity> entities = simulationRunner.getState();
        for (Entity entity : entities)
        {
            draw(canvas, entity);
        }
    }

    /**
     * Draw an entity, based on its semantic
     *
     * @param canvas
     * @param entity
     */
    private final void draw(Canvas canvas, Entity entity)
    {
        if (entity instanceof SphereEntity)
        {
            draw(canvas, (SphereEntity) entity);
        }
        else if (entity instanceof LineSegmentEntity)
        {

            draw(canvas, ((LineSegmentEntity) entity).getLineSegment());
        }
        else if (entity instanceof PolygonEntity)
        {
            draw(canvas, (PolygonEntity) entity);
        }
    }

    /**
     * Draws a circle
     *
     * @param canvas
     * @param circle
     */
    private void draw(Canvas canvas, SphereEntity circle)
    {
        Vec3D circleCenter = circle.getCenter();

        RectF rectF = new RectF(
                (float) (circleCenter.x - circle.getRadius()), (float) (circleCenter.y - circle.getRadius()),
                (float) (circleCenter.x + circle.getRadius()), (float) (circleCenter.y + circle.getRadius())
        );

        canvas.drawOval(rectF, fillPaint);
    }

    private void draw(Canvas canvas, LineSegment lineSegment)
    {
        Vec3D unitNormal = lineSegment.getUnitNormal();
        canvas.drawLine((float) lineSegment.p1.x, (float) lineSegment.p1.y, (float) lineSegment.p2.x, (float) lineSegment.p2.y, fillPaint);
        Vec3D center = Vec3D.scale(Vec3D.add(lineSegment.p1, lineSegment.p2), 0.5f);
        Vec3D normalEndPoint = Vec3D.add(center, Vec3D.scale(unitNormal, 10.0f));
        canvas.drawLine((float) center.x, (float) center.y, (float) normalEndPoint.x, (float) normalEndPoint.y, fillPaint);
    }

    private void draw(Canvas canvas, PolygonEntity polygon)
    {
        for (LineSegment lineSegment : polygon.getLineSegments())
        {
            draw(canvas, lineSegment);
        }
    }

    @Override
    @ExportedProperty(category = "drawing")
    public boolean isOpaque()
    {
        return true;
    }


    private void setClipRegionAndViewTransform()
    {
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        simToViewTransform = new Matrix();
        if (viewWidth == 0 || viewHeight == 0 || simWidth == 0 || simHeight == 0)
        {
            clipRectangle = new RectF(0, 0, 0, 0);

        }
        clipRectangle = AndGuiUtils.getRenderViewRectangle(viewWidth, viewHeight, simWidth / simHeight);

        //1st: Translate sim so that (simX,simY) is at (0,0)
        simToViewTransform.preTranslate(-simX, -simY);

        //2nd: Scale sim to match viewing area (clipRectangle)
        float xScale = clipRectangle.width() / simWidth;
        float yScale = clipRectangle.height() / simHeight;
        simToViewTransform.postScale(xScale, yScale);

        //3rd: Translate viewing area to the clipped region
        simToViewTransform.postTranslate(clipRectangle.left, clipRectangle.top);
    }

    public void setSimView(float simX, float simY, float simWidth, float simHeight)
    {
        this.simX = simX;
        this.simY = simY;
        this.simWidth = simWidth;
        this.simHeight = simHeight;
        setClipRegionAndViewTransform();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        setClipRegionAndViewTransform();
    }
}
