/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.view;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;

import com.pheiffware.andpheifflib.sphere.engine.physics.PhysicsSystemManager;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.Entity;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody.LineSegmentEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody.PolygonEntity;
import com.pheiffware.andpheifflib.sphere.engine.physics.entity.rigidBody.SphereEntity;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.LineSegment;
import com.pheiffware.andpheifflib.sphere.engine.vec3f.Vec3F;

/**
 *
 */
public class TestPhysicsView extends View
{

	private PhysicsSystemManager physicsSystemManager;
	private Paint fillPaint;
	private Paint outlinePaint;

	/**
	 * @param context
	 */
	public TestPhysicsView(Context context, PhysicsSystemManager physicsSystemManager)
	{
		super(context);
		fillPaint = new Paint();
		fillPaint.setStyle(Style.FILL);
		fillPaint.setColor(Color.rgb(255, 0, 0));
		outlinePaint = new Paint();
		outlinePaint.setStyle(Style.STROKE);
		outlinePaint.setColor(Color.rgb(255, 0, 0));
		setMinimumWidth(800);
		setMinimumHeight(800);
		this.physicsSystemManager = physicsSystemManager;
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				postInvalidate();
			}
		}, 0, 16);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(Color.BLACK);
		List<Entity> entities = physicsSystemManager.copyForRender();
		for (Entity entity : entities)
		{
			draw(canvas, entity);
		}
	}

	/**
	 * Draw an entity, based on its type
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
		Vec3F circleCenter = circle.center;
		RectF rectF = new RectF(circleCenter.x - circle.getRadius(), circleCenter.y - circle.getRadius(), circleCenter.x + circle.getRadius(),
				circleCenter.y + circle.getRadius());
		canvas.drawOval(rectF, fillPaint);
	}

	private void draw(Canvas canvas, LineSegment lineSegment)
	{
		Vec3F unitNormal = lineSegment.getUnitNormal();
		canvas.drawLine(lineSegment.p1.x, lineSegment.p1.y, lineSegment.p2.x, lineSegment.p2.y, fillPaint);
		Vec3F center = Vec3F.scale(Vec3F.add(lineSegment.p1, lineSegment.p2), 0.5f);
		Vec3F normalEndPoint = Vec3F.add(center, Vec3F.scale(unitNormal, 10.0f));
		canvas.drawLine(center.x, center.y, normalEndPoint.x, normalEndPoint.y, fillPaint);
	}

	private void draw(Canvas canvas, PolygonEntity polygon)
	{
		for (LineSegment lineSegment : polygon.getLineSegments())
		{
			draw(canvas, lineSegment);
		}

		float x = polygon.getBoundingSphere().getCenter().x;
		float y = polygon.getBoundingSphere().getCenter().y;
		float radius = polygon.getBoundingSphere().getRadius();
		RectF rectF = new RectF(x - radius, y - radius, x + radius, y + radius);
		canvas.drawOval(rectF, outlinePaint);
	}

	@Override
	@ExportedProperty(category = "drawing")
	public boolean isOpaque()
	{
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
	}

}
