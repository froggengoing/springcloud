package com.froggengo.zookeeper;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.xml.internal.ws.Closeable;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class zookeeperMain implements  Runnable{

    ZooKeeper zooKeeper;

    public zookeeperMain(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    /**
     * 主线程启动，获取"/zk_test"的数据，并设置监听器后台运行。
     * 当"/zk_test"数据发送改变时候，执行MyWatcher#process(org.apache.zookeeper.WatchedEvent)
     * 注意：
     *      1.必须阻塞主线程,英文watch监听器的线程为守护线程
     *      2.必须重新设置watch，因为watch是一次性用品，这里可能存在重新设置watch之间，遗漏了事件
     * @param args
     * @throws IOException
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        String connectionString="localhost:2181";
        int sessionTimeout=3000;
        ZooKeeper zk = new ZooKeeper(connectionString,sessionTimeout,null);
        MyWatcher myWatcher = new MyWatcher(zk);
        byte[] data = zk.getData("/zk_test", myWatcher, null);
        System.out.println(new String(data));

        //阻塞主线程
        System.out.println(Thread.currentThread().getName());
        new zookeeperMain(zk).run();
/*        Runnable runnable = () -> {
            try {
                InputStream in;
                while ((in = System.in) != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String msg = reader.readLine();
                    String[] split = msg.split(",");
                    if (split.length < 2) {
                        System.out.println("请输入正确的格式！");
                        continue;
                    }
                    switch (split[0]) {
                        case "getData":
                            byte[] data = zk.getData(split[1], new MyWatcher(), null);
                            System.out.println("getData: " + new String(data, "utf-8"));
                            break;
                        case "getChildren":
                            List<String> children = zk.getChildren(split[1], true);
                            System.out.print("getChildren: ");
                            children.stream().forEach(n -> System.out.print(n + ","));
                            System.out.println("");
                            break;
                        case "setData":
                            zk.setData(split[1], split[2].getBytes(), -1);
                            data = zk.getData(split[1], new MyWatcher(), null);
                            System.out.println("set and getData: " + new String(data, "utf-8"));
                            break;
                        case "getACL":
                            System.out.print("getACL: ");
                            List<ACL> acl = zk.getACL(split[1], null);
                            acl.forEach(n -> {
                                System.out.print(n.getId() + ",");
                            });
                            break;
                        default:
                            System.out.println("未知命令" + split[0]);
                    }
                }
            }catch (Exception e){
                System.out.println("异常："+e);
            }
        };
        new Thread(runnable).start();*/

    }

    @Override
    public void run() {
        try {
            synchronized (this) {
                    wait();
            }
        } catch (InterruptedException e) {
        }
    }
}
class MyWatcher implements Watcher{
    ZooKeeper zooKeeper;
    public MyWatcher(ZooKeeper zooKeeper) {
        this.zooKeeper=zooKeeper;
    }

    @Override
    public void process(WatchedEvent event) {
        String path = event.getPath();
        System.out.println("path: "+ path);
        System.out.println("state: "+event.getState());
        System.out.println("type: "+event.getType());
        System.out.println("wrapper: "+event.getWrapper());
        System.out.println("tostring: "+event.toString());
        try {
            System.out.println("是否为守护线程："+Thread.currentThread().isDaemon());
            System.out.println("监听器："+Thread.currentThread().getName());
            //必须重新设置监听器。
            byte[] data = zooKeeper.getData(path, new MyWatcher(zooKeeper), null);
            System.out.println(new String(data));
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
