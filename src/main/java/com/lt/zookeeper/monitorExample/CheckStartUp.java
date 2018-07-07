package com.lt.zookeeper.monitorExample;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by taoshiliu on 2018/7/6.
 */
public class CheckStartUp {

    private static List<DangerCenter> stationList;
    private static CountDownLatch countDownLatch;

    public CheckStartUp() {
    }

    public static boolean checkAllStations() throws InterruptedException {
        countDownLatch = new CountDownLatch(3);

        stationList = new ArrayList<DangerCenter>();
        stationList.add(new StationBeijing(countDownLatch));
        stationList.add(new StationChangchun(countDownLatch));
        stationList.add(new StationSanling(countDownLatch));

        Executor executor = Executors.newFixedThreadPool(stationList.size());

        for(DangerCenter center : stationList) {
            executor.execute(center);
        }

        countDownLatch.await();

        for(DangerCenter dangerCenter : stationList) {
            if(!dangerCenter.isOk()) {
                return false;
            }
        }

        return true;

    }

    public static void main(String[] args) throws InterruptedException {
        boolean result = CheckStartUp.checkAllStations();
        System.out.println("结果为：" + result);
    }

}
