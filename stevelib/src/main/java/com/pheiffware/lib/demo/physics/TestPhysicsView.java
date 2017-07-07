/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.demo.physics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

import com.pheiffware.lib.and.gui.graphics.RenderView;
import com.pheiffware.lib.geometry.Vec3D;
import com.pheiffware.lib.geometry.shapes.LineSegment;
import com.pheiffware.lib.physics.entity.Entity;
import com.pheiffware.lib.physics.entity.physicalEntity.physicalEntities.LineSegmentEntity;
import com.pheiffware.lib.physics.entity.physicalEntity.physicalEntities.PolygonEntity;
import com.pheiffware.lib.physics.entity.physicalEntity.physicalEntities.SphereEntity;
import com.pheiffware.lib.simulation.SimulationRunner;

import java.util.List;

/**
 * A view which can display the given physics simulation runner whose state contains a list of entities.
 */
public class TestPhysicsView extends RenderView
{
    private static final Paint fillPaint = new Paint();
    private static final Paint outlinePaint = new Paint();

    {
        fillPaint.setStyle(Style.FILL);
        fillPaint.setColor(Color.rgb(255, 0, 0));
        outlinePaint.setStyle(Style.STROKE);
        outlinePaint.setColor(Color.rgb(255, 0, 0));
    }

    private SimulationRunner<List<Entity>> simulationRunner;

    public TestPhysicsView(Context context, int renderPeriodMS, RectF visibleRenderArea, SimulationRunner<List<Entity>> simulationRunner)
    {
        super(context, renderPeriodMS, visibleRenderArea);
        this.simulationRunner = simulationRunner;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
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
}
