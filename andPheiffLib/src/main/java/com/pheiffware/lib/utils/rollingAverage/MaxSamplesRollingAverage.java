package com.pheiffware.lib.utils.rollingAverage;

import java.util.LinkedList;

/**
 * Maintains a rolling average of a value, by keeping up to max values.
 * <p/>
 * Created by Steve on 7/25/2016.
 */
public class MaxSamplesRollingAverage
{
    //How many historical values to include in average
    private int maxValues;

    protected final LinkedList<Double> values = new LinkedList<>();

    public MaxSamplesRollingAverage(int maxValues)
    {
        this.maxValues = maxValues;
    }

    public void addValue(Double value)
    {
        values.addFirst(value);
        if (values.size() > maxValues)
        {
            values.removeLast();
        }
    }

    public double getAverage()
    {
        double total = 0;
        for (Double value : values)
        {
            total += value;
        }
        return total / values.size();
    }
}
