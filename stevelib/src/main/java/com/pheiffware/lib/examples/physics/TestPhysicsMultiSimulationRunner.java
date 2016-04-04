package com.pheiffware.lib.examples.physics;

import com.pheiffware.lib.physics.PhysicsSystem;
import com.pheiffware.lib.physics.entity.Entity;
import com.pheiffware.lib.simulation.SimStoppedException;
import com.pheiffware.lib.simulation.SimulationRunner;

import java.util.List;
import java.util.Random;

/**
 * Runs multiple physics scenarios, one after another.  This implements the SimulationRunner interface and can be started/stopped like any other SimulationRunner.
 * <p/>
 * Created by Steve on 4/1/2016.
 */
public class TestPhysicsMultiSimulationRunner extends SimulationRunner<List<Entity>>
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

    //The current physics scenario being run
    private int physicsScenarioIndex = 0;

    //The step of the current physics scenario being run
    private int scenarioStep;

    //A status indicator which can be queried
    private volatile double updatesPerSecond;

    public TestPhysicsMultiSimulationRunner(double maxSimTimePerSecond, boolean randomizeEntityOrder, TestPhysicsScenario[] physicsScenarios)
    {
        this(maxSimTimePerSecond, randomizeEntityOrder, physicsScenarios, new PhysicsSystem());
    }

    //Used so physicsSystem can be captured in field
    private TestPhysicsMultiSimulationRunner(double maxSimTimePerSecond, boolean randomizeEntityOrder, TestPhysicsScenario[] physicsScenarios, PhysicsSystem physicsSystem)
    {
        super(physicsSystem);
        this.randomizeEntityOrder = randomizeEntityOrder;
        this.physicsScenarios = physicsScenarios;
        this.maxSimTimePerSecond = maxSimTimePerSecond;
        this.physicsSystem = physicsSystem;
        if (physicsScenarios.length > 0)
        {
            changeScenario(physicsScenarios[0]);
        }
    }

    @Override
    protected void runSimulation() throws SimStoppedException
    {

        while (physicsScenarioIndex < physicsScenarios.length)
        {
            runScenario(physicsScenarios[physicsScenarioIndex]);
            physicsScenarioIndex++;
            if (physicsScenarioIndex < physicsScenarios.length)
            {
                changeScenario(physicsScenarios[physicsScenarioIndex]);
            }
//            PLog.info("Ups : " + physicsScenarios[physicsScenarioIndex].getNumSteps() / Utils.getTimeElapsed(simulationRunner.getRealStartTime()));
        }
    }

    private void runScenario(TestPhysicsScenario testPhysicsScenario) throws SimStoppedException
    {
        while (scenarioStep < testPhysicsScenario.getNumSteps())
        {
            performTimeStep(testPhysicsScenario.getTimeStepDuration());
            throttleAndHandleSignals(maxSimTimePerSecond);
            scenarioStep++;
        }
    }

    private void changeScenario(TestPhysicsScenario testPhysicsScenario)
    {
        physicsSystem.reset();
        physicsScenarios[physicsScenarioIndex].setup(physicsSystem);
        if (randomizeEntityOrder)
        {
            physicsSystem.randomizeEntityProcessingOrder_TESTING_ONLY(new Random());
        }
        scenarioStep = 0;
    }
}
