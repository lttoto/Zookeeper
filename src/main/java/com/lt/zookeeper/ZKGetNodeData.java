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
public class ZKGetNodeData implements Watcher {

    private ZooKeeper zooKeeper = null;

    public static final String zkServerPath = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    public static final Integer timeout = 5000;
    private static Stat stat = new Stat();

    public ZKGetNodeData() {
    }

    public ZKGetNodeData(String connectString) {
        try {
            zooKeeper = new ZooKeeper(connectString,timeout,new ZKGetNodeData());
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
        ZKGetNodeData zkServer = new ZKGetNodeData(zkServerPath);

        byte[] resByte = zkServer.getZooKeeper().getData("/test",true,stat);
        String result = new String(resByte);
        System.out.println("当前值：" + result);
        countDownLatch.await();
    }

    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType() == Event.EventType.NodeDataChanged) {
            ZKGetNodeData zkServer = new ZKGetNodeData(zkServerPath);
            try {
                byte[] resByte = zkServer.getZooKeeper().getData("/test",false,stat);
                String result = new String(resByte);
                System.out.println("变更后的值：" + result);
                System.out.println("版本号变化dversion: " + stat.getVersion());
                countDownLatch.countDown();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {

        }
        if(watchedEvent.getType() == Event.EventType.NodeCreated) {

        }
        if(watchedEvent.getType() == Event.EventType.NodeDeleted) {

        }

    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public static Stat getStat() {
        return stat;
    }

    public static void setStat(Stat stat) {
        ZKGetNodeData.stat = stat;
    }

    public static CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public static void setCountDownLatch(CountDownLatch countDownLatch) {
        ZKGetNodeData.countDownLatch = countDownLatch;
    }
}
