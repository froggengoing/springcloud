package com.froggengo.zookeeper.curator.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simulates some external resource that can only be access by one process at a time
 */
public class FakeLimitedResource
{
    private final AtomicBoolean inUse = new AtomicBoolean(false);

    public void     use() throws InterruptedException
    {
        // in a real application this would be accessing/manipulating a shared resource

        if ( !inUse.compareAndSet(false, true) )
        {
            throw new IllegalStateException("Needs to be used by one client at a time");
        }

        try
        {
            System.out.println("i am doing something");
            Thread.sleep((long)(3000 * Math.random()));
        }
        finally
        {
            inUse.set(false);
        }
    }
}
