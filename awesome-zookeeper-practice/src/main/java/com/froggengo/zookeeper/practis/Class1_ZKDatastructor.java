package com.froggengo.zookeeper.practis;


import com.froggengo.zookeeper.election.ZooKeeperService;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class Class1_ZKDatastructor {
    /**
     * 创建临时节点，执行结束临时节点消失
     * @throws IOException
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void test () throws IOException, KeeperException, InterruptedException {
        String url="localhost:2181";
        String path="/ephemeral";
        ZooKeeper zk = new ZooKeeper(url,500,event->{
            System.out.println("1---"+event.getPath()+"=="+event.getType());
            //2---/ephemeral==NodeDataChanged
        });
        zk.create(path,"hello world".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
        byte[] data = zk.getData(path, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("2---"+event.getPath()+"=="+event.getType());
            }
        }, null);
        System.out.println(new String(data));
        //使用zkcli查看是否有临时,并修改节点数据，观察watcher打印结果
        TimeUnit.SECONDS.sleep(60);
        //如果没有正常close导致，临时节点一直存在
        zk.close();
    }

    /**
     * 创建顺序节点，只需要提供路径以及顺序节点的前缀，自动会补0以及序号
     * @throws Exception
     */
    @Test
    public void testSequence() throws Exception {
        String url="localhost:2181";
        String path="/ephemeral";
        ZooKeeper zk = new ZooKeeper(url,500,event->{
            System.out.println("1---"+event.getPath()+"=="+event.getType());
            //2---/ephemeral==NodeDataChanged
        });
        zk.create(path,"hello world".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        zk.create(path+"/p_","children1".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        zk.create(path+"/p_","children1".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        List<String> children = zk.getChildren(path, false);
        children.forEach(System.out::println);
    }

}
