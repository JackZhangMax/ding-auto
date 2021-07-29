package com.zml.dingauto.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;

/**
 * @author zhangmingliang
 * @version 1.0
 * @date 2021/6/15 10:10
 */
@Slf4j
@Configuration
@EnableScheduling
public class AutoTimer {

    @Resource
    private AutoService autoService;

    @Scheduled(cron = "0 20 9 * * *")
    public void startTimer () throws InterruptedException {
        log.info("startTimer start!");
        if (!getWorkingDay()){
            log.info("老子今天不上班");
            return;
        }
        Random r = new Random();
        long sleepTime = r.nextInt(420 - 10 + 1) + 10;
        log.info("startTimer sleepTime = {}", sleepTime);
        // 结束进程通知
        LocalDateTime dateTime = LocalDateTime.now();
        dateTime.plusSeconds(sleepTime).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try {
            HttpUtil.get("https://api.day.app/w8LtxK8JtqnF6LoyJrALg8/打卡任务初始化/预计在" + dateTime.plusSeconds(sleepTime).format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "开始");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread.sleep(sleepTime * 1000);
        autoService.start();
        log.info("startTimer end!");
    }

    @Scheduled(cron = "0 30 18 * * *")
    public void offWorkTimer () throws InterruptedException {
        log.info("offWorkTimer start!");
        if (!getWorkingDay()){
            log.info("老子今天不上班");
            return;
        }
        Random r = new Random();
        long sleepTime = r.nextInt(1800 - 10 + 1) + 10;
        log.info("offWorkTimer sleepTime = {}", sleepTime);
        // 结束进程通知
        LocalDateTime dateTime = LocalDateTime.now();
        dateTime.plusSeconds(sleepTime).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try {
            HttpUtil.get("https://api.day.app/w8LtxK8JtqnF6LoyJrALg8/打卡任务初始化/预计在" + dateTime.plusSeconds(sleepTime).format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "开始");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread.sleep(sleepTime * 1000);
        autoService.start();
        log.info("offWorkTimer end!");
    }

    /**
     * 获取今天是不是工作日
     * @return
     */
    public static Boolean getWorkingDay(){
        // 结束进程通知
        LocalDateTime dateTime = LocalDateTime.now();
        String dateTimeStr = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try {
            String resp = HttpUtil.get("http://timor.tech/api/holiday/info/" + dateTimeStr);
            TimorResp timorResp = JSONUtil.toBean(resp, TimorResp.class);
            if (Arrays.asList(0,3).contains(timorResp.getType().getType())){
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
            return true;
        }
        return false;
    }
}
