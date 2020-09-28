package com.awesomeJdk.practise.disruptor;

import com.lmax.disruptor.EventFactory;

public class LongEvent
{
    private long value;

    public void set(long value)
    {
        this.value = value;
    }
}
