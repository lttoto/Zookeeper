package com.lt.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by taoshiliu on 2018/7/6.
 */
public class ZkNodeExist implements Watcher{

    private ZooKeeper zooKeeper = null;

    public static final String zkServerPath = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    public static final Integer timeout = 5000;

    public ZkNodeExist() {}

    public ZkNodeExist(String connectString) {
        try {
            zooKeeper = new ZooKeeper(connectString,timeout,new ZkNodeExist());
        } catch (IOException e) {
            e.printStackTrace();
            if(zooKeeper != null) {
                try {
                    zooKeeper.close();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws KeeperException, InterruptedException {
        ZkNodeExist zkServer = new ZkNodeExist(zkServerPath);

        Stat stat = zkServer.getZooKeeper().exists("/test",true);
        if(stat != null) {
            System.out.println("查询的节点版本为dataVersion: " + stat.getVersion());
        }else {
            System.out.println("该节点不存在。。。");
        }

        countDownLatch.await();
    }


    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType() == Event.EventType.NodeCreated) {
            System.out.println("NodeCreated");
            countDownLatch.countDown();
        }
        if(watchedEvent.getType() == Event.EventType.NodeDataChanged) {
            System.out.println("NodeDataChanged");
            countDownLatch.countDown();
        }
        if(watchedEvent.getType() == Event.EventType.NodeDeleted) {
            System.out.println("NodeDeleted");
            countDownLatch.countDown();
        }
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public static CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public static void setCountDownLatch(CountDownLatch countDownLatch) {
        ZkNodeExist.countDownLatch = countDownLatch;
    }
}
