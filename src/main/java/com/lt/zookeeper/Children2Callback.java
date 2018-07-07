package com.lt.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Created by taoshiliu on 2018/7/6.
 */
public class Children2Callback implements AsyncCallback.Children2Callback {
    public void processResult(int i, String s, Object o, List<String> list, Stat stat) {
        for(String str : list) {
            System.out.println(str);
        }
        System.out.println("ChildrenCallback:" + s);
        System.out.println((String) o);
        System.out.println(stat.toString());
    }
}
