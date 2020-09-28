package com.awesomeJdk.practise.disruptor;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

public class LongEventFactory implements EventFactory<LongEvent>
{
    @Override
    public LongEvent newInstance()
    {
        return new LongEvent();
    }
}
