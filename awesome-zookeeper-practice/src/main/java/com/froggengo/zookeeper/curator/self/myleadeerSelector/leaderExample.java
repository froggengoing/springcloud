package com.froggengo.zookeeper.curator.self.myleadeerSelector;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 启动zookee，同时启动三台leaderExample
 * 最早一台会使leader，关闭leader，过了一会将会第二台称为leader
 */
public class leaderExample {
    public static void main(String[] args) throws IOException {
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181",5000,6000,
                new RetryNTimes(5,5000));
        LeaderSelector leaderSelector = new LeaderSelector(client, "/example/leader", new LeaderSelectorListener() {
            @Override
            public void takeLeadership(CuratorFramework client) throws Exception {
                // we are now the leader. This method should not return until we want to relinquish leadership
                double random = Math.random();
                final int waitSeconds = (int)(1000 * random) + 1;
                System.out.println("设备"+ random + " is now the leader. Waiting " + waitSeconds + " seconds...");
                //System.out.println("设备"+ random + " has been leader " + leaderCount.getAndIncrement() + " time(s) before.");
                try
                {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds));
                }
                catch ( InterruptedException e )
                {
                    System.err.println("设备"+ random + " was interrupted.");
                    Thread.currentThread().interrupt();
                }
                finally
                {
                    System.out.println("设备"+ random + " relinquishing(放弃) leadership.\n");
                }
            }

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {

            }
        });
        client.start();
        leaderSelector.start();
        new BufferedReader(new InputStreamReader(System.in)).readLine();
    }
}
