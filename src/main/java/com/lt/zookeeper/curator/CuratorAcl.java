package com.lt.zookeeper.curator;

import com.lt.zookeeper.AclUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taoshiliu on 2018/7/7.
 */
public class CuratorAcl {

    public CuratorFramework client = null;
    public static final String zkServerPath = "localhost:2181";

    public CuratorAcl() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,5);

        client = CuratorFrameworkFactory.builder().authorization("digest","imooc1:123456".getBytes())
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

        String nodePath = "/acl/imooc";

        List<ACL> acls = new ArrayList<ACL>();
        Id imooc1 = new Id("digest", AclUtils.getDigestUserPwd("imooc1:123456"));
        Id imooc2 = new Id("digest",AclUtils.getDigestUserPwd("imooc2:123456"));
        acls.add(new ACL(ZooDefs.Perms.ALL,imooc1));
        acls.add(new ACL(ZooDefs.Perms.READ,imooc2));
        acls.add(new ACL(ZooDefs.Perms.DELETE | ZooDefs.Perms.CREATE,imooc2));

        byte[] data = "spiderman".getBytes();
        cto.client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE,true)
                .forPath(nodePath,data);

        cto.client.setACL().withACL(acls).forPath("/curatorNode");
    }
}
