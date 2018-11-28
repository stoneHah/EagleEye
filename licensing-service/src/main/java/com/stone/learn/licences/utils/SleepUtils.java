package com.stone.learn.licences.utils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/10/25
 **/
public class SleepUtils {
    public static void second(long seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }

    /**
     * 1/3 的概率睡眠seconds指定的秒数
     * @param seconds
     */
    public static void randomSleep(long seconds){
        Random random = new Random();
        int randomNum = random.nextInt(3) + 1;
        if (randomNum == 3) {
            second(seconds);
        }
    }
}
