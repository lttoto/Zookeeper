package com.lt.zookeeper.curator;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

/**
 * Created by taoshiliu on 2018/7/7.
 */
public class MyCuratorWathcer implements CuratorWatcher {
    public void process(WatchedEvent watchedEvent) throws Exception {
        System.out.println("active watcher path " + watchedEvent.getPath());
    }
}
