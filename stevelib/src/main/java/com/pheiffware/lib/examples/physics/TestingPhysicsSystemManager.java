/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
 */
package com.pheiffware.lib.examples.physics;

import java.util.List;
import java.util.Random;

import com.pheiffware.lib.utils.Utils;
import com.pheiffware.lib.utils.log.PLog;
import com.pheiffware.lib.physics.PhysicsSystem;
import com.pheiffware.lib.physics.entity.Entity;
import com.pheiffware.lib.simulation.DeterministicSimulationRunner;
import com.pheiffware.lib.simulation.SimulationRunner;

/**
 * Designed to run the simulator in a background thread in a precise and
 * consistent way. Allows multiple test scenarios to be setup and run.
 */
public class TestingPhysicsSystemManager
{
	// Randomizes the order in which entities appear in the PhysicSystem list
	// for each scenario
	private final boolean randomizeEntityOrder;

	// The physics scenarios to run through
	private final TestPhysicsScenario[] physicsScenarios;

	// Use this ratio to delay updates to the simulation so that it flows with
	// this ratio to real time
	private final double maxSimTimePerSecond;

	private final PhysicsSystem physicsSystem;
	private SimulationRunner<List<Entity>> simulationRunner = null;

	public TestingPhysicsSystemManager(double maxSimTimePerSecond, boolean randomizeEntityOrder, TestPhysicsScenario[] physicsScenarios)
	{
		this.randomizeEntityOrder = randomizeEntityOrder;
		this.physicsScenarios = physicsScenarios;
		this.maxSimTimePerSecond = maxSimTimePerSecond;
		physicsSystem = new PhysicsSystem();
	}

	/**
	 * Starts the simulation.
	 */
	public void start() {
		new Thread() {
			public void run() {
				for (int i = 0; i < physicsScenarios.length; i++) {
					physicsSystem.reset();
					physicsScenarios[i].setup(physicsSystem);
					if (randomizeEntityOrder) {
						physicsSystem.randomizeEntityProcessingOrder_TESTING_ONLY(new Random());
					}
					changeScenario(physicsScenarios[i]);
					simulationRunner.awaitCompletion();
					PLog.info("Ups : " + physicsScenarios[i].getNumSteps() / Utils.getTimeElapsed(simulationRunner.getRealStartTime()));
				}
			}
		}.start();

	}

	private synchronized void changeScenario(TestPhysicsScenario testPhysicsScenario) {
		double timeStep = testPhysicsScenario.getRuntime() / testPhysicsScenario.getNumSteps();
		simulationRunner = new DeterministicSimulationRunner<List<Entity>>(physicsSystem, maxSimTimePerSecond, timeStep,
				testPhysicsScenario.getNumSteps());
		simulationRunner.start();
	}

	public synchronized List<Entity> getState() {
		// Waits until the 1st simulation starts
		while (simulationRunner == null)
			;
		return simulationRunner.getState();
	}

	public void endCurrentScenario() {
		// Waits until the 1st simulation starts
		while (simulationRunner == null)
			;
		simulationRunner.stop();
	}

	public void applyExternalInput(String key, Object value) {
		// Waits until the 1st simulation starts
		while (simulationRunner == null)
			;
		simulationRunner.applyExternalInput(key, value);
	}
}
