/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.physics.testing;

import com.pheiffware.andpheifflib.sphere.engine.physics.PhysicsSystem;

/**
 * Describes a physics scenario and how long it should run. Used for testing.
 */
public abstract class PhysicsScenario
{
	private final float scenarioRuntime;

	public PhysicsScenario(float scenarioRuntime)
	{
		this.scenarioRuntime = scenarioRuntime;
	}

	void resetPhysicsSystem(PhysicsSystem physicsSystem)
	{
		physicsSystem.reset();
		setup(physicsSystem);
	}

	public abstract void setup(PhysicsSystem physicsSystem);

	public float getScenarioRuntime()
	{
		return scenarioRuntime;
	}
}
