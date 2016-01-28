/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics;

import com.pheiffware.andpheifflib.sphere.engine.physics.testing.PhysicsScenario;

/**
 * Updates the physics system based on the passage of real time.
 */
public class RealtimePhysicsSystemManager extends PhysicsSystemManager
{
	// Prevents a time-step from ever being longer than this. If something causes the thread to not be able to update for a while (OS stealing
	// resources) this prevents wild jumps in state.
	private final float maxTimeStep;

	// The minimum time allowed time-step (again in real computer time, not simulated time).
	private final float minTimeStep;

	// Converts real passage of time to simulation time-steps. In units of (simTimeUnit/second). Each time step will be
	// (real time passed since last update) * simulationRate.
	private final float simulationRate;

	// The last time the system was updated.
	private long lastUpdateTimeStamp;

	public RealtimePhysicsSystemManager(float maxTimeStep, float minTimeStep, float simulationRate, PhysicsScenario initialState)
	{
		super();
		this.maxTimeStep = maxTimeStep;
		this.minTimeStep = minTimeStep;
		this.simulationRate = simulationRate;
		initialState.setup(getPhysicsSystem());
	}

	@Override
	public void start()
	{
		lastUpdateTimeStamp = System.nanoTime();
		super.start();
	}

	/* (non-Javadoc)
	 * @see physics.managers.PhysicsSystemManager#updateImplement(physics.PhysicsSystem)
	 */
	@Override
	protected void updateImplement(PhysicsSystem physicsSystem)
	{
		float timeStep = elapsedTimeSinceLastUpdate() * simulationRate;

		if (timeStep > maxTimeStep)
		{
			timeStep = maxTimeStep;
		}
		else if (timeStep < minTimeStep)
		{
			timeStep = minTimeStep;
		}
		physicsSystem.update(timeStep);
	}

	/**
	 * Calculates the elapsed time since last update.
	 * 
	 * @return
	 */
	private float elapsedTimeSinceLastUpdate()
	{
		long currentTimeStamp = System.nanoTime();
		float elapsedTime = (currentTimeStamp - lastUpdateTimeStamp) / 1000000000.0f;
		lastUpdateTimeStamp = currentTimeStamp;
		return elapsedTime;
	}

}
