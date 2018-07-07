package com.lt.zookeeper.monitorExample;

import java.util.concurrent.CountDownLatch;

/**
 * Created by taoshiliu on 2018/7/6.
 */
public class StationSanling extends DangerCenter {

    public StationSanling(CountDownLatch countDownLatch) {
        super(countDownLatch, "sanling");
    }

    @Override
    public void check() {
        System.out.println("正在检查【" + this.getStation() + "】。。。");

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("检查【" + this.getStation() + "完毕，可以发车");
    }
}
