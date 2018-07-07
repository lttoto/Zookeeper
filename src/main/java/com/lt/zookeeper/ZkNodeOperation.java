package com.lt.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * Created by taoshiliu on 2018/7/6.
 */
public class ZkNodeOperation implements Watcher{

    private ZooKeeper zooKeeper = null;

    public static final String zkServerPath = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    public static final Integer timeout = 5000;

    public ZkNodeOperation() {}

    public ZkNodeOperation(String connectString) {
        try {
            zooKeeper = new ZooKeeper(connectString,timeout,new ZkNodeOperation());
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

    public void creatZkNode(String path, byte[] data, List<ACL> acls) {

        String result = "";

        try {
            //同步创建
            result = zooKeeper.create(path,data,acls, CreateMode.EPHEMERAL);

            String ctx = "{'create','success'}";
            //异步创建
            zooKeeper.create(path,data,acls,CreateMode.PERSISTENT,new CreateCallBack(),ctx);

            System.out.println("创建节点：\t" + result + "\t成功。。。");
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws KeeperException, InterruptedException {
        ZkNodeOperation zkServer = new ZkNodeOperation(zkServerPath);
        //创建节点
        zkServer.creatZkNode("/testnode","testnode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE);
        //修改节点
        Stat stat = zkServer.getZooKeeper().setData("/testnode","modify".getBytes(),1);
        System.out.println(stat.getVersion());
        //删除节点
        zkServer.getZooKeeper().delete("/testnode",0);

        //异步删除节点
        String ctx = "{'delete','success'}";
        zkServer.getZooKeeper().delete("/testnode",0,new DeleteCallBack(),ctx);

    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void process(WatchedEvent watchedEvent) {

    }
}
