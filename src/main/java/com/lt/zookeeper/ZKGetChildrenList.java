package com.lt.zookeeper;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by taoshiliu on 2018/7/6.
 */
public class ZKGetChildrenList implements Watcher {

    private ZooKeeper zooKeeper = null;

    public static final String zkServerPath = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    public static final Integer timeout = 5000;

    public ZKGetChildrenList() {}

    public ZKGetChildrenList(String connectString) {
        try {
            zooKeeper = new ZooKeeper(connectString,timeout,new ZKGetChildrenList());
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

        ZKGetChildrenList zkServer = new ZKGetChildrenList(zkServerPath);

        List<String> strChildList = zkServer.getZooKeeper().getChildren("/test",true);
        for(String s : strChildList) {
            System.out.println(s);
        }

        //异步调用
        String ctx = "{'callback':'ChildrenCallback'}";
        zkServer.getZooKeeper().getChildren("/test", true, new Children2Callback(),ctx);
        zkServer.getZooKeeper().getChildren("/test", true, new ChildrenCallback(),ctx);

        countDownLatch.await();
    }

    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
            System.out.println("NodeChildrenChanged");
            ZKGetChildrenList zkServer = new ZKGetChildrenList(zkServerPath);
            try {
                List<String> strChildList = zkServer.getZooKeeper().getChildren(watchedEvent.getPath(),false);
                for(String s : strChildList) {
                    System.out.println(s);
                }
                countDownLatch.countDown();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(watchedEvent.getType() == Event.EventType.NodeCreated) {
            System.out.println("NodeCreated");
        }
        if(watchedEvent.getType() == Event.EventType.NodeDataChanged) {
            System.out.println("NodeDataChanged");
        }
        if(watchedEvent.getType() == Event.EventType.NodeDeleted) {
            System.out.println("NodeDeleted");
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
        ZKGetChildrenList.countDownLatch = countDownLatch;
    }
}
