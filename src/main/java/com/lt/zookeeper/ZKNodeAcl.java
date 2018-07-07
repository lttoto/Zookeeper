package com.lt.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taoshiliu on 2018/7/6.
 */
public class ZKNodeAcl implements Watcher{

    private ZooKeeper zooKeeper = null;

    public static final String zkServerPath = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    public static final Integer timeout = 5000;

    public ZKNodeAcl() {}

    public ZKNodeAcl(String connectString) {
        try {
            zooKeeper = new ZooKeeper(connectString,timeout,new ZKNodeAcl());
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
            result = zooKeeper.create(path,data,acls, CreateMode.PERSISTENT);
            System.out.println("创建节点：\t" + result + "\t成功。。。");
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, KeeperException, InterruptedException {
        ZKNodeAcl zkServer = new ZKNodeAcl(zkServerPath);
        //world:anyone:cdrwa
        zkServer.creatZkNode("/testAcl","test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE);
        //自定义用户认证访问
        List<ACL> acls = new ArrayList<ACL>();
        Id imooc1 = new Id("digest",AclUtils.getDigestUserPwd("imooc1:123456"));
        Id imooc2 = new Id("digest",AclUtils.getDigestUserPwd("imooc2:123456"));
        acls.add(new ACL(ZooDefs.Perms.ALL,imooc1));
        acls.add(new ACL(ZooDefs.Perms.READ,imooc2));
        acls.add(new ACL(ZooDefs.Perms.DELETE | ZooDefs.Perms.CREATE,imooc2));
        zkServer.creatZkNode("/aclimooc/testdigest","testdigest".getBytes(),acls);
        //使用特定的用户创建
        zkServer.getZooKeeper().addAuthInfo("digest","imooc1:123456".getBytes());
        zkServer.creatZkNode("/aclimooc/testdigest/childtest","childtest".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL);

        Stat stat = new Stat();
        byte[] data = zkServer.getZooKeeper().getData("/aclimooc/testdigest",false,stat);
        System.out.println(new String(data));

        //IP的acl
        List<ACL> aclsIP = new ArrayList<ACL>();
        Id ipId1 = new Id("ip","127.0.0.1");
        aclsIP.add(new ACL(ZooDefs.Perms.ALL,ipId1));
        zkServer.creatZkNode("/aclimooc/iptest","iptest".getBytes(),aclsIP);

        zkServer.getZooKeeper().setData("/aclimooc/iptest","123456".getBytes(),0);
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
