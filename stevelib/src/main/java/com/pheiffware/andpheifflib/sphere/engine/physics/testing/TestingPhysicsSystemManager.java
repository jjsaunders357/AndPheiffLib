/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.testing;

import java.util.Random;

import android.util.Log;

import com.pheiffware.andpheifflib.sphere.engine.physics.PhysicsSystem;
import com.pheiffware.andpheifflib.sphere.engine.physics.PhysicsSystemManager;

/**
 * Designed to run the simulator in a background thread in a precise and consistent way. Allows multiple test scenarios to be setup and run.
 */
public class TestingPhysicsSystemManager extends PhysicsSystemManager
{
	// The fixed time step used on every update. Guarantees consistency.
	private final float fixedTimeStep;

	// Randomizes the order in which entities appear in the PhysicSystem list for each scenario
	private final boolean randomizeEntityOrder;

	// The physics scenarios to run through
	private final PhysicsScenario[] physicsScenarios;

	// The index of the current scenario being run
	private int scenarioIndex = -1;

	// Use this ratio to delay updates to the simulation so that it flows with this ratio to real time
	private final float realTimeToSimTimeRatio;

	// The number of time steps run for this scenario
	private int scenarioNumTimeSteps;

	// The total time spent performing update calculations during this scenario
	private float scenarioCalcTimens = 0.0f;

	// When the current scenario started
	private long scenarioStartedTimeStamp;

	// Used to randomly reorder physical entities
	private Random random;

	public TestingPhysicsSystemManager(float fixedTimeStep, float realTimeToSimTimeRatio, boolean randomizeEntityOrder,
			PhysicsScenario[] physicsScenarios)
	{
		super();
		this.fixedTimeStep = fixedTimeStep;
		this.randomizeEntityOrder = randomizeEntityOrder;
		this.physicsScenarios = physicsScenarios;
		this.realTimeToSimTimeRatio = realTimeToSimTimeRatio;
		random = new Random();
	}

	public TestingPhysicsSystemManager(float fixedTimeStep, float realTimeToSimTimeRatio, boolean randomizeEntityOrder,
			PhysicsScenario[] physicsScenarios, long seed)
	{
		super();
		this.fixedTimeStep = fixedTimeStep;
		this.randomizeEntityOrder = randomizeEntityOrder;
		this.physicsScenarios = physicsScenarios;
		this.realTimeToSimTimeRatio = realTimeToSimTimeRatio;
		random = new Random(seed);
	}

	@Override
	public void start()
	{
		scenarioStartedTimeStamp = System.nanoTime();
		super.start();
	}

	/* (non-Javadoc)
	 * @see physics.managers.PhysicsSystemManager#updateImplement(physics.PhysicsSystem)
	 */
	@Override
	protected void updateImplement(PhysicsSystem physicsSystem)
	{
		if (scenarioIndex == -1 || physicsSystem.getTotalRunTime() >= physicsScenarios[scenarioIndex].getScenarioRuntime())
		{
			scenarioIndex++;
			if (scenarioIndex == physicsScenarios.length)
			{
				stopNonBlocking();
				return;
			}
			physicsSystem.reset();
			physicsScenarios[scenarioIndex].setup(physicsSystem);
			if (randomizeEntityOrder)
			{
				physicsSystem.randomizeEntityProcessingOrder_TESTING_ONLY(random);
			}
			scenarioNumTimeSteps = 0;
			scenarioCalcTimens = 0;
			scenarioStartedTimeStamp = System.nanoTime();
		}

		long start = System.nanoTime();
		physicsSystem.update(fixedTimeStep);
		scenarioCalcTimens += System.nanoTime() - start;
		scenarioNumTimeSteps++;

		// Stacked with drop
		// ~100 ups
		// ~180 ups
		// ~300 ups
		// ~330 - 350 ups
		// ~450 ups
		// ~680 ups
		// ~640 ups
		// ~680 ups
		// ******
		// ConstrainedStackedObjects(20.0f, 40.5f, 500.5f, 20, 7, 800, 0.9f)
		// TestingPhysicsSystemManager uses seed of 12345
		// Settles to about
		// ~660 ups
		// ~980 ups
		// ~1050 ups
		// ~1004 ups
		if ((scenarioNumTimeSteps & 255) == 0)
		{
			Log.i("Speed", "Ups : " + scenarioNumTimeSteps / (scenarioCalcTimens / 1000000000.0));
		}

		float realTimeElapsed;
		float simTimeElapsed;
		do
		{
			realTimeElapsed = (System.nanoTime() - scenarioStartedTimeStamp) / 10000000000.0f;
			simTimeElapsed = fixedTimeStep * scenarioNumTimeSteps;
		} while (realTimeElapsed / simTimeElapsed < realTimeToSimTimeRatio);

	}
}
