package com.lt.zookeeper.checkConfigExample;

import com.lt.zookeeper.checkConfigExample.util.JsonUtils;
import com.lt.zookeeper.checkConfigExample.util.RedisConfig;
import com.sun.xml.internal.ws.util.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;

import java.util.concurrent.CountDownLatch;

/**
 * Created by taoshiliu on 2018/7/7.
 */
public class Client1 {

    public CuratorFramework client = null;
    public static final String zkServerPath = "localhost:2181";

    public Client1() {
        RetryPolicy retryPolicy = new RetryNTimes(3,5000);
        client = CuratorFrameworkFactory.builder()
                .connectString(zkServerPath)
                .sessionTimeoutMs(10000).retryPolicy(retryPolicy)
                .namespace("workspace").build();
        client.start();
    }

    public void closeZKClinet() {
        if(client != null) {
            this.client.close();
        }
    }

    public final static String CONFIG_NODE_PATH = "/super/imooc";
    public final static String SUB_PATH = "redis_config";

    public static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        Client1 cto = new Client1();
        System.out.println("start");

        final PathChildrenCache childrenCache = new PathChildrenCache(cto.client,CONFIG_NODE_PATH,true);
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);

        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {

                if(pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
                    String configNodePath = pathChildrenCacheEvent.getData().getPath();
                    if(configNodePath.equals(CONFIG_NODE_PATH + SUB_PATH)) {
                        System.out.println("config path change " + configNodePath);
                        String jsonConfig = new String(pathChildrenCacheEvent.getData().getData());
                        System.out.println("jsonConfig = " + jsonConfig);

                        RedisConfig redisConfig = null;
                        if(null != jsonConfig || !"".equals(jsonConfig)) {
                            //redisConfig = JsonUtils.jsonToPojo(jsonConfig,RedisConfig.class);
                        }

                        if(redisConfig != null) {
                            String type = redisConfig.getType();
                            String url = redisConfig.getUrl();
                            String remark = redisConfig.getRemark();

                            if(type.equals("add")) {
                                System.out.println("add event");
                                Thread.currentThread().sleep(500);
                                System.out.println("add event path =" + url);
                                Thread.currentThread().sleep(1000);
                                System.out.println("success");
                            }else if(type.equals("update")) {
                                System.out.println("update event");
                                Thread.currentThread().sleep(500);
                                System.out.println("update event path =" + url);
                                Thread.currentThread().sleep(1000);
                                System.out.println("success");
                            }else if(type.equals("delete")) {
                                System.out.println("delete event");
                                Thread.currentThread().sleep(500);
                                System.out.println("delete event path =" + url);
                                Thread.currentThread().sleep(1000);
                                System.out.println("success");
                            }
                        }
                    }
                }
            }
        });
    }
}
