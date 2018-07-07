package com.lt.zookeeper.monitorExample;

import java.util.concurrent.CountDownLatch;

/**
 * Created by taoshiliu on 2018/7/6.
 */
public abstract class DangerCenter implements Runnable{

    private CountDownLatch countDownLatch;
    private String station;
    private boolean ok;

    public DangerCenter(CountDownLatch countDownLatch, String station) {
        this.countDownLatch = countDownLatch;
        this.station = station;
    }

    public void run() {
        try {
            check();
            ok = true;
        }catch (Exception e) {
            e.printStackTrace();
            ok = false;
        }finally {
            if(countDownLatch != null) {
                countDownLatch.countDown();
            }
        }
    }

    public abstract void check();

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
