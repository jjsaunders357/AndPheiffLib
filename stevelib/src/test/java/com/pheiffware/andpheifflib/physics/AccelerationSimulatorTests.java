
package com.pheiffware.andpheifflib.physics;


import com.pheiffware.andpheifflib.physics.AccelerationSimulator;


import org.junit.Test;

import static org.junit.Assert.*;

public class AccelerationSimulatorTests {
    @Test
    public void basicMove() {
        AccelerationSimulator moveSim = new AccelerationSimulator(3, 53, 10, 5, 2);
        assertTrue("Wrong end position", moveSim.getEndPosition() == 53);
        assertTrue("Wrong acceleration distance", moveSim.getAccelerationDistance() == 10.0);
        assertTrue("Wrong velocity distance", moveSim.getMaxVelocityDistance() == 15.0);
        assertTrue("Wrong deceleration distance", moveSim.getDecelerationDistance() == 25.0);
        assertTrue("Wrong position", moveSim.getPositionAtTime(-1) == 3);
        assertTrue("Wrong position", moveSim.getPositionAtTime(0) == 3);
        assertTrue("Wrong position", moveSim.getPositionAtTime(1) == 5.5);
        assertTrue("Wrong position", moveSim.getPositionAtTime(3) == 23.0);
        assertTrue("Wrong position", moveSim.getPositionAtTime(5.5) == 44.0);
        assertTrue("Wrong position", moveSim.getPositionAtTime(8.5) == 53.0);
        assertTrue("Wrong position", moveSim.getPositionAtTime(10) == 53.0);
    }

    @Test
    public void negativeMove() {
        AccelerationSimulator moveSim = new AccelerationSimulator(53, 3, 10, 5, 2);
        assertTrue("Wrong end position", moveSim.getEndPosition() == 3);
        assertTrue("Wrong acceleration distance", moveSim.getAccelerationDistance() == 10.0);
        assertTrue("Wrong velocity distance", moveSim.getMaxVelocityDistance() == 15.0);
        assertTrue("Wrong deceleration distance", moveSim.getDecelerationDistance() == 25.0);
        assertTrue("Wrong position", moveSim.getPositionAtTime(-1) == 53);
        assertTrue("Wrong position", moveSim.getPositionAtTime(0) == 53);
        assertTrue("Wrong position", moveSim.getPositionAtTime(1) == 50.5);
        assertTrue("Wrong position", moveSim.getPositionAtTime(3) == 33.0);
        assertTrue("Wrong position", moveSim.getPositionAtTime(5.5) == 12.0);
        assertTrue("Wrong position", moveSim.getPositionAtTime(8.5) == 3.0);
        assertTrue("Wrong position", moveSim.getPositionAtTime(10) == 3.0);
    }

    /**
     * Simulated hardware can't reach maximum velocity because distance is too short<dl>
     * <dt>Limitations:</dt>
     * <dd>None</dd>
     * <dt>Postconditions:</dt>
     * <dd>None
     * <p/>
     * </dd>
     * </dl>
     */
    @Test
    public void spikeMove() {
        AccelerationSimulator moveSim = new AccelerationSimulator(2, 37, 20, 5, 2);
        assertTrue("Wrong end position", moveSim.getEndPosition() == 37);

        assertTrue("Wrong acceleration time", moveSim.getAccelerationTime() == 2.0);
        assertTrue("Wrong acceleration distance", moveSim.getAccelerationDistance() == 10.0);
        assertTrue("Wrong velocity distance", moveSim.getMaxVelocityTime() == 0.0);
        assertTrue("Wrong velocity distance", moveSim.getMaxVelocityDistance() == 0.0);
        assertTrue("Wrong deceleration distance", moveSim.getDecelerationTime() == 5.0);
        assertTrue("Wrong deceleration distance", moveSim.getDecelerationDistance() == 25.0);
        assertTrue("Wrong position", moveSim.getPositionAtTime(-1) == 2);
        assertTrue("Wrong position", moveSim.getPositionAtTime(0) == 2);
        assertTrue("Wrong position", moveSim.getPositionAtTime(1) == 4.5);
        assertTrue("Wrong position", moveSim.getPositionAtTime(3) == 21.0);
        assertTrue("Wrong position", moveSim.getPositionAtTime(7) == 37.0);
        assertTrue("Wrong position", moveSim.getPositionAtTime(8) == 37.0);
    }

    /**
     * Simulated hardware has infinite acceleration and deceleration<dl>
     * <dt>Limitations:</dt>
     * <dd>None</dd>
     * <dt>Postconditions:</dt>
     * <dd>None
     * <p/>
     * </dd>
     * </dl>
     */
    @Test
    public void noAccMove() {
        AccelerationSimulator moveSim = new AccelerationSimulator(5, 15, 5, Double.POSITIVE_INFINITY);
        assertTrue("Wrong end position", moveSim.getEndPosition() == 15);

        assertTrue("Wrong acceleration time", moveSim.getAccelerationTime() == 0.0);
        assertTrue("Wrong acceleration distance", moveSim.getAccelerationDistance() == 0.0);
        assertTrue("Wrong velocity distance", moveSim.getMaxVelocityTime() == 2.0);
        assertTrue("Wrong velocity distance", moveSim.getMaxVelocityDistance() == 10.0);
        assertTrue("Wrong deceleration distance", moveSim.getDecelerationTime() == 0.0);
        assertTrue("Wrong deceleration distance", moveSim.getDecelerationDistance() == 0.0);
        assertTrue("Wrong position", moveSim.getPositionAtTime(-1) == 5);
        assertTrue("Wrong position", moveSim.getPositionAtTime(0) == 5);
        assertTrue("Wrong position", moveSim.getPositionAtTime(1) == 10.0);
        assertTrue("Wrong position", moveSim.getPositionAtTime(2) == 15.0);
        assertTrue("Wrong position", moveSim.getPositionAtTime(3) == 15.0);
    }
}
