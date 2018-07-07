package com.lt.zookeeper.curator;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * Created by taoshiliu on 2018/7/7.
 */
public class MyWatcher implements Watcher {
    public void process(WatchedEvent watchedEvent) {
        System.out.println("active watcher path " + watchedEvent.getPath());
    }
}
