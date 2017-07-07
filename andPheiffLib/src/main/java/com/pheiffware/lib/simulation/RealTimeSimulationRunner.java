package com.pheiffware.lib.simulation;


import com.pheiffware.lib.utils.Utils;

/**
 * Runs the simulation in real time at the given rate.  A minimum and maximum sim time step can also be specified.
 *
 * @param <SimState>
 */
public class RealTimeSimulationRunner<SimState> extends SimulationRunner<SimState>
{
    private final double maxSimTimeStep;
    private final double minSimTimeStep;
    private final double simTimePerSecond;

    /**
     * @param simulation
     * @param maxSimTimePerSecond if the simulation is running faster than this, then wait as necessary.
     * @param maxSimTimeStep      do not ever make a time step larger than this, even if the simulation is behind schedule
     * @param minSimTimeStep      do not ever make a time step smaller than this, even if the simulation is running fast enough
     */
    public RealTimeSimulationRunner(Simulation<SimState> simulation, double maxSimTimePerSecond, double maxSimTimeStep, double minSimTimeStep)
    {
        super(simulation);
        this.maxSimTimeStep = maxSimTimeStep;
        this.minSimTimeStep = minSimTimeStep;
        this.simTimePerSecond = maxSimTimePerSecond;
    }

    protected void runSimulation() throws SimStoppedException
    {
        long lastTimeStamp = System.nanoTime();

        while (true)
        {
            long nextTimeStamp = System.nanoTime();
            double timeStep = simTimePerSecond * Utils.getTimeElapsed(lastTimeStamp, nextTimeStamp);
            lastTimeStamp = nextTimeStamp;

            if (timeStep > maxSimTimeStep)
            {
                timeStep = maxSimTimeStep;
            }
            else if (timeStep < minSimTimeStep)
            {
                timeStep = minSimTimeStep;
            }
            performTimeStep(timeStep);
            throttleAndHandleSignals(simTimePerSecond);
        }

    }
}
