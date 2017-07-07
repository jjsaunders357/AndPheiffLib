package com.pheiffware.lib.simulation;

import com.pheiffware.lib.utils.Utils;

/**
 * Manages a simulation by running it in a background thread.  This deals with the threading/synchronization issues related to this. How the simulation is actually run is done by
 * the runSimulation() method calling this class' timeStep() and handleSignals() methods as appropriate.
 * <p/>
 * This is not thread safe and should not be addressed by multiple threads without synchronization.  Its handling of the simulation itself, internally, is thread safe.  Operations
 * such as timeStep will always be called sequentially in one thread.
 *
 * @param <SimState> The semantic of state returned from the simulation.
 * @author Steve
 */
public abstract class SimulationRunner<SimState> implements Runnable
{
    //A lock object for synchronizing against the simulation thread
    private Object simThreadLock = new Object();

    //This is set to force the simulation thread to give an opening for other synchronized blocks
    private volatile boolean signalFlag = false;

    //When set to true the next call to handleSignals will throw a SimStoppedException
    private volatile boolean stopFlag;

    //The thread were simulation code is run
    private Thread simulationThread;

    //When the last start() was issued
    private volatile long startTimeStamp;

    //Total elapsed simulation time
    private double elapsedSimTimeSinceStart;

    //The simulation object itself
    private final Simulation<SimState> simulation;

    public SimulationRunner(Simulation<SimState> simulation)
    {
        this.simulation = simulation;
        simulationThread = null;
    }

    /**
     * An extending class overrides this to perform one or more time steps in the desired manner in a background thread. This method should call timeStep() to update the time step.
     * This method should periodically call handleSignals().  This method allows other threads to get the simulation state or issue a stop.  handleSignals() with throw a
     * SimStoppedException if/when a stop is requested.  Since the simulation can be restarted again, catching this exception is an opportunity to save state.  It is expected that
     * this exception will be rethrown.
     * <p/>
     * runSimulation can be called multiple times if stop() is called and then start is called again later.  If the simulation ends naturally, it still may be called again if the
     * start method is called again.  In this case it should silently fall through.
     *
     * @throws SimStoppedException
     */
    protected abstract void runSimulation() throws SimStoppedException;

    /**
     * Updates one time step of given size.
     *
     * @param timeStep
     */
    protected final void performTimeStep(double timeStep)
    {
        elapsedSimTimeSinceStart += timeStep;
        simulation.performTimeStep(timeStep);
    }

    /**
     * Enforce delay if simulation is running too fast.
     *
     * @throws SimStoppedException thrown if a stop signal comes in.
     */
    protected final void throttleAndHandleSignals(double maxSimTimePerSecond) throws SimStoppedException
    {
        // Don't throttle at all if this is inf.
        if (maxSimTimePerSecond == Double.POSITIVE_INFINITY)
        {
            handleSignals();
            return;
        }

        do
        {
            handleSignals();
        } while (elapsedSimTimeSinceStart / getElapsedRealTimeSinceStart() > maxSimTimePerSecond);
    }

    /**
     * Should be called periodically while performing the simulation to allow outside threads to interact with the simulation. For example: getState()
     *
     * @throws SimStoppedException Will be thrown if a request to stop was made.
     */
    protected final void handleSignals() throws SimStoppedException
    {
        if (signalFlag == true)
        {
            try
            {
                // Gives up simThreadLock. The requester is expected to call notify()
                // explicitly when done.
                simThreadLock.wait();
            }
            catch (InterruptedException e)
            {
            }
            if (stopFlag)
            {
                throw new SimStoppedException();
            }
        }
    }

    /**
     * Wrapper for the runSimulation method which extending classes fill in.
     */
    @Override
    public void run()
    {
        startTimeStamp = System.nanoTime();
        elapsedSimTimeSinceStart = 0.0;
        try
        {
            synchronized (simThreadLock)
            {
                runSimulation();
            }
        }
        catch (SimStoppedException e)
        {
            // Exits thread.
        }
    }

    /**
     * Gets a snapshot of the simulation.
     *
     * @return
     */
    public final SimState getState()
    {
        SimState state;
        signalFlag = true;
        synchronized (simThreadLock)
        {
            signalFlag = false;
            state = simulation.copyState();
            simThreadLock.notify();
        }
        return state;
    }

    /**
     * Applies external input to the simulation.
     *
     * @param key
     * @param value
     */
    public void applyExternalInput(String key, Object value)
    {
        signalFlag = true;
        synchronized (simThreadLock)
        {
            signalFlag = false;
            simulation.applyExternalInput(key, value);
            simThreadLock.notify();
        }
    }

    /**
     * Causes the simulation to stop in an orderly manner.
     */
    public final void start()
    {
        if (!isRunning())
        {
            simulationThread = new Thread(this);
        }
        stopFlag = false;
        simulationThread.start();
    }

    /**
     * Wait for the simulation to end naturally.
     */
    public void awaitCompletion()
    {
        try
        {
            if (simulationThread != null)
            {
                simulationThread.join();
            }
        }
        catch (InterruptedException e)
        {
        }
    }

    /**
     * Causes the simulation to stop in an orderly manner.
     */
    public final void stop()
    {
        signalFlag = true;
        synchronized (simThreadLock)
        {
            signalFlag = false;
            stopFlag = true;
            simThreadLock.notify();
        }
    }

    /**
     * Stops the simulation and blocks until it ends.
     */
    public final void stopAndWait()
    {
        stop();
        awaitCompletion();
    }

    /**
     * Is the simulation running?
     *
     * @return
     */
    public boolean isRunning()
    {

        return simulationThread != null && simulationThread.isAlive();
    }

    /**
     * Runs the specified code, in the simulation thread, during the next break in execution (call to handleSignals).
     *
     * @param runnable
     */
    public final void runCriticalSection(Runnable runnable)
    {
        signalFlag = true;
        synchronized (simThreadLock)
        {
            signalFlag = false;
            runnable.run();
            simThreadLock.notify();
        }
    }

    protected double getElapsedRealTimeSinceStart()
    {
        return Utils.getTimeElapsed(startTimeStamp);
    }

}
