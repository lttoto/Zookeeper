package com.lt.zookeeper;

import org.apache.zookeeper.AsyncCallback;

/**
 * Created by taoshiliu on 2018/7/6.
 */
public class DeleteCallBack implements AsyncCallback.VoidCallback {
    public void processResult(int i, String s, Object o) {
        System.out.println("删除节点：" + s);
        System.out.println((String)o);
    }
}
