package com.lt.zookeeper;

import org.apache.zookeeper.AsyncCallback;

import java.util.List;

/**
 * Created by taoshiliu on 2018/7/6.
 */
public class ChildrenCallback implements AsyncCallback.ChildrenCallback {
    public void processResult(int i, String s, Object o, List<String> list) {
        for(String str : list) {
            System.out.println(str);
        }
        System.out.println("ChildrenCallback:" + s);
        System.out.println((String) o);
    }
}
