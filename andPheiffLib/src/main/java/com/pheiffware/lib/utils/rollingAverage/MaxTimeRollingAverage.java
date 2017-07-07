package com.pheiffware.lib.utils.rollingAverage;

import java.util.LinkedList;

/**
 * Maintains a rolling average of a value, keeping values which span a maximum time period.
 * <p/>
 * Created by Steve on 7/25/2016.
 */
public class MaxTimeRollingAverage
{
    //Maximum number of milliseconds between the oldest sample and the newest sample to maintain
    private long maxTimeLapseNano;

    protected final LinkedList<Sample> samples = new LinkedList<>();

    public MaxTimeRollingAverage(double maxTimeLapseSeconds)
    {
        this.maxTimeLapseNano = (long) (maxTimeLapseSeconds * 1000000000);
    }

    public void addValue(double value, long timeStamp)
    {
        samples.addFirst(new Sample(value, timeStamp));
        while (samples.size() > 1)
        {
            Sample sample = samples.getLast();
            long diff = timeStamp - sample.timeStamp;
            if (diff > maxTimeLapseNano)
            {
                samples.removeLast();
            }
            else
            {
                return;
            }
        }
    }

    public double getAverage()
    {
        double total = 0;
        for (Sample sample : samples)
        {
            total += sample.value;
        }
        return total / samples.size();
    }

    private static class Sample
    {
        final double value;
        final long timeStamp;

        public Sample(double value, long timeStamp)
        {
            this.value = value;
            this.timeStamp = timeStamp;
        }
    }
}
