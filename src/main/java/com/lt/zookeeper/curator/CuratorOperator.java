package com.lt.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Created by taoshiliu on 2018/7/6.
 */
public class CuratorOperator {

    public CuratorFramework client = null;
    public static final String zkServerPath = "localhost:2181";

    public CuratorOperator() {
        //1000间隔时间、5重试次数
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,5);
        //3重试次数、5000重试间隔
        //RetryPolicy retryPolicy = new RetryNTimes(3,5000);
        //重试1次，3000重试间隔
        //RetryPolicy retryPolicy = new RetryOneTime(3000);
        //一直重试
        //RetryPolicy retryPolicy = new RetryForever();
        //2000重试间隔，3000最大重试时间
        //RetryPolicy retryPolicy = new RetryUntilElapsed(2000,3000);

        client = CuratorFrameworkFactory.builder()
                .connectString(zkServerPath)
                .sessionTimeoutMs(10000).retryPolicy(retryPolicy)
                .namespace("workspace").build();
        client.start();
    }

    public void closeZKClient() {
        if(client != null) {
            this.client.close();
        }
    }

    public static void main(String[] args) throws Exception {
        CuratorOperator cto = new CuratorOperator();
        boolean isZKCuratorStarted = cto.client.isStarted();
        System.out.println("Status is " + isZKCuratorStarted);
        Thread.currentThread().sleep(3000);

        cto.closeZKClient();
        boolean isZKCuratorStarted1 = cto.client.isStarted();
        System.out.println("Status is " + isZKCuratorStarted1);

        //创建节点
        final String nodePath = "/super/imooc";
        byte[] data = "superme".getBytes();
        cto.client.create().creatingParentContainersIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(nodePath,data);

        //更新节点
        byte[] newData = "batman".getBytes();
        cto.client.setData().withVersion(2)
                .forPath(nodePath,newData);

        //删除节点
        cto.client.delete()
                .guaranteed()
                .deletingChildrenIfNeeded()
                .withVersion(2)
                .forPath(nodePath);

        //读取节点数据
        Stat stat = new Stat();
        byte[] statData = cto.client.getData().storingStatIn(stat).forPath(nodePath);
        System.out.println("Node =" + nodePath + " ; data =" + new String(statData));
        System.out.println("Version =" + stat.getVersion());

        //查询子节点
        List<String> childNodes = cto.client.getChildren().forPath(nodePath);
        System.out.println("begin print child");
        for(String s : childNodes) {
            System.out.println(s);
        }

        //判断是否存在
        Stat statExist = cto.client.checkExists().forPath(nodePath);
        System.out.println(statExist);

        //watcher事件,只会触发一次
        cto.client.getData().usingWatcher(new MyCuratorWathcer()).forPath(nodePath);
        cto.client.getData().usingWatcher(new MyWatcher()).forPath(nodePath);

        //注册watcher事件，多次触发
        final NodeCache nodeCache = new NodeCache(cto.client,nodePath);
        nodeCache.start(true);

        if(nodeCache.getCurrentData() != null) {
            System.out.println("init" + new String(nodeCache.getCurrentData().getData()));
        }else {
            System.out.println("init empty");
        }

        nodeCache.getListenable().addListener(new NodeCacheListener() {
            public void nodeChanged() throws Exception {
                String data = new String(nodeCache.getCurrentData().getData());
                System.out.println("path =" + nodeCache.getCurrentData().getPath() + " ; data =" + data);
            }
        });

        //子节点watcher
        String childNodePathCache = nodePath;
        final PathChildrenCache childrenCache = new PathChildrenCache(cto.client,childNodePathCache,true);

        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        List<ChildData> childDataList = childrenCache.getCurrentData();

        System.out.println("son data");

        for(ChildData cd : childDataList) {
            String childData = new String(cd.getData());
            System.out.println(childData);
        }

        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                if(pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)) {
                    System.out.println("init");
                }else if(pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                    System.out.println("add :" + pathChildrenCacheEvent.getData().getPath());
                    System.out.println("add data :" + new String(pathChildrenCacheEvent.getData().getData()));
                }else if(pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
                    System.out.println("delete :" + pathChildrenCacheEvent.getData().getPath());
                }else if(pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
                    System.out.println("modify :" + pathChildrenCacheEvent.getData().getPath());
                    System.out.println("modify data :" + pathChildrenCacheEvent.getData().getData());
                }
            }
        });
    }
}
