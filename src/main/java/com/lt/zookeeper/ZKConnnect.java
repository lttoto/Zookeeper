package com.lt.zookeeper;



import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by taoshiliu on 2018/7/6.
 */
public class ZKConnnect implements Watcher{

    final static Logger log = LoggerFactory.getLogger(ZKConnnect.class);

    //public static final String zkServerPath = "127.0.0.1:2181";
    public static final String zkServerPath = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    public static final Integer timeout = 5000;

    public static void main(String[] args) throws IOException, InterruptedException {
        ZooKeeper zk = new ZooKeeper(zkServerPath,timeout,new ZKConnnect());

        System.out.println("客户端开始连接zookeeper服务器。。。");
        System.out.println("连接状态:{} " + zk.getState());

        Thread.currentThread().sleep(2000);
        //new Thread().sleep(2000);

        System.out.println("连接状态:{} " + zk.getState());
    }

    public void process(WatchedEvent watchedEvent) {
        System.out.println("接收到watch通知:{} " + watchedEvent);
    }
}
