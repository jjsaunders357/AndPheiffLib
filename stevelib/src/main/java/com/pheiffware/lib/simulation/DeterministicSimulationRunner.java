package com.pheiffware.lib.simulation;

/**
 * Runs a simulation with an exact time step on each frame for an exact number of steps.
 *
 * @param <SimState>
 */
public class DeterministicSimulationRunner<SimState> extends SimulationRunner<SimState>
{
    //The maximum amount of time which is allowed to pass in the simulation per second in real time.
    private final double maxSimTimePerSecond;

    //The number of time steps to carry out
    private final int numSteps;

    //The size of each time timeStepNumber
    private final double timeStep;

    //The number of the current time timeStepNumber
    private int timeStepNumber = 0;

    public DeterministicSimulationRunner(Simulation<SimState> simulation, double maxSimTimePerSecond, double timeStepDuration, int numSteps)
    {
        super(simulation);
        this.maxSimTimePerSecond = maxSimTimePerSecond;
        this.timeStep = timeStepDuration;
        this.numSteps = numSteps;
    }

    protected void runSimulation() throws SimStoppedException
    {
        while (timeStepNumber < numSteps)
        {
            performTimeStep(timeStep);
            throttleAndHandleSignals(maxSimTimePerSecond);
            timeStepNumber++;
        }
    }
}
