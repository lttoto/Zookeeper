package com.lt.zookeeper;

import org.apache.zookeeper.AsyncCallback;

import java.net.SocketPermission;

/**
 * Created by taoshiliu on 2018/7/6.
 */
public class CreateCallBack implements AsyncCallback.StringCallback{
    public void processResult(int i, String s, Object o, String s1) {
        System.out.println("创建节点：" + s);
        System.out.println((String)o);
    }
}
